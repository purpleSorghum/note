package top.zigaoliang.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

/**
 * @ClassName Test
 * @Author hanlin
 * @Date 2019/6/26 11:23
 **/
public class Test {
    private static final String KEYTAB_FILE = ClassLoader.getSystemResource("kerberos/user.keytab").getPath();
    private static final String KEYTAB_CONF = ClassLoader.getSystemResource("kerberos/krb5.conf").getPath();
    private static Configuration conf=null;

    static {
        System.setProperty("java.security.krb5.conf",KEYTAB_CONF);
        conf=HBaseConfiguration.create();
        conf.set("hadoop.security.authentication","Kerberos");
        conf.set("keytab.file",KEYTAB_FILE);
        conf.set("kerberos.principal","user/hadoop.hadoop.com@HADOOP.COM");
        conf.set("hbase.master.kerberos.principal","user/hadoop.hadoop.com@HADOOP.COM");
        conf.set("hbase.regionserver.kerberos.principal","user/hadoop.hadoop.com@HADOOP.COM");
        conf.set("hbase.zookeeper.quorum","192.168.1.61");
        conf.set("hbase.zookeeper.property.clientPort","24001");
        conf.set("hbase.security.authentication","kerberos");

        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab("hbasehd/hadoop.hadoop.com@HADOOP.COM",KEYTAB_FILE);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Connection conn = ConnectionFactory.createConnection(conf);
        System.out.println("conn : "+conn);
        Admin admin = conn.getAdmin();
        System.out.println("admin :"+admin);
        if(!admin.tableExists(TableName.valueOf("t2"))){
            HTableDescriptor htable=new HTableDescriptor(TableName.valueOf("t2"));
            htable.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(htable);
        }
    }

}
