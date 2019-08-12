package top.zigaoliang;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;


import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName HbaseSampleUtil
 * @Author hanlin
 * @Date 2019/8/7 13:44
 **/
public class HbaseSampleUtil {
    //分页大小，通过这个计算每页里面取得的样品数量
    public static int pageSize=100;
    public static final Logger LOG = Logger.getLogger(HbaseSampleUtil.class);

    /**
     * @Author hanlin
     * @Date 2019/8/8 10:57
     * @Description 对hbase进行集群采样，通过总数和样品数量及分页大小，计算每页采集的
     *              数据量，每页采集部分数据，返回最后的采集样品结果
     * @param name 需要采集样品的表名
     * @param config 集群的配置信息
     * @param number 想要采集的数量，如果数量大于表的总数，则返回所有数据
     * @return java.util.List<org.apache.hadoop.hbase.client.Result> 样品数据
     **/
    public static List<Result> sample(TableName name,Configuration config,int number) throws Exception{
        List<Result> results = new ArrayList<>();
        List<Result> subList = new ArrayList<>();
        long count = useScanCount(name,config);
        int page,pageNumber;
        do {
            //如果每页取的数量不足1条，页面大小扩大10倍
            pageSize *= 10;
            page = Math.round((float) count / pageSize);
            if (page == 0) page = 1;
            pageNumber = number / page;
        }while ((pageNumber < 1));
        LOG.info("table count is :" + count + ", page is : "+ page + ", pageNumber is :" +pageNumber);
        Connection conn = ConnectionFactory.createConnection(config);
        Table table = conn.getTable(name);
        PageFilter filter = new PageFilter(pageSize +1);
        byte[] startRow = new byte[0];
        do {
            Scan scan = new Scan(startRow, filter);
            ResultScanner rs = table.getScanner(scan);

            for (Result res : rs) {
                subList.add(res);
            }

            Result lastResult = subList.get(subList.size() - 1);
            startRow = lastResult.getRow();
            subList.remove(lastResult);
            if (pageNumber < subList.size()) {
                Collections.shuffle(subList);
            } else {
                pageNumber = subList.size();
            }

            if(number - results.size() < pageNumber) pageNumber = number - results.size();

            results.addAll(subList.subList(0, pageNumber));

            if(subList.size() < pageSize){
                break;
            }

            subList.clear();
        }while (true);
        return results;
    }

    /**
     * @Author hanlin
     * @Date 2019/8/8 10:55
     * @Description 通过扫描所有行的方式统计表的总行数
     * @param name 表名
     * @param config 配置信息
     * @return long
     **/
    public static long useScanCount(TableName name,Configuration config) throws Exception{
        int count =0;
        Connection conn = ConnectionFactory.createConnection(config);
        Table table = conn.getTable(name);
        Scan scan = new Scan();
        scan.setFilter(new KeyOnlyFilter());
        ResultScanner rs = table.getScanner(scan);
        for(Result result : rs){
            count ++;
        }
        conn.close();
        return count;
    }


    /**
     * @Author hanlin
     * @Date 2019/8/8 10:56
     * @Description 使用协处理器的方式统计表的所有行
     * @param name 要统计表的表名
     * @param config 集群的配置信息
     * @return long
     **/
    public static long useCoprocessorCount(TableName name,Configuration config) throws Throwable {
        Connection conn = ConnectionFactory.createConnection(config);
        Table table = conn.getTable(name);
        Admin admin = conn.getAdmin();
        String coprocess="org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
        HTableDescriptor desc = table.getTableDescriptor();
        if(!desc.hasCoprocessor(coprocess)){
            desc.addCoprocessor(coprocess);
            admin.disableTable(name);
            admin.modifyTable(name,desc);
            admin.enableTable(name);
        }
        AggregationClient client = new AggregationClient(config);
        return  client.rowCount(table,new LongColumnInterpreter(),new Scan());
    }

}
