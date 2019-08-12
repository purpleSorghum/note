package top.zigaoliang;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.List;

/**
 * @ClassName HbaseSampleUtisTest
 * @Author hanlin
 * @Date 2019/8/7 17:07
 **/
public class HbaseSampleUtisTest {


    @Test
    public void testSample() throws Throwable{
        System.setProperty("HADOOP_USER_NAME","hbase");
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        TableName name = TableName.valueOf("t1");
//        long count = HbaseSampleUtil.useCoprocessorCount(name,config);
        List<Result> list = HbaseSampleUtil.sample(name,config,300);
        for(Result r : list){
            System.out.println(Bytes.toString(r.getRow()));
        }
        System.out.println("抽取总数（条）：" + list.size());
    }


    @Test
    public void testCount() throws Throwable{
        System.setProperty("HADOOP_USER_NAME","hbase");
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        TableName name = TableName.valueOf("t1");
        long start = System.currentTimeMillis();
//        long count = HbaseSampleUtil.useCoprocessorCount(name,config);
        long count = HbaseSampleUtil.useScanCount(name,config);
        long end = System.currentTimeMillis();
        System.out.println(count);
        System.out.println("共消耗时长（毫秒）："+(end - start));
    }


    @Test
    public void cleanCoprocessor() throws Throwable{
        System.setProperty("HADOOP_USER_NAME","hbase");
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        Connection conn = ConnectionFactory.createConnection(config);
        TableName name=TableName.valueOf("t1");
        Table table = conn.getTable(name);
        Admin admin = conn.getAdmin();

        String coprocess="org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
        HTableDescriptor desc = table.getTableDescriptor();
        if(desc.hasCoprocessor(coprocess))
            desc.removeCoprocessor(coprocess);
        admin.disableTable(name);
        admin.modifyTable(name,desc);
        admin.enableTable(name);
    }
}
