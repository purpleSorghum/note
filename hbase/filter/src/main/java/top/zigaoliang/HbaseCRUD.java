package top.zigaoliang;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName HbaseCRUD
 * @Author hanlin
 * @Date 2019/4/29 14:24
 **/
public class HbaseCRUD {

    private static Table table = null;

    public static void main(String[] args) throws Exception {
        table = Common.init();
//        put();
//        get();
//        append();
//        delete();
//        get();
        scan();
        Common.destroy();
    }


    public static void put() throws Exception{
        Put put=new Put(Bytes.toBytes("row1"));
        //可以在一行中，同时添加多个列 。一个put对象可以提交同一行的多个单元格，就是列
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"),Bytes.toBytes("tom"));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("id"),Bytes.toBytes("1"));
        put.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("phone"),Bytes.toBytes("13044445555"));
        table.put(put);//单个写入

        //可以使用htable的put list 的方法异步提交多个行，但是不保证整体的元子性，只保证一行的元子性。
        //如果部分行提交失败，可以重新提交就可以了，新的数据会覆盖原来的数据，所以没有元子性可以
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

    public static void scan() throws Exception{
        Scan scan = new Scan();

        scan.setBatch(3);//限制一个results的最大列数
        scan.setCaching(5);//限制每次请求的results的数量
//        scan.setLimit(10); //限制每次请求的行数，在2.0以后的版本中才存在
//        scan.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("phone"));
//        scan.addFamily(Bytes.toBytes("f2"));
        Iterable<Result> results=table.getScanner(scan);
        Iterator<Result> it=results.iterator();
        while (it.hasNext()){
            Result result=it.next();
            Cell[] cells=result.rawCells();
            for(Cell cell:cells){
                System.out.println("行："+Bytes.toString(CellUtil.cloneRow(cell))+
                        " ,列族："+Bytes.toString(CellUtil.cloneFamily(cell))+
                        " ,列："+Bytes.toString(CellUtil.cloneQualifier(cell))+
                        " ,值："+Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

}
