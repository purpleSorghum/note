package top.zigaoliang.mr.wordcount;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @ClassName WordCountMapper
 * @Author hanlin
 * @Date 2019/6/18 10:53
 **/
public class WordCountMapper extends Mapper<LongWritable,Text,LongWritable,Text>{
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(key,value);
    }
}
