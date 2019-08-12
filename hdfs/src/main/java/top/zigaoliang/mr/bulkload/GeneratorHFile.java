package top.zigaoliang.mr.bulkload;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * @Author hanlin
 * @Date 2019/7/24 10:38
 * @Description  导入100万的数据到hbase，尝试使用这种方式，看能不能快些。
 *              将hdfs上的数据文件，通过mr直接写为hfile文件，然后
 *              导入到hbase，这样的数据输入会比通过命令进行输入速度上要快很多。
 *              适合批量导入大量数据。
 *
 *         使用方式：将此类打包为jar，然后放到服务器上面（不是hdfs上），使用任务的方式执行此类。
 *             命令：hadoop jar hdfs.jar top.zigaoliang.mr.bulkload.GeneratorHFile2
 * @param
 * @return
 **/
public class GeneratorHFile {
    static class HFileImportMapper extends Mapper<LongWritable,Text,ImmutableBytesWritable,KeyValue> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            for( int i =1000000;i<10000000;i++) {
                ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes("r"+i));
                KeyValue kv1 = new KeyValue(Bytes.toBytes("r"+i), Bytes.toBytes("f1"), Bytes.toBytes("name"), Bytes.toBytes("name"+i));
                KeyValue kv2 = new KeyValue(Bytes.toBytes("r"+i), Bytes.toBytes("f1"), Bytes.toBytes("age"), Bytes.toBytes((int)(Math.random()*10+10)));
                context.write(rowkey, kv1);
                context.write(rowkey, kv2);
            }
        }

    }
    public static void main(String[] args) {

        Configuration conf = new Configuration();
//        conf.addResource("/soft/hbase/conf/hbase-site.xml");
        conf.set("hbase.fs.tmp.dir","partitions_"+UUID.randomUUID());
        conf.set("hbase.rootdir","hdfs://192.168.100.21:8020/hbase");
        conf.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        conf.set("zookeeper.znode.parent","/hbase");

        String tableName = "t1";
        String input = "hdfs://192.168.100.21:8020/tmp/person.txt";
        String output = "hdfs://192.168.100.21:8020/tmp/pers";
        System.out.println("table:" + tableName);
        HTable table;
        try {
            FileSystem fs = FileSystem.get(URI.create(output),conf);
            fs.delete(new Path(output),true);
            fs.close();

//            Connection conn = ConnectionFactory.createConnection(conf);
            table = new HTable(conf,tableName);

            Job job = Job.getInstance(conf);
            job.setJobName("Generate HFile");

            job.setJarByClass(GeneratorHFile.class);
            job.setInputFormatClass(TextInputFormat.class);
            job.setMapperClass(HFileImportMapper.class);
            FileInputFormat.setInputPaths(job,input);
            FileOutputFormat.setOutputPath(job,new Path(output));

//                HFileOutputFormat2.configureIncrementalLoad(job,table); //这个方法过时了，转为下面的方法
            HFileOutputFormat2.configureIncrementalLoad(job, table.getTableDescriptor(), table.getRegionLocator());

            job.waitForCompletion(true);

            System.out.println("文件已经转换为HFile文件，开始执行BulkLoad操作！");
            LoadIncrementalHFiles load = new LoadIncrementalHFiles(conf);
            load.doBulkLoad(new Path(output),table);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
