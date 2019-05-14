package top.zigaoliang.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @ClassName WCApp
 * @Author hanlin
 * @Date 2019/5/7 15:56
 **/
public class WCApp {
    public static void main(String[] args) throws Exception {
        Configuration conf=new Configuration();
        Job job = Job.getInstance(conf);

        job.setJobName("WCApp");
        job.setJarByClass(WCApp.class);
        job.setInputFormatClass(TextInputFormat.class);

        job.setMapperClass(WCMapper.class);
        job.setReducerClass(WCReducer.class);

        job.setNumReduceTasks(1);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        job.waitForCompletion(false);
    }
}