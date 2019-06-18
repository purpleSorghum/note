package top.zigaoliang.mr.wordcount;

import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @ClassName WordCountMapper
 * @Author hanlin
 * @Date 2019/6/18 10:53
 **/
public class WordCountMapper extends Mapper{
    @Override
    protected void map(Object key, Object value, Context context) throws IOException, InterruptedException {
        context.write(key,value);
    }
}
