package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoNumberUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.DateUtil;
import top.zigaoliang.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日期算法
 * Created by byc on 10/24/18.
 */
public class AlgoDate extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoDate.class);
    public AlgoDate() {
        super(AlgoId.DATE);
    }

    @Override
    public boolean find(String in) {
        ErrorCode errorCode = null;
        /**
         * 判断是不是year类型
         * mysql中year类型：范围1901 ~ 2155
         */
        if(checkMysqlYear(in)){
            return true;
        }
        /**
         * 验证是否支持mysql的TIME类型，MySQL的Time类型范围：
         * "-838:59:59" - "838:59:59"
         */
//        if(checkMysqlTime(in)){
//            return true;
//        }
        //根据空格分隔字符串
        String[] dateArray = in.split(" ");
        try {
            //验证年份是否合法
            String[] dateStrTemArray = dateArray[0].replaceAll("[/\\-年月日:. ]", " ").split(" ");
            //年份是4位
            if (dateStrTemArray[0].length() == 4) {
                boolean yearCheckThird = Util.yearRange[0] <= Integer.parseInt(dateStrTemArray[0]) && Integer.parseInt(dateStrTemArray[0]) <= Util.yearRange[1];
                if (!yearCheckThird) {
                    //  年份不符合要求
                    errorCode = ErrorCode.DATE_YEAR_ERROR;
                    log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                    return false;
                }
            }
            //年份是两位的
//            if(dateStrTemArray[0].length() == 2){
//
//            }
            boolean monthCheck = Integer.parseInt(dateStrTemArray[1]) <= Util.monthRange[1] && Integer.parseInt(dateStrTemArray[1]) >= Util.monthRange[0];
            //验证月份是否合法
            if (!monthCheck) {
                errorCode = ErrorCode.DATE_MOUTH_ERROR;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            //验证日是否合法
            //根据年份和月份计算的
            StringBuilder sb = new StringBuilder();
            sb.append(dateStrTemArray[0]).append("-").append(dateStrTemArray[1]).append("-").append(dateStrTemArray[2]);
            if (!Util.isDate(sb.toString())) {
                errorCode = ErrorCode.DATE_DAY_ERROR_BYYEARANDMONTH;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        } catch (Exception e) {
            errorCode = ErrorCode.DATE_FIND_NUKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }



    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskDate confMaskDate = (Conf.ConfMaskDate)confMask;
        //mysql的year类型仿真
        if(checkMysqlYear(in)){
            out.append(randomMysqlyear(in,confMaskDate));
            return 0;
        }
        //mysql的time类型仿真
//        if(checkMysqlTime(in)){
//
//        }
        ErrorCode errorCode = null;
        String[] dataRange = getDataYearByParam(confMaskDate.rangeday);
        String[] yearRange = getDataYearByParam(confMaskDate.rangeyear);
        if (yearRange == null || dataRange == null) {
            errorCode = ErrorCode.DATE_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 日期仿真参数：" + confMaskDate.rangeyear + " " + confMaskDate.rangeday);
            return errorCode.getCode();
        }
        //根据一个或多个空格分隔字符串
        //获得字符串的年月日
        String[] dateArray = in.split(" ")[0].replaceAll("[/\\-年月日:。. ]", " ").split(" ");
        if (dateArray.length == 3 && Integer.valueOf(dateArray[2].length()) > 31) {
            out.append(in);
            return 0;
        }
        //获得时间（时分秒）
        String[] timeArray = getDateTime(in);
        if(timeArray != null){
            timeArray = maskTime(timeArray,0,confMaskDate.seed,true);
        }
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(CommonUtil.fillNumberStr(randomYear(Integer.parseInt(dateArray[0]), confMaskDate, yearRange), 4));
            sb.append("-");
            String month = "";
            //月份
            if (confMaskDate.month) {
                //保留月份
                month = dateArray[1];
            } else {
                month = Util.getMonthRandom(dateArray[1].length());
            }
            sb.append(month).append("-");
            //日期
            if (confMaskDate.day) {
                //保留日期
                sb.append(dateArray[2]);
            } else {
                //当前的日期加上变化范围后如果超过28小于0  在进行处理日在0—28之间
                String dataTemp = dataControl(Integer.parseInt(dateArray[2]), dataRange);
                sb.append(dataTemp);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.DATE_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        //还原字符串的格式
        out.append(reBackDateFormat(in, sb.toString(),timeArray));
        return 0;
    }

    /**
     * @param year
     * @return  Conf.ConfMaskDate confMaskDate
     */
    public String randomYear(int year, Conf.ConfMaskDate confMaskDate, String[] yearRange) {
        /**
         * 这个2038可能是时间戳的2038
         */
        if (year == 2038) {return "2037";}
        if (confMaskDate.year) {
            return Integer.toString(year);
        } else {
            //不保留年份
            int[] yearLimit = new int[2];
            yearLimit[0] = Integer.parseInt(confMaskDate.dateMin.substring(0,4));
            yearLimit[1] = Integer.parseInt(confMaskDate.dateMax.substring(0,4));
            if(year < yearLimit[0] || year > yearLimit[1]){
                return Integer.toString(year);
            }
            String yearTemp = Integer.toString(Util.getNumByRange(Integer.parseInt(yearRange[0]),
                    Integer.parseInt(yearRange[1])));
            int newYear = year + Integer.parseInt(yearTemp);
            if(newYear >= yearLimit[1]){
                newYear = Util.getNumByRange(year,yearLimit[1]);
            }else if(newYear <= yearLimit[0]){
                newYear = Util.getNumByRange(yearLimit[0],year);
            }
            return Integer.toString(newYear);
        }
    }



    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    /**
     *
     * @param in   数据库中源数据日期的格式
     * @param dateEnd 脱敏要转化位源数据格式的日期
     * @return
     */
    public String reBackDateFormat(String in, String dateEnd, String[] timeArry) {
        ErrorCode errorCode = null;
        String[] aplitChar = {"-", "/", "\\", ".", "年", ":", " "};
        StringBuilder out = new StringBuilder();
        String[] dateArrayEnd = dateEnd.split("-");
        try {
            String split = "";
            for (int i = 0; i < aplitChar.length; i++) {
                if (in.split(" ")[0].contains(aplitChar[i])) {
                    split = aplitChar[i];
                    break;
                }
            }
            if (split.equals("年")) {
                out.append(dateArrayEnd[0]).append("年").append(dateArrayEnd[1])
                        .append("月").append(dateArrayEnd[2]).append("日");
            } else {
                out.append(dateArrayEnd[0]).append(split).append(dateArrayEnd[1])
                        .append(split).append(dateArrayEnd[2]);
            }
            if (timeArry != null) {
                out.append(" ").append(timeArry[0]).append(":")
                        .append(timeArry[1]).append(":").append(timeArry[2])
                        .append(in.split(" ")[1].substring(8));
            } else {
                out.append(in.substring(in.split(" ")[0].length()));
            }
        } catch (Exception e) {
            errorCode = ErrorCode.DATE_REBACKFORMAT_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return null;
        }
        return out.toString();
    }


    public int maskBase(String in, StringBuilder out, boolean flag){
        ErrorCode errorCode = null;
        try {
            out.append(maskCommon(in,flag));
        } catch (Exception e) {
            errorCode = ErrorCode.DATE_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * @param in
     * @param flag
     * @return
     */
    public String maskCommon(String in, boolean flag) throws ParseException {
        Conf.ConfMaskDate confMaskDate = (Conf.ConfMaskDate) confMask;
        //如果是year类型，按照year类型进行处理
        if (checkMysqlYear(in)) {
            return maskForMysqlYear(in, confMaskDate, flag);
        }
        //获得字符串的年月日
        String[] dateArray = in.split(" ")[0].replaceAll("[/\\-年月日:。. ]", " ").split(" ");
        if(dateArray[0].equals("2038")){
            return in;  //2038年你怎么处理，都有可能脱敏后越界
        }
        if (dateArray.length == 3 && dateArray[2].length() == 4) {
            return in;
        }
        //获得时间（时分秒）
        String[] timeArray = getDateTime(in);
        if (timeArray != null) {
            timeArray = maskTime(timeArray, 1, confMaskDate.seed, flag);
        }
        StringBuilder result = new StringBuilder();
        result.append(dateMask(dateArray,confMaskDate,flag));
        return reBackDateFormat(in, result.toString(), timeArray);
    }

//    对日期进行脱敏(年月日)
//    备注：如果脱敏的时候可以选择参数(保留年，保留月)，就
//    要对年月日独立来脱敏，但是某一月的天数跟年份和月份有
//    关系，走不通
     public String dateMask(String[] date,Conf.ConfMaskDate confMaskDate, boolean flag) throws ParseException {
         String temp = "0000-01-01";
         StringBuilder dateFormat = new StringBuilder();
         dateFormat.append(date[0]).append("-").append(date[1]).append("-").append(date[2]);
        //获得脱敏的时间范围
         Long[] dataRange = getDateRange(temp,confMaskDate);
         //当前日期到0000-01-01的天数
         Long currentToDifference = DateUtil.getDayDifference(temp,dateFormat.toString());
         //脱敏要变化的天数
         Long changeDay = Util.maskBaseForLong(dataRange,currentToDifference,confMaskDate.seed,flag);
         return DateUtil.addDate(temp,changeDay);
     }

     //获得脱敏的时间范围（天数范围）
     public Long[] getDateRange(String dateMin, Conf.ConfMaskDate confMaskDate) throws ParseException {
         //梳理哪里传入的时间脱敏范围是距离0000-01-01从longRange[0]天到longRange[1]天内
         Long[] longRange = new Long[2];
         longRange[0] = DateUtil.getDayDifference(confMaskDate.dateMin,dateMin);
         longRange[1] = DateUtil.getDayDifference(dateMin,confMaskDate.dateMax);
         return longRange;
     }


    /**
     * @param rangeParam  时间的取值范围字符串
     * @return            获得的时间的范围数组
     */
    public String[] getDataYearByParam(String rangeParam){
        String[] array = new String[2];
        String timeRange = CommonUtil.removeSpecial(rangeParam, "[\n()\\\\()（） ]");
        if (timeRange.split(",").length == 2) {
            array[0] = timeRange.split(",")[0];
            array[1] = timeRange.split(",")[1];
        }
        //整数发现的时候"+0"是不合法的，但这里是可以的
        if(array[0].equals("-0") && array[1].equals("+0")){
            return array;
        }
        if(!AlgoNumberUtil.findInteger(array[0]) || !AlgoNumberUtil.findInteger(array[1])){
            return null;
        }
        return array;
    }

    /**
     *  对mysql的year类型脱敏
     */
    public String maskForMysqlYear(String in, Conf.ConfMaskDate confMaskDate,boolean flag){
        int[] yearRange = {1901,2155};
        return Integer.toString(Util.maskBaseForInteger(yearRange,Integer.parseInt(in),confMaskDate.seed,flag));
    }

    //获得日期的时分秒
    public String[] getDateTime(String in){
        String[] timeArray = new String[3];
        String[] dataArray = in.split(" ");
        String splitChar = "";
        if(dataArray.length >=2){
            if(dataArray[1].contains(":")){
                splitChar = ":";
            }else if(dataArray[1].contains("：")){
                splitChar = "：";
            }
        }
        if(dataArray.length >=2 && StringUtils.isNotBlank(splitChar)){
           //说明有时分秒
            timeArray = dataArray[1].split(splitChar);
            if(timeArray[2].length() > 2){
                timeArray[2] = timeArray[2].substring(0,2);
            }
        }
        if(timeArray[0] == null || timeArray[1] == null || timeArray[2] == null){
            return null;
        }
        return timeArray;
    }

    //对时分秒进行脱敏
    public String[] maskTime(String[] timeArray,int state,int seed,boolean flag){
        if(Integer.parseInt(timeArray[0]) <= 11){
            //考虑到时间的12进制和24进制
            timeArray[0] = state == 0?CommonUtil.fillNumberStr(Util.getNumByRange(0,11)  + "",2)
                    :(CommonUtil.fillNumberStr(Integer.toString(Util.maskBaseForInteger(Util.hourArrayShort,Integer.parseInt(timeArray[0]),seed,flag)),2));
        }else{
            timeArray[0] = state == 0?CommonUtil.fillNumberStr(Util.getNumByRange(0,23)  + "",2)
                    :(CommonUtil.fillNumberStr(Integer.toString(Util.maskBaseForInteger(Util.hourArrayLong,Integer.parseInt(timeArray[0]),seed,flag)),2));
        }
        timeArray[1] = state == 0?CommonUtil.fillNumberStr(Util.getNumByRange(0,59) + "",2):CommonUtil.fillNumberStr(Integer.toString(Util.maskBaseForInteger(Util.minuteSecond,Integer.parseInt(timeArray[1]),seed,flag)),2);
        timeArray[2] = state == 0?CommonUtil.fillNumberStr(Util.getNumByRange(0,59) + "",2):CommonUtil.fillNumberStr(Integer.toString(Util.maskBaseForInteger(Util.minuteSecond,Integer.parseInt(timeArray[2]),seed,flag)),2);
        return timeArray;
    }

    //控制原来的日 + 日变化范围之后范围还在（1，28）之间
    public String dataControl(int data,String[] dataRange){
        int dateTemp =data + Util.getNumByRange(Integer.parseInt(dataRange[0]), Integer.parseInt(dataRange[1]));
        if(dateTemp > 0 && dateTemp <= 28){
            return CommonUtil.fillNumberStr(Integer.toString(dateTemp),2);
        }
        if(dateTemp <= 0){
            return CommonUtil.fillNumberStr(Integer.toString(Util.getNumByRange(1,data)),2);
        }
        if(dateTemp > 28){
            //如果原来的是29，脱敏之后是29，30，31，返回28
            if(data > 28){
                return Integer.toString(28);
            }else{
                return CommonUtil.fillNumberStr(Integer.toString(Util.getNumByRange(data,28)),2);
            }
        }
        return "";
    }

    /**
     * 判断是否满足mysql的year类型的范围
     * @param in
     * @return
     */
    public boolean checkMysqlYear(String in){
       if(in.length() != 4){
           return false;
       }
       //如果不能被转换成整数，当然也不可能是year类型
       if(CommonUtil.outOfIntRange(in)){
           return false;
       }
       //注意：如何向mysql的year类型添加非法的数据，MySQL会自动转成"0000"
       if((Integer.parseInt(in) >= Util.mysqlYearRange[0]
               && Integer.parseInt(in) <= Util.mysqlYearRange[1])
               || Integer.parseInt(in) == 0){
           return true;
       }
       return false;
    }

    /**
     * 判断是否满足MySQL的Time类型
     * @param in
     * @return
     */
    public boolean checkMysqlTime(String in) {
        if (!in.contains(":")) {
            return false;
        }
        String[] timeArray = in.split(":");
        if (timeArray.length != 3) {
            return false;
        }
        if (Integer.parseInt(timeArray[0]) > 838 || Integer.parseInt(timeArray[1]) < -838) {
            return false;
        }
        //注意：不可能等于24，等于24就转成00点了
        if (Integer.parseInt(timeArray[1]) >= 24 || Integer.parseInt(timeArray[1]) < 0) {
            return false;
        }
        //注意：分钟和秒不能等于60，等于60就转成了00了
        if (Integer.parseInt(timeArray[2]) >= 60 || Integer.parseInt(timeArray[2]) < 0) {
            return false;
        }
        return true;
    }

    /**
     * 对MySQL的year类型进行脱敏
     * @param in
     * @return
     */
    public String maskMysqlyear(String in,Conf.ConfMaskDate confMaskDate,boolean flag){
        if(Integer.parseInt(in) == 0){
            return CommonUtil.fillNumberStr(in,4);
        }
        if(confMaskDate.year){
            //保留年份
            return in;
        }else{
            return Integer.toString(Util.maskBaseForInteger(Util.mysqlYearRange,Integer.parseInt(in),confMaskDate.seed,flag));
        }
    }

    //mysql的year类型仿真
    public String randomMysqlyear(String in,Conf.ConfMaskDate confMaskDate){
        if(confMaskDate.year){
            return in;
        }else{
            return Integer.toString(Util.getNumByRange(1901,2155));
        }
    }

    //mysql的time类型仿真
//    public String randomMysqlTime(String in,Conf.ConfMaskDate confMaskDate){
//        String[] timeArray = in.split(":");
//
//
//    }


    List<String> array=null;
    private List<String>  buildTestInitData(){
        if(this.confMask ==null){
            Conf.ConfMaskDate ad = new Conf.ConfMaskDate();
            ad.year=false;//保留年
            ad.month=false;//保留月
            ad.day=false;//保留日
            ad.rangeyear="(-105,+105)";//年变化范围
            ad.rangeday="(-365,+365)"; //日变化范围

            //梳理那里传给的日期范围(必须带上时分秒)，使用固定的标准格式的字符串串日期
            //如dateTime类型：0000-01-01 9999-12-31
            ad.dateMin = "1711-01-01 00:00:00";  //默认值
            ad.dateMax = "2102-12-31 23:59:59";
            this.confMask = ad;
        }

        if(array==null){
            array = new ArrayList();
            array.add("1756-11-12 08:11:55.111");
            array.add("1856");
            array.add("1756/02/12 08:11:55.111");
            array.add("1756/02/12 08:11:55");
            array.add("17560212 08:11:55.111");
            array.add("1756年02月12日 08:11:55");
            array.add("1756 02 12");
        }
        return  array;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = buildTestInitData();
        String in = array.get(Util.getNumByRange(0,array.size()-1));
        this.random(in,out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf,String in,String out){
        if(Strings.isNullOrEmpty(in) && Strings.isNullOrEmpty(out)){
            return new Object[]{true,null};
        }
        if(Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)){
            return new Object[]{false,"in or out is null"};
        }

        boolean isYear = checkMysqlYear(in);
        String[] in_dateArray = null;
        String[] in_timeArray = null;
        String[] out_dateArray = null;
        String[] out_timeArray = null;
        if(!isYear){
            //根据一个或多个空格分隔字符串
            //获得字符串的年月日
            in_dateArray = in.split(" ")[0].replaceAll("[/\\-年月日:。. ]", " ").split(" ");
            //获得时间（时分秒）
            in_timeArray = getDateTime(in);
            out_dateArray = out.split(" ")[0].replaceAll("[/\\-年月日:。. ]", " ").split(" ");
            //获得时间（时分秒）
            out_timeArray = getDateTime(out);
        }

        switch (conf.process) {
            case RANDOM:
            case MASK:
            case UNMASK:
                if(isYear){
                    if(((Conf.ConfMaskDate)conf).year&&!in.endsWith(out)){
                        return new Object[]{false,"违反保留年策略"};
                    }
                }else if (((Conf.ConfMaskDate) conf).year) {
                    if (!in_dateArray[0].equals(out_dateArray[0])) {
                        return new Object[]{false, "违反保留年策略"};
                    }
                }else if (((Conf.ConfMaskDate) conf).month) {
                    if (!in_dateArray[1].equals(out_dateArray[1])) {
                        return new Object[]{false, "违反保留月策略"};
                    }
                }else if (((Conf.ConfMaskDate) conf).day) {
                    if (!in_dateArray[2].equals(out_dateArray[2])) {
                        return new Object[]{false, "违反保留日策略"};
                    }
                } else {
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date in_date = sdf.parse(String.format("%s-%s-%s %s:%s:%s", in_dateArray[0], in_dateArray[1], in_dateArray[2], in_timeArray[0], in_timeArray[1], in_timeArray[2]));
                        long in_time = in_date.getTime();
                        Date out_date = sdf.parse(String.format("%s-%s-%s %s:%s:%s", out_dateArray[0], out_dateArray[1], out_dateArray[2], out_timeArray[0], out_timeArray[1], out_timeArray[2]));
                        long out_time = out_date.getTime();


                        if(((Conf.ConfMaskDate) conf).rangeyear!=null){
                            String[] _ry = ((Conf.ConfMaskDate) conf).rangeyear.split(",");
                            _ry[0] = _ry[0].substring(2);
                            _ry[1] = _ry[1].substring(1);
                            _ry[1] = _ry[1].substring(0,_ry[1].length()-1);

                            if(out_time<in_time-(Long.valueOf(_ry[0]) * 365 * 24 * 60 * 60 * 1000)||out_time>in_time+(Long.valueOf(_ry[1]) * 365 * 24 * 60 * 60 * 1000)){
                                return new Object[]{false, "违反超出年变化范围策略"};
                            }
                        }
                        if(((Conf.ConfMaskDate) conf).rangeday!=null){
                            String[] _rd = ((Conf.ConfMaskDate) conf).rangeday.split(",");
                            _rd[0] = _rd[0].substring(2);
                            _rd[1] = _rd[1].substring(1);
                            _rd[1] = _rd[1].substring(0,_rd[1].length()-1);

                            if(out_time<in_time-(Long.valueOf(_rd[0]) * 24 * 60 * 60 * 1000)||out_time>in_time+(Long.valueOf(_rd[1]) * 24 * 60 * 60 * 1000)){
                                return new Object[]{false, "违反超出日变化范围策略"};
                            }
                        }



                        if (!Strings.isNullOrEmpty(((Conf.ConfMaskDate) conf).dateMin)&&!Strings.isNullOrEmpty(((Conf.ConfMaskDate) conf).dateMax)) {
                            long _min_date = sdf.parse(((Conf.ConfMaskDate) conf).dateMin).getTime();
                            long _max_date = sdf.parse(((Conf.ConfMaskDate) conf).dateMax).getTime();
                            if(out_time<_min_date||out_time>_max_date){
                                return new Object[]{false, "违反超出日期范围策略"};
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case COVER:

                break;
            default:
                return new Object[]{true, null};
        }
        return new Object[]{true,null};
    }

}

