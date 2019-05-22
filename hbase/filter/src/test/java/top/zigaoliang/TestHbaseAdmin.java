package top.zigaoliang;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @ClassName TestHbaseAdmin
 * @Author hanlin
 * @Date 2019/5/20 14:43
 **/
public class TestHbaseAdmin {

    @Test
    public void TestTruncate() throws IOException {
        Admin admin = ConnectionFactory.createConnection(HBaseConfiguration.create()).getAdmin();
        TableName tableName=TableName.valueOf("t6");
        admin.truncateTable(tableName,false);
    }

}
