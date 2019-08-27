package top.zigaoliang;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.List;

/**
 * @ClassName ZKBase
 * @Author hanlin
 * @Date 2019/5/20 17:21
 **/
public class ZKBase {

    @Test
    public void ls() throws  Exception{
        ZooKeeper zk = new ZooKeeper("192.168.100.21:2181",5000,null);
        List<String> list = zk.getChildren("/",null);
        for(String s : list){
            System.out.println(s);
        }
    }
    @Test
    public void lsAll() throws  Exception{
        ls("/");
    }

    /**
     * 列出指定path下的孩子
     */
    public void ls(String path) throws  Exception{
        System.out.println(path);
        ZooKeeper zk = new ZooKeeper("192.168.100.21:2181",5000,null);
        List<String> list = zk.getChildren(path,null);
        if(list == null || list.isEmpty()){
            return ;
        }
        for(String s : list){
            //先输出孩子
            if(path.equals("/")){
                ls(path + s);
            }
            else{
                ls(path + "/" + s);
            }
        }
    }

    /**
     * 设置数据
     */
    @Test
    public void setData() throws Exception{
        ZooKeeper zk = new ZooKeeper("192.168.100.21:2181", 5000, null);
        zk.setData("/a","tomaslee".getBytes(),0);
    }

    /**
     * 判断节点存在
     */
    @Test
    public void exist() throws Exception{
        ZooKeeper zk = new ZooKeeper("192.168.100.21:2181", 5000, null);
        Stat stat=zk.exists("/config",false);
        if(stat == null){
            System.out.println("节点不存在！");
        }else{
            System.out.println("节点存在！");
        }
    }

    /**
     * 创建临时节点
     */
    @Test
    public void reateEmphoral() throws Exception{
        ZooKeeper zk = new ZooKeeper("192.168.100.21:2181", 5000, null);
        zk.create( "/c/c1" ,"tom".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        System.out.println("hello");
    }

    @Test
    public void testWatch() throws Exception{
        final ZooKeeper zk = new ZooKeeper("192.168.100.21:2181", 5000, null);

        Stat st = new Stat();

        Watcher w = new Watcher() {
            //回调
            public void process(WatchedEvent event) {
                try {
                    System.out.println("数据改了！！！");
                    zk.getData("/king", this, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        byte[] data = zk.getData("/king", w , st);

        System.out.println(new String(data));

        while(true){
            Thread.sleep(1000);
        }
    }
}
