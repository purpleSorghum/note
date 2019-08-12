package top.zigaoliang;


import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HiveBase
 * @Author hanlin
 * @Date 2019/8/5 13:50
 **/
public class HiveBase {

    @Test
    public void executeSQL() throws Exception{
        int rows=0;
        Class.forName("org.apache.hive.jdbc.HiveDriver");
//        Connection conn = DriverManager.getConnection("jdbc:hive2://192.168.100.21:10000/mydb2");
        Connection conn = DriverManager.getConnection("jdbc:hive2://192.168.100.21:10000/mydb2","root","123456");
        Statement st = conn.createStatement();
        //在on条件中可以指定某个列，如果这个列是表在分桶时候用的列，那么mr不会进行全表扫描，只扫描分区，效率会有很大提升
//        ResultSet rs = st.executeQuery("select * from t1 tablesample(bucket 2 out of 1000 on rand()) where age > 15");
        ResultSet rs = st.executeQuery("select count(*) from t1 ");
//        ResultSet rs = st.executeQuery("Select * from t1 tablesample(bucket 1 out of 1 on rand())");
        while(rs.next()){
            rows++;
//            System.out.println(rs.getString("name") + "," + rs.getString("age")) ;
            System.out.println(rs.getInt(1));
        }

        rs.close();
        st.close();
        conn.close();
    }

    @Test
    public void test() throws Exception{
        Map<String,Object> map = new HashMap<>();
        map.put("address","192.168.100.21:10000");
        map.put("database","mydb2");
        map.put("table","t1");
        map.put("number",100);
        map.put("type",0);

        ResultSet rs = get(map);
        while (rs.next()){
            System.out.println(rs.getString(1) + ","
                    + rs.getString(2)+ ","
                    + rs.getString(3));
        }
        System.out.println("共查到记录（条）："+rs.getRow());
        Collections.shuffle(new ArrayList<>());
    }

    public ResultSet get(Map<String,Object> map) throws Exception {
        init(map);
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        Connection conn = DriverManager.getConnection("jdbc:hive2://"+map.get("address")+"/" + map.get("database"),map.get("user").toString(),map.get("password").toString());
        Statement st = conn.createStatement();
        Object type = map.get("type");
        String sql ="select " + map.get("column") + " from " + map.get("table");
        if((int)type == 0){
            ResultSet r = st.executeQuery("select count(*) from "+map.get("table"));
            r.next();
            int count = r.getInt(1);
            int number = (int)(map.get("number"));
            int t = count/number > 0 ? count/number : 1 ;
            sql += " tablesample(bucket 1 out of "+ t + " on rand())";
        }else{
            sql += " distribute by rand() sort by rand() limit " + map.get("number");
        }
        System.out.println("SQL:"+sql);
        ResultSet rs = st.executeQuery(sql);
        return rs;
    }

    private void init(Map<String,Object> map) {
        if(map.get("address") == null)
            throw new RuntimeException("address is null 地址不能为空，例：192.168.1.10：10000");
        if(map.get("database") == null)
            throw new RuntimeException("database is null 数据库不能为空");
        if(map.get("table") == null)
            throw new RuntimeException("table is null 表名不能为空");
        if(map.get("number") == null)
            throw new RuntimeException("number is null  数量或比例不能为空");
        //不存在的时候才添加,用于设置默认值
        map.putIfAbsent("type",0);
        map.putIfAbsent("column","*");
        map.putIfAbsent("user","root");
        map.putIfAbsent("password","123456");
    }
}
