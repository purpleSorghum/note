package top.zigaoliang.mr.bulkload;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @ClassName ReadHFile
 * 直接从HFile文件中读取出表的数据
 * @Author hanlin
 * @Date 2019/6/25 13:56
 **/
public class ReadHFile {
    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        String hfile = "hdfs://192.168.100.21:8020/hbase/data/default/t1/14cda0f7fd87e7a1a8fcec9818123b06/f1/1ebc82c8854b46538a8d9ebe4ea97017";

        HFile.Reader reader = HFile.createReader( FileSystem.get(conf),new Path(hfile),new CacheConfig(conf),conf);
        HFileScanner scanner = reader.getScanner(false,false);
        reader.loadFileInfo();
        scanner.seekTo();
        System.out.println("从HFile文件中读取到的数据：");
        do{
            Cell cell = scanner.getKeyValue();
            System.out.println("行号："+Bytes.toString(CellUtil.cloneRow(cell)) +
                    "  列族："+Bytes.toString(CellUtil.cloneFamily(cell)) +
                    "  列："+Bytes.toString(CellUtil.cloneQualifier(cell)) +
                    "  值："+Bytes.toString(CellUtil.cloneValue(cell)));
        }while (scanner.next());
    }
}
