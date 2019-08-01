package top.zigaoliang;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * @ClassName HBasePut
 * @Author hanlin
 * @Date 2019/7/31 14:05
 **/
public class HBasePut {

    @Test
    public void putList() throws Exception{
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        System.setProperty("HADOOP_USER_NAME","hbase");
        Connection conn = ConnectionFactory.createConnection(config);
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf("t1"))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf("t1"));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
        Table table = conn.getTable(TableName.valueOf("t1"));

        long start = System.currentTimeMillis();
        for(int i=0;i<100000;i++) {
            Put put = new Put(Bytes.toBytes("r" + i));
            //可以在一行中，同时添加多个列 。一个put对象可以提交同一行的多个单元格，就是列
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("name"), Bytes.toBytes("name"+i));
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("id"), Bytes.toBytes(i));
            int age = (int)(Math.random() * 10 + 10);
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("age"), Bytes.toBytes(String.valueOf(age)));
            table.put(put);//单个写入
            System.out.println((i/100000.0 * 100) + "%");
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时(秒)：" + (end - start /1000));

    }

    @Test
    public void scan() throws Exception{
        long start = System.currentTimeMillis();
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        System.setProperty("HADOOP_USER_NAME","hbase");
        Connection conn = ConnectionFactory.createConnection(config);
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf("t1"))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf("t1"));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
        Table table = conn.getTable(TableName.valueOf("t1"));

        Scan scan = new Scan();
        scan.setFilter(new KeyOnlyFilter());
        Iterable<Result> results=table.getScanner(scan);
        Iterator<Result> it=results.iterator();
        List<String> rowList = new ArrayList();
        while (it.hasNext()){
            Result result=it.next();
            rowList.add(Bytes.toString(result.getRow()));
        }
        long end = System.currentTimeMillis();
        System.out.println("查询记录数"+ rowList.size() +"耗时(秒)：" + (end - start) );

    }

}
