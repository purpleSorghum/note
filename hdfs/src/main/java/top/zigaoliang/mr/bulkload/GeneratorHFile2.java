package top.zigaoliang.mr.bulkload;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * @ClassName GeneratorHFile2
 * @Author hanlin
 * @Date 2019/6/21 16:35
 **/
public class GeneratorHFile2 {
    static class HFileImportMapper2 extends Mapper<LongWritable,Text,ImmutableBytesWritable,KeyValue> {

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            System.out.println("line：" + line);
            String[] datas = line.split(" ");
            String row = "r" + datas[0];
            ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes("row"));
            KeyValue kv = new KeyValue(Bytes.toBytes(row),Bytes.toBytes("f1"),Bytes.toBytes(datas[1]),Bytes.toBytes(datas[2]));
            context.write(rowkey,kv);
        }

    }
    public static void main(String[] args) {

        Configuration conf = new Configuration();
//        conf.addResource("/soft/hbase/conf/hbase-site.xml");
        conf.set("hbase.fs.tmp.dir","partitions_"+UUID.randomUUID());
        conf.set("hbase.rootdir","hdfs://192.168.100.21:8020/hbase");
        conf.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        conf.set("zookeeper.znode.parent","/hbase");

        String tableName = "person";
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

            job.setJarByClass(GeneratorHFile2.class);
            job.setInputFormatClass(TextInputFormat.class);
            job.setMapperClass(HFileImportMapper2.class);
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
