package top.zigaoliang;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * @ClassName Common
 * @Author hanlin
 * @Date 2019/5/6 11:49
 **/
public class Common {
    public static Connection conn = null;
    public static  Admin admin = null;
    // 初始化
    public static Table init() throws Exception{
        Configuration config = HBaseConfiguration.create();
        conn = ConnectionFactory.createConnection(config);
        System.out.println("conn : "+conn);
        admin = conn.getAdmin();
        System.out.println("admin :"+admin);
        if(!admin.tableExists(TableName.valueOf("t2"))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf("t2"));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
        return conn.getTable(TableName.valueOf("t2"));
    }

    public static void destroy(){
        if(admin != null){
            try {
                admin.close();
                System.out.println("admin close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(conn != null ){
            try {
                conn.close();
                System.out.println("conn close");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
