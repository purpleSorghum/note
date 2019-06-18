package top.zigaoliang.mr.wordcount;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import java.net.URI;

/**
 * @ClassName WordCountDriver
 * @Author hanlin
 * @Date 2019/6/18 10:56
 **/
public class WordCountDriver {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(WordCountDriver.class);
        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);

        String uri="hdfs://192.168.100.21:8020";
        Path inputPath= new Path(uri+"/wordcount");
        Path outputPath = new Path(uri + "/wordcount/result");

        FileSystem fs= FileSystem.get(new URI(uri),conf);
        fs.delete(outputPath,true);

        FileInputFormat.addInputPath(job,inputPath);
        FileOutputFormat.setOutputPath(job,outputPath);

        job.waitForCompletion(true);

        System.out.println("统计结果：");
        FileStatus[] fileStatuses = fs.listStatus(outputPath);
        for(int i = 0 ;i < fileStatuses.length;i++){
            System.out.println(fileStatuses[i].getPath());
            FSDataInputStream in = fs.open(fileStatuses[i].getPath());
            IOUtils.copyBytes(in,System.out,4096,false);
        }


    }
}
