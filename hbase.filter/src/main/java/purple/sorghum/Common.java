package purple.sorghum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

/**
 * @ClassName Common
 * @Author hanlin
 * @Date 2019/5/6 11:49
 **/
public class Common {
    // 初始化
    public static Table init() throws Exception{
        Configuration config = HBaseConfiguration.create();
        Connection conn=ConnectionFactory.createConnection(config);
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf("t2"))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf("t2"));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
        return conn.getTable(TableName.valueOf("t2"));
    }
}
