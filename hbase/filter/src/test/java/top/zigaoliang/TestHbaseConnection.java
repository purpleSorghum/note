package top.zigaoliang;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;

import static org.apache.hadoop.fs.FileSystem.LOG;

/**
 * @ClassName TestHbaseConnection
 * @Author hanlin
 * @Date 2019/5/16 10:33
 **/
public class TestHbaseConnection {

    @Test
    public void test() throws Exception {
        Boolean hbaseStatus1 = false;
        Admin admin = null;
        try {
            LOG.info("getHBaseStatus: creating default Hbase configuration");

            LOG.info("getHBaseStatus: setting config values from client");
            LOG.info("getHBaseStatus: checking HbaseAvailability with the new config");
            admin = ConnectionFactory.createConnection(HBaseConfiguration.create()).getAdmin();
            LOG.info("getHBaseStatus: no exception: HbaseAvailability true");
            hbaseStatus1 = true;
        } catch (ZooKeeperConnectionException zce) {
            String msgDesc = "getHBaseStatus: Unable to connect to `ZooKeeper` "
                    + "using given config parameters.";
            LOG.error(msgDesc + zce);
            throw zce;

        } catch (MasterNotRunningException mnre) {
            String msgDesc = "getHBaseStatus: Looks like `Master` is not running, "
                    + "so couldn't check that running HBase is available or not, "
                    + "Please try again later.";
            LOG.error(msgDesc + mnre);
            throw mnre;

        } catch(IOException io) {
            String msgDesc = "getHBaseStatus: Unable to check availability.";

            LOG.error(msgDesc + io);
            throw io;

        } catch (Throwable e) {
            String msgDesc = "getHBaseStatus: Unable to check availability .";
            LOG.error(msgDesc + e);
            hbaseStatus1 = false;
            RuntimeException hdpException = new RuntimeException(msgDesc, e);
            throw hdpException;
        } finally {
            if (admin != null) {
                try {
                    admin.close();
                } catch (IOException e) {
                    LOG.error("Unable to close HBase connection []", e);
                }
            }
        }

        System.out.println(hbaseStatus1);
    }
}
