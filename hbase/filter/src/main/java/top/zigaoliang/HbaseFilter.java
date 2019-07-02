package top.zigaoliang;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueExcludeFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName HbaseFilter
 * @Author hanlin
 * @Date 2019/4/29 14:24
 **/
public class HbaseFilter {

    private static Table table = null;

    public static void main(String[] args) throws Exception {
        table = Common.init();
        put();
        scan();
    }

    //批量写入测试数据
    public static void put() throws Exception {
        for(int i=0;i<10000;i++){
            Put put = new Put(Bytes.toBytes("row"+i));
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("name"), Bytes.toBytes("tom"+i));
            put.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("age"), Bytes.toBytes(""+(int)(Math.random()*20)));
            table.put(put);//单个写入
        }

    }
    //输出查询结果
    public static void outCell(Result result){
        Cell[] cells = result.rawCells();
        for(Cell cell:cells){
            System.out.println("行："+Bytes.toString(CellUtil.cloneRow(cell))+
                    " ,列族："+Bytes.toString(CellUtil.cloneFamily(cell))+
                    " ,列："+Bytes.toString(CellUtil.cloneQualifier(cell))+
                    " ,值："+Bytes.toString(CellUtil.cloneValue(cell)));
        }
    }

    public static void scanTest(Filter filter) throws Exception{
        Scan scan = new Scan();
        scan.setFilter(filter);
        Iterable<Result> results=table.getScanner(scan);
        Iterator<Result> it=results.iterator();
        while (it.hasNext()){
            Result result=it.next();
            outCell(result);
        }
    }
    public static void get() throws Exception{
        Get get = new Get(Bytes.toBytes("row2"));
        //可选项，不指定列，则查所有列
        get.addColumn(Bytes.toBytes("f1"),Bytes.toBytes("name"));
        Result res = table.get(get);
        outCell(res);
    }

    // 1、行键过滤器 RowFilter  get请求是需要指定行的，所以用不上这个
    public static void scan() throws Exception{
        //行键 大于row8 的行
//        Filter rowFilter = new RowFilter(CompareFilter.CompareOp.GREATER, new BinaryComparator("row8".getBytes()));

        //行建大于等于 row9 的行
        Filter rowFilter = new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator("row9".getBytes()));
//        scanTest(rowFilter);

        // 2、列簇过滤器 FamilyFilter
        //列族等于f1
//        Filter familyFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator("f1".getBytes()));

        //列族不等于 f1
//        Filter familyFilter = new FamilyFilter(CompareFilter.CompareOp.NOT_EQUAL, new BinaryComparator("f1".getBytes()));

        //列族小于f2
        Filter familyFilter = new FamilyFilter(CompareFilter.CompareOp.LESS, new BinaryComparator("f2".getBytes()));

        //3、列过滤器 QualifierFilter

        //只查询列名等于name
        Filter qualifierFilter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator("name".getBytes()));


        //4、值过滤器 ValueFilter
        //值 包含10的
//        Filter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator("10"));

        //值等于10
        Filter valueFilter = new ValueFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("10")));

        // 5、时间戳过滤器 TimestampsFilter
        List<Long> list = new ArrayList<Long>();
        list.add(1557125253325L);
        list.add(1557125253306L);
        TimestampsFilter timestampsFilter = new TimestampsFilter(list);


        //=======================专用过滤器========================

        //1、单列值过滤器 SingleColumnValueFilter ----会返回满足条件的整行
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                "f1".getBytes(), //列簇
                "name".getBytes(), //列
                CompareFilter.CompareOp.EQUAL,
                new SubstringComparator("tom9946"));
    //如果不设置为 true，则那些不包含指定 column 的行也会返回
        //如果不设置为true则没有这个列族，或者没有这个列的行，数据也会被返回
        singleColumnValueFilter.setFilterIfMissing(true);

        //2、单列值排除器 SingleColumnValueExcludeFilter
        //查询符合条件的行，但不包含该列的数据
        SingleColumnValueExcludeFilter singleColumnValueExcludeFilter = new SingleColumnValueExcludeFilter(
                "f1".getBytes(),
                "name".getBytes(),
                CompareFilter.CompareOp.EQUAL,
                new SubstringComparator("tom9946"));
        singleColumnValueExcludeFilter.setFilterIfMissing(true);

        //3、前缀过滤器 PrefixFilter----针对行键
        PrefixFilter prefixFilter = new PrefixFilter("row994".getBytes());

        //4、列前缀过滤器 ColumnPrefixFilter
//        ColumnPrefixFilter columnPrefixFilter = new ColumnPrefixFilter("name".getBytes());
        ColumnPrefixFilter columnPrefixFilter = new ColumnPrefixFilter("nam".getBytes());

        //5、分页过滤器 PageFilter
        PageFilter pageFilter = new PageFilter(2);

        //键过滤器 KeyOnlyFilter 只包含键默认不包含值，可以传参数true将值的长度作为值返回
        KeyOnlyFilter keyOnlyFilter = new KeyOnlyFilter();

        //基于列分页过滤器.对列进行分页limit列的数量，offset列的偏移量，下面是只返回一个列，是第二列
        ColumnPaginationFilter columnPaginationFilter =new ColumnPaginationFilter(1,1);

        scanTest(pageFilter);
    }

}
