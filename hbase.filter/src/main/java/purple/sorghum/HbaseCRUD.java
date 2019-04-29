package purple.sorghum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HbaseCRUD
 * @Author hanlin
 * @Date 2019/4/29 14:24
 **/
public class HbaseCRUD {

    private static Table table = null;

    public static void main(String[] args) throws Exception {
        init();
        put();
        get();
        append();
        delete();
        get();
    }

    // 初始化
    private static void init() throws Exception{
        Configuration config = HBaseConfiguration.create();
        Connection conn=ConnectionFactory.createConnection(config);
        Admin admin = conn.getAdmin();
        if(!admin.tableExists(TableName.valueOf("t2"))){
            HTableDescriptor table=new HTableDescriptor(TableName.valueOf("t2"));
            table.addFamily(new HColumnDescriptor("f1"));
            admin.createTable(table);
        }
        table=conn.getTable(TableName.valueOf("t2"));
    }

    public static void put() throws Exception{
        Put put=new Put(Bytes.toBytes("row1"));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("tom"));
        table.put(put);//单个写入
        List<Put> list= new ArrayList<Put>();

        Put put1 = new Put(Bytes.toBytes("row2"));
        put1.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("tomas"));
        list.add(put1);

        Put put2 = new Put(Bytes.toBytes("row2"));
        put2.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("id"),Bytes.toBytes("3"));
        list.add(put2);

        Put put3 = new Put(Bytes.toBytes("row2"));
        put3.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("age"),Bytes.toBytes(18));
        list.add(put3);

        table.put(list);//批量写入
        System.out.println("put success !");
    }

    public static void get() throws Exception{
        Get get = new Get(Bytes.toBytes("row2"));

        //可选项，不指定列，则查所有列
        get.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"));
        Result res = table.get(get);
        Cell[] cells = res.rawCells();
        for(Cell cell:cells){
            System.out.println("行："+Bytes.toString(CellUtil.cloneRow(cell))+
                    " ,列族："+Bytes.toString(CellUtil.cloneFamily(cell))+
                    " ,列："+Bytes.toString(CellUtil.cloneQualifier(cell))+
                    " ,值："+Bytes.toString(CellUtil.cloneValue(cell)));
        }
    }

    public static void append() throws  Exception{
        Append append = new Append(Bytes.toBytes("row2"));
        append.add(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("li"));
        table.append(append);
        System.out.println("append success !");
    }

    public static void delete() throws Exception{
        Delete delete=new Delete(Bytes.toBytes("row2"));
        delete.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"));
        table.delete(delete);
        System.out.println("delete success !");
    }

    public static void scan(){
        Scan scan = new Scan();
    }

}
