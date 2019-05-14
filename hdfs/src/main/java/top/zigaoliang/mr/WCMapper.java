package top.zigaoliang.mr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @ClassName WCMapper
 * @Author hanlin
 * @Date 2019/5/7 15:28
 **/
public class WCMapper extends Mapper<LongWritable,Text,Text,IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Text keyOut=new Text();
        IntWritable valueOut= new IntWritable();
        String[] arr=value.toString().split(" ");
        for(String str : arr){
            keyOut.set(str);
            valueOut.set(1);
            context.write(keyOut,valueOut);
        }
    }
}
