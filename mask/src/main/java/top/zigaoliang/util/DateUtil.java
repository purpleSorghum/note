package top.zigaoliang.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理的工具类
 */
public class DateUtil {

    //根据一个或多个空格分隔字符串
    //获得字符串的年月日  时分秒信息
    public static String[] getDateInfor(String date){
        String[] timeArray  = new String[6];
        String[] dateFirst = date.split("\\s+")[0].replaceAll("[/\\-年月日:。. ]"," ").split(" ");
        String[] timeLast = date.split("\\s+")[1].replaceAll("[时分秒:：。. ]"," ").split(" ");
        for(int i = 0; i < dateFirst.length; i++){
            timeArray[i] = dateFirst[i];
        }
        for(int j = 0; j < timeLast.length; j++){
            timeArray[j+3]  = timeLast[j];
        }
        return timeArray;
    }


    //获得日期或 时间的 分割字符
    public static String getDateSplit(String in) {
        in = in.trim();
        String[] aplitChar = {"-", "/", "\\", ".", "年", ":", " "};
        StringBuffer out = new StringBuffer();
            String split = "";
            for (int i = 0; i < aplitChar.length; i++) {
                if (in.split("\\s+")[0].contains(aplitChar[i])) {
                    split = aplitChar[i];
                    break;
                }
            }
        return  split;
    }


    /**
     * 获得用来遮蔽日期的字符串
     * @param length
     * @return
     */
    public static String getCoverStr(int length,String type){
        if(length <= 0){
            return null;
        }
        StringBuffer out = new StringBuffer();
        for(int i = 0; i < length; i++){
            out.append(type);
        }
        return out.toString();
    }

    //获取当前时间的年份
    public static int getCurrentYear(){
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    //当前的年份减一年
    public static int getCurrentYearNext(){
        return getCurrentYear() - 1;
    }


    //计算两个日期之间的天数
    /**
     * 网上说用Calendar计算天数的差，跨年的时候会有问题
     */
    public static Long getDayDifference(String startDate,String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sdf.parse(startDate);
        Date end = sdf.parse(endDate);
        return Double.valueOf(Math.ceil((end.getTime() - start.getTime())/(1000*3600*24d))).longValue();
    }
    //日期加上指定的天数   1989-08-08   618505200000
    public static String addDate(String srcDate,long day) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date  date = sdf.parse(srcDate);
        long time = date.getTime();
        day = day * 24 * 60 * 60 * 1000;
            time += day;
        return sdf.format(time);
    }



}
