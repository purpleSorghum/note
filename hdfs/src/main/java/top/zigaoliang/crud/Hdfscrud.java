package top.zigaoliang.crud;


//import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;


import java.io.FileOutputStream;
import java.net.URI;


/**
 * @ClassName Hdfscrud
 * @Author hanlin
 * @Date 2019/5/14 17:16
 * hdfs 的基本操作，上传下载之类
 * hdfs 设置在java操作中指定用户名的几种方式
 * System.setProperty("HADOOP_USER_NAME","root");
 * UserGroupInformation
 **/
public class Hdfscrud {

    FileSystem fs = getFS();

    public Hdfscrud() throws Exception {
    }

    @Test
    //上传文件到hdfs
    public void put() throws Exception{

//        FileInputStream fis = new FileInputStream("D:\\RELEASE_NOTES.txt");
//        FSDataOutputStream fos = getFS().create(new Path("/tmp/test.txt"));
//        IOUtils.copy(fis,fos);
        //或者这种
        fs.copyFromLocalFile(new Path("D:\\RELEASE_NOTES.txt"),new Path("/tmp/test.txt"));
        fs.close();
    }

    @Test
    public void get() throws Exception{
        //这个可能是系统兼容性问题
//        fs.copyToLocalFile(new Path("/tmp/test.txt"),new Path("D:\\test.txt"));
//        fs.copyToLocalFile(false,new Path("/tmp/test.txt"),new Path("D:\\test.txt"),true);
        //还可以使用以下方式
        FileOutputStream fos = new FileOutputStream("D:\\test.txt");
        FSDataInputStream is = fs.open(new Path("/tmp/test.txt"));
//        IOUtils.copy(is,fos);// org.apache.commons.io.IOUtils 使用这个
        //或者
        IOUtils.copyBytes(is,fos,2048,true);
        fs.close();
    }
    @Test
    //删除
    public void delete() throws Exception{
        fs.delete(new Path("/tmp/test.txt"),true);
        fs.close();
    }

    @Test
    //创建加密区,需要配置并启动kms，暂时忽略
    public void createEncryptionZone() throws Exception{
        System.setProperty("HADOOP_USER_NAME","root");
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://192.168.100.21:8020/");
        HdfsAdmin admin = new HdfsAdmin(new URI("hdfs://192.168.100.21:8020/"),conf);
        admin.createEncryptionZone(new Path("/path"),"myKey");

    }

    private FileSystem getFS() throws Exception{
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://192.168.100.21:8020/");
        System.setProperty("HADOOP_USER_NAME","root");
//        UserGroupInformation userGrout = UserGroupInformation.createRemoteUser("lin");
//        UserGroupInformation.setLoginUser(userGrout);
        FileSystem fs = FileSystem.get(conf);
        return fs;
    }
}
