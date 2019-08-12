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
import org.apache.hadoop.security.UserGroupInformation;

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
    public static Table init(String table) throws Exception{
        UserGroupInformation userGrout = UserGroupInformation.createRemoteUser("hbase");
        UserGroupInformation.setLoginUser(userGrout);
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum","192.168.100.21:2181");
        conn = ConnectionFactory.createConnection(config);
        admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf(table))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf(table));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
        return conn.getTable(TableName.valueOf(table));
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
