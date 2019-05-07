package purple.sorghum;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @ClassName WCReducer
 * @Author hanlin
 * @Date 2019/5/7 15:49
 **/
public class WCReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for(IntWritable iw : values){
            count+=iw.get();
        }
        context.write(key,new IntWritable(count));
    }
}
