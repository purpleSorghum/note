package top.zigaoliang.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用的工具类
 * Created by byc on 12/17/18.
 */
public class CommonUtil {
    private static Logger log = Logger.getLogger(AlgoMaskUtil.class.getSimpleName());

    public static String[] number = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    //车牌号分割字符
    public static String[] plateNumberSplitChar = {".","-"," "};

    //判断一个数组中是否包含某个元素
    public static boolean arrayContain(String[] arr, String dom) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(dom)) {
                return true;
            }
        }
        return false;
    }

    public static String deleteString(String str, char delChar) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != delChar) {
                out.append(str.charAt(i));
            }
        }
        return out.toString();
    }

    /**
     * 删除字符串中指定的字符
     *
     * @param str
     * @param arry
     * @return
     */
    public static String deleteChars(String str, char[] arry) {
        for (int i = 0; i < arry.length; i++) {
            str = deleteString(str, arry[i]);
        }
        return str;
    }

    /**
     * 删除字符串中指定的字符串
     * 只删除字符串第一次出现的位置
     * @return
     */
    public static String deleteStr(String src, String[] param) {
        for (int i = 0; i < param.length; i++) {
            if (StringUtils.isNotBlank(param[i])) {
                int startIndex = src.indexOf(param[i]);
                if (startIndex >= 0) {
                    StringBuffer temp = new StringBuffer();
                    temp.append(src.substring(0, startIndex)).append(src.substring(startIndex + param[i].length()));
                    src = temp.toString();
                }
            }
        }
        return src;
    }



    /**
     * 获得字符串中包含的数组中的字符
     *
     * @return
     */
    public static String getDomFromStr(String src, String[] arry) {
        for (int i = 0; i < arry.length; i++) {
            if (src.contains(arry[i])) {
                return arry[i];
            }
        }
        return null;
    }

    /**
     * 判断一个字符串中是否包含  指定数组中的元素
     * @return
     */
    public static boolean strHasArrayDom(String src, String[] array){
        for (int i = 0; i <array.length ; i++) {
           if(src.contains(array[i])){
               return true;
           }
        }
        return false;
    }
    //判断一个字符串中是否包含指定数组中的元素  如果包含，返回这个元素
    public static String strHasArray(String src, String[] array){
        for(int i = 0; i < array.length; i++){
            if(src.contains(array[i])){
                return array[i];
            }
        }
        return "";
    }



    //获得集合的下一个 下标
    public static int getNextIndex(int index, int length) {
        index += 1;
        if (index > length) {
            return index - length;
        } else {
            return index;
        }
    }

    //判断一个字符串中是否包含数字 数字包括 '0' '一'' 两种
    public static boolean containNumber(String src) {
        for (int i = 0; i < number.length; i++) {
            if (src.contains(number[i])) {
                return true;
            }
        }
        return false;
    }

    //判断一个字符串是否都是数字
    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }


    //将字符串按照指定的字符数组分割  分割结果类似：[张庄, 镇, 马店, 乡]
    public static String[] splitByArray(String src, String[] arr) {
        int idxXiang, idxJieDao, idxZhen, min;

        List<String> result = new ArrayList<>();

        do {
            idxXiang = src.indexOf(arr[0]);
            idxJieDao = src.indexOf(arr[1]);
            idxZhen = src.indexOf(arr[2]);

            min = idxJieDao;
            if (min == -1 || (idxXiang > -1 && idxXiang < min)) min = idxXiang;
            if (min == -1 || (idxZhen > -1 && idxZhen < min)) min = idxZhen;

            if (min > -1) {
                result.add(src.substring(0, min));
                result.add(min == idxJieDao ? src.substring(min, min + 2) : src.substring(min, min + 1));
                src = min == idxJieDao ? src.substring(min + 2) : src.substring(min + 1);
            }

        } while (min > -1);
        return result.toArray(new String[result.size()]);
    }

    /**
     * 获得指定元素的下标   整形数组
     *
     * @param array
     * @param value
     * @return
     */
    public static int getArrayNodeByIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;   //获得指定元素的下标
    }

    /**
     * 获得指定元素的下标   字符串数组
     *
     * @return
     */
    public static int getArrayNodeByIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    //判断一个字符是否是汉字  注意：如果是一二这种不认为是汉字
    /**
     * @param c
     * @return 1 汉字 2 "一，二"这种汉字 3 数字 4 字母a-z  5 字母A-Z
     */
    public static int isChineseContain(char c) {
        for (int i = 0; i < Util.numberChinanese.length; i++) {
            if ((c + "").equals(Util.numberChinanese[i])) {
                return 2;
            }
        }
        if (String.valueOf(c).matches("[\u4e00-\u9fa5]")) {
            return 1;
        }
        if (48 <= Integer.valueOf(c) && Integer.valueOf(c) <= 57) {
            return 3;
        }
        if (97 <= Integer.valueOf(c) && Integer.valueOf(c) <= 122) {
            return 4;
        }
        if (65 <= Integer.valueOf(c) && Integer.valueOf(c) <= 90) {
            return 5;
        }
        return 0;
    }

    //判断字符串是否以某个地址关键字结尾
    public static boolean endByKeyword(String[] array, String address) {
        address = address.trim();
        //如果该县是XXX镇，XXX乡，取最后一位
        //如果该县是XXX街道，取最后2位
        String lastCharOne = address.substring(address.length() -1);
        String lastCharTwo = address.substring(address.length() -2);
        if(Arrays.binarySearch(array,lastCharOne) >= 0  || Arrays.binarySearch(array,lastCharTwo) >= 0){
            return true;
        }
        return false;
    }

    //获得字符串结尾的关键字
    public static String getEndKeyWord(String[] array, String address){
        return  null;
    }


    /**
     * 数字字符串填充到指定的长度
     * 在数字的前面使用0填充
     * 例子： 2：45：6（时间）-> 02:45:06
     * @param length
     * @return
     */
    public static String fillNumberStr(String src, int length) {
        StringBuffer result = new StringBuffer();
        if (Integer.toString(length).length() < length) {
            int fillLength = length - src.length();
            for (int i = 0; i < fillLength; i++) {
                result.append("0");
            }
            return result.append(src).toString();
        } else {
            return src;
        }
    }

    /**
     * 得到一个字符串中某个字符出项的次数
     *
     * @return
     */
    public static int countStr(String src, String toFind) {
        int num = 0;
        while (src.contains(toFind)) {
            src = src.substring(src.indexOf(toFind) + toFind.length());
            num++;
        }
        return num;
    }

    //判断一个字符串转换成int类型是否会超出int的取值范围
    public static boolean outOfIntRange(String src) {
        try {
            Integer.parseInt(src);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
    public static boolean outOfLong(String src) {
        try {
            Long.parseLong(src);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    //判断一个字符串转换成double类型是否会超出double的取值范围
    public static boolean outOfDoubleRange(String src) {
        try {
            Double.parseDouble(src);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    /**
     * 字符串只保留中文，去除其他的数字字母或特殊符号
     *
     * @return
     */
    public static String remainChinese(String src) {
        char[] array = src.toCharArray();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (String.valueOf(array[i]).matches("[\u4e00-\u9fa5]")) {
                result.append(array[i]);
            }
        }
        return result.toString();
    }

    //判断一个字符串中是否有汉字
    public static boolean hasChinese(String src) {
        String regex = ".*[\\u4e00-\\u9fa5].*";
        return src.matches(regex);
    }

    //判断一个字符串是否全是数字
    public static boolean isAllChinese(String src){
        String reg = "[\\u4e00-\\u9fa5]+";
        if (!src.matches(reg)){
            return false;
        }
        return true;
    }
    //判断一个字符是否是汉字
    public static boolean isChinese(String src){
        if(src.matches("[\u4e00-\u9fa5]")){
            return true;
        }else{
            return false;
        }
    }

    //判断一个字符串中是否有数字
    public static boolean hasLetter(String src) {
        String regexletter = ".*[a-zA-Z].*";
        return src.matches(regexletter);
    }

    //判断一个字符串中是否有字母
    public static boolean hasNumber(String src) {
        String regexNumber = ".*[0-9].*";
        return src.matches(regexNumber);
    }

    // 去掉字符串中的特殊字符
    public static String removeSpecial(String src) {
        String regEx = "[\n()\\\\()（）%*&]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(src);
        return matcher.replaceAll("").trim();
    }
    public static String removeSpecial(String src,String regEx){
        Matcher matcher = Pattern.compile(regEx).matcher(src);
        return matcher.replaceAll("").trim();
    }


    public static boolean isNumberic(char ch) {
        return Character.isDigit(ch);
    }

    public static boolean isLetter(char ch) {
        return Character.isLowerCase(ch) || Character.isUpperCase(ch);
    }

    public static String strRemoveAppoint(String src, String goal) {
        if(src.lastIndexOf(goal) >= 0){
            if(src.lastIndexOf(goal) == 0){
                return "";
            }else{
                return src.substring(0, src.lastIndexOf(goal));
            }
        }
        return src;
    }

    //根据种子生成一个指定范围的随机数
    public static int getRanomBySeed(int seed,int min, int max){
        Random random = new Random(seed);
        //根据种子生成某个区间的随机数
        return random.nextInt(max -min  +1) + min;
    }
    //根据种子生成一个数字
    public static int getRanomBySeed(int seed){
        Random random = new Random(seed);
        return random.nextInt(10);
    }



    //判断字符串中是否包含特殊字符
    public static boolean isSpecialChar(String str){
        Pattern p = Pattern.compile("[\\/'\"\":\\t\\r\\n;`~!@#$%^&*,.<>?]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    //对指定的日期增加指定的天数
    public static String dataAddSomeTime(String date, int day, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String restr = "";
        try {
            Date dt = dateFormat.parse(date);
            Calendar rightNoe = Calendar.getInstance();
            rightNoe.setTime(dt);
            rightNoe.add(Calendar.DATE, day);
            Date result = rightNoe.getTime();
            restr = dateFormat.format(result);
        }catch (Exception e){
            log.error("CommonUtil -> dataAddSomeTime对日期添加指定的天数出现未知异常");
            e.printStackTrace();
        }
        return restr;
    }
    //按照指定的格式 格式日期
    public static String dataFormat(String date,String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String restr = "";
        try {
            restr = dateFormat.format(dateFormat.parse(date));
        } catch (ParseException e) {
            log.error("CommonUtil -> dataFormat 格式化日期出现未知异常");
            e.printStackTrace();
        }
        return restr;
    }


    //取得整形数组中的最大值
    public static int getMaxFromArray(String[] paramArray){
        Arrays.sort(paramArray);
        return Integer.parseInt(paramArray[paramArray.length -1]);
    }

    //判断某字符串是否以指定的字符串结尾
    public static boolean strEndWithSpecial(String src, String[] array){
        for(int i = 0; i < array.length; i++){
            if(src.endsWith(array[i])){
                return true;
            }
        }
        return false;
    }


    //得到字符串的前缀 例子：   "{[李白}"   return   "{[
    //                        " _李白"    return   " _
    public static String getPrefix(String src) {
        StringBuffer result = new StringBuffer();
        char[] charArray = src.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (!Character.isLetter(charArray[i])
                    && !Character.isDigit(charArray[i])
                    && charArray[i] != '-' && charArray[i] != '+' && charArray[i] != ')') {
                result.append(charArray[i]);
            } else {
                return result.toString();
            }
        }
        return result.toString();
    }

    //得到字符串的后缀 例子：   "{[李白}"   return   }"
    public static String getSuffix(String src){

        if(src == null){
            return  "";
        }
        StringBuffer result = new StringBuffer(src);
        src = result.reverse().toString();
        String prefix  = getPrefix(src);
        if(prefix.equals(src)){
            return "";
        }
        return prefix;
    }

    //参数去掉前缀和后缀的特殊字符
    //  eg:     "{[李白}"   ->   李白
    public static String removeSpecialFromSrc(String src,String prefix, String suffix){
        try {
            return src.substring(prefix.length(),src.length()-suffix.length());
        }catch (Exception e) {
            return src;
        }
    }

    //判断一个字符串中时候只包含指定的字符
    public static boolean containSpecialChar(String src, String[] specialChar){
        String[] charArray =  src.split("");
        boolean flag = false;
        for(int i = 1; i < charArray.length; i++){
            for(int j = 0; j < specialChar.length ; j++){
                if(charArray[i].equals(specialChar[j])){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                return false;
            }
        }
        return true;
    }

    //返回字符串中指定的字符在字符串中的下标
    public static int getStrIndex(String src, String goal){
//        Matcher matcher = Pattern.compile(goal).matcher(src);
//        return matcher.start();
         return src.indexOf(goal);
    }

    public static String coverBySymbol(String symbol,int length){
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            result.append(symbol);
        }
        return  result.toString();
    }

    //在数组中找到指定的关键字
    public static String getKeyWordFromArr(String src, String[] arr){
        int index = Arrays.binarySearch(arr,src);
        if(index >= 0){
            return arr[index];
        }else{
            return "";
        }
    }



}
