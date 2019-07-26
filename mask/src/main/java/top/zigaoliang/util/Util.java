package top.zigaoliang.util;


import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.TreeNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 洗牌算法
 * 主要功能：给一个固定值n，输出一个乱序后的数组Integer[]，n为数组的大小，数组的值从0开始。
 * Created by byc on 10/29/18.
 */
public class Util {
    private static Logger log = Logger.getLogger(Util.class);

    public static char[] emailBase = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final String[] numberChinanese = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    //每月有31天的月份
    public static final int[] longMonth = {1, 3, 5, 7, 8, 10, 12};
    //月份的天数范围
    public static final int[] getMonthRange = {28, 29, 30, 31};
    //年份的最大值和最小值
    public static int[] yearRange = {0000, 9999};
    //身份证中的日期范围
    public static int[] yearRangeIdCard = {1900, 2020};

    //年份的取值范围，不包括0
    public static int[] monthRange = {1, 12};
    //日的范围
    public static int[] dayRange = {1, 28};
    //A-F 编码范围
    public static int[] upperChar = {65, 70};
    //a-f 编码范围
    public static int[] downChar = {97, 102};
    //小写字母的编码范围
    public static int[] numberArray = {0, 9};
    public static int[] numberArrayBeginOne = {1, 9};
    //单数数字
    public static int[] numberSingle = {1, 3, 5, 7, 9};
    //偶数数字
    public static int[] numberEven = {0, 2, 4, 6, 8};
    //整数的第一位的数字
    public static int[] numberArrayFirst = {1, 9};
    //小时的取值范围12进制
    public static int[] hourArrayShort = {0, 11};
    //小时的取值范围24进制
    public static int[] hourArrayLong = {0, 23};
    //分钟/秒 的取值范围
    public static int[] minuteSecond = {0, 59};
    //mysql中year类型：范围1901 ~ 2155
    public static int[] mysqlYearRange = {1901, 2155};
    //中文unicode编码的范围
    //身分证加权计算的模
    public static String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
    //身份证的校验码
    public static String[] varifyCode = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
    //组织机构代码 中的 字符
    public static final String[] codeNo = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B",
                "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
                "T", "U", "V", "W", "X", "Y", "Z"};

    //组织机构代码 计算 校验码的 映射关系
    public static final String[] staVal = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
                "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35"};
    //各个省份的编码
    public static int[] proviceCode = {11, 12, 13, 14, 15, 21, 22, 23, 31, 32, 33, 34, 35, 36,
                37, 41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62,
                63, 64, 65, 71, 81, 82};

    /**
     * 获得一个乱序的序号数组
     *
     * @param size 数组大小
     */
    public static int[] shuffle(int size) {
        int[] array = new int[size];
        if (size <= 1) {
            array[0] = 0;
            return array;
        }
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        for (int i = 0; i < size; i++) {
            int index = 0;
            while ((index = (int) (Math.random() * size)) == i) {
            }
            array[i] = array[i] + array[index];
            array[index] = array[i] - array[index];
            array[i] = array[i] - array[index];
        }
        return array;
    }

    /**
     * 根据跟组信息进行乱序
     * 分组乱序概念如下：
     * 如果当前所有行的数据（in）需要按照某列（性别列）进行分组乱序
     * 首先需要对该列进行数据分类（性别列只包含男和女两类）
     * 然后乱序时只能发生在相同类别中
     * （即男和男乱序，女和女乱序，
     * 例如 1、2、3行是男，4、5、6行是女，
     * 那么乱序时，只能1、2、3之间互相乱序，4、5、6之间互相乱序）
     *
     * @param in         输入数据
     * @param groupIndex 需要分组的列索引集合，即哪些列需要进行分组
     */
    public static int[] shuffle(List<List<Object>> in, List<Integer> groupIndex) {
        int[] newIndexes = new int[in.size()];
        Set<List<Integer>> indesex = new HashSet<>();//用于统计各个分组的索引
        // 使用树形结构是为了保留多级分组的分组过程
        // 例如：分组列有两个，性别（男和女）和职位（研发和财务）
        // 男----研发----[1,5,8]
        //    |--财务----[7,9,10]
        // 女----研发----[2,4,6]
        //    |--财务----[3,11]
        TreeNode root = new TreeNode();//用于保存分组过程
        TreeNode node;
        int i = 0;
        // 遍历每一行数据，对需要分组的列，进行分类统计
        for (List<Object> row : in) {
            node = root;
            // 遍历每一个分组列
            for (int idx : groupIndex) {
                // 获取当前行分组列的具体数据（例如：男或者女）作为 Key 值，存入 TreeNode 中
                String key = row.get(idx).toString();
                if (!node.getMap().containsKey(key)) {
                    node.getMap().put(key, new TreeNode());
                }
                node = node.getMap().get(key);
            }
            if (node.getIndexes().size() == 0) {
                // 将分组完成后的结果存入 indesex 中
                indesex.add(node.getIndexes());
            }
            // 记录乱序前的序号，[0,1,2,3,4,5,6,7...]
            newIndexes[i] = i;
            node.getIndexes().add(i++);
        }

        // 遍历每一种分组结果，在分组内进行乱序
        for (List<Integer> group : indesex) {
            int[] newGroup = shuffle(group.size());
            for (int j = 0; j < group.size(); j++) {
                newIndexes[group.get(j)] = group.get(newGroup[j]);
            }
        }
        return newIndexes;
    }

    /**
     * 随机生成某个区间的整数
     *
     * @param start 长度的最小值
     * @param end   长度的最大值
     * @return
     */
    public static int getNumByRange(int start, int end) {
        return new Random().nextInt(end - start + 1) + start;
    }

    /**
     * 生成指定范围的Long类型的数据
     *
     * @param start 长度的最小值
     * @param end   长度的最大值
     * @return
     */
    public static Long getNumByRange(Long start, Long end) {
        return new RandomDataGenerator().nextLong(start,end);
    }


    /**
     * 功能：判断字符串出生日期是否符合正则表达式：包括年月日，闰年、平年和每月31天、30天和闰月的28天或者29天
     *
     * @return true, 符合; false, 不符合。
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(
                    "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断第18位校验码是否正确 第18位校验码的计算方式：
     * 1. 对前17位数字本体码加权求和 公式为：S = Sum(Ai * Wi), i =
     * 0, ... , 16 其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4
     * 2 1 6 3 7 9 10 5 8 4 2
     * 2. 用11对计算结果取模 Y = mod(S, 11)
     * 3. 根据模的值得到对应的校验码
     * 对应关系为： Y值： 0 1 2 3 4 5 6 7 8 9 10 校验码： 1 0 X 9 8 7 6 5 4 3 2
     */
    public static boolean isVarifyCode(String Ai, String IDStr) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = sum % 11;
        String strVerifyCode = varifyCode[modValue];
        Ai = Ai + strVerifyCode;
        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                return false;
            }
        }
        return true;
    }

    //计算身份证的校验码
    public static String comPuteIDcardCheck(String AI) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum = sum + Integer.parseInt(String.valueOf(AI.charAt(i))) * Integer.parseInt(Wi[i]);
        }
        int modValue = sum % 11;
        String strVerifyCode = varifyCode[modValue];
        return strVerifyCode;
    }


    /**
     * 随机生成一个省份code
     *
     * @return
     */
    public static int getProvice() {
        return proviceCode[(getNumByRange(0, proviceCode.length - 1))];
    }

    /**
     * 生成指定位数的随机整数
     * 不能以0开头
     */
    public static String getIntegerNoZeroFirst(int length) {
        if (length <= 0) {
            log.error("参数不能小于等于0" + "方法名称：" + "Util.getIntegerNoZeroFirst");
            return "";
        }
        if (length == 1) {
            return Integer.toString(getNumByRange(1, 9));
        }
        StringBuffer out = new StringBuffer();
        Random random = new Random();
        out.append(Integer.toString(getNumByRange(1, 9)));
        for (int i = 0; i < length - 1; i++) {
            out.append(String.valueOf(random.nextInt(9)));
        }
        return out.toString();
    }

    /**
     * @param length 生成指定长度的整形字符串 可以 以0开头
     * @return
     */
    public static String getRandowNumber(int length) {
        StringBuffer val = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            //random.nextInt(n)  产生0到n 随机数
            val.append(String.valueOf(random.nextInt(10)));
        }
        return val.toString();
    }

    /**
     * "二五八"
     *
     * @param length 长度
     * @return
     */
    public static String getRandomNumberChinese(int length) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int randomIndex = getNumByRange(0, numberChinanese.length - 1);
            result.append(numberChinanese[randomIndex]);
        }
        return result.toString();
    }

    //随机生成一个日期
    public static String getRandomDateRange() {
        StringBuffer result = new StringBuffer();
        result.append(CommonUtil.fillNumberStr(Integer.toString(Util.getNumByRange(Util.yearRangeIdCard[0], Util.yearRangeIdCard[1])), 4));
        result.append(CommonUtil.fillNumberStr(Integer.toString(Util.getNumByRange(Util.monthRange[0], Util.monthRange[1])), 2));
        result.append(CommonUtil.fillNumberStr(Integer.toString(Util.getNumByRange(Util.dayRange[0], Util.dayRange[1])), 2));
        return result.toString();
    }

    /**
     * 整数  下标 脱敏公共方法
     *
     * @return
     */
    public static int maskBaseForInteger(int[] range, int in, int seed, boolean flag) {
        //出现整数是修改偏移量  (修改种子值)
        int rangeInt = range[1] - range[0] + 1;
        if (seed % (range[1] - range[0] + 1) == 0) {
            Random random = new Random(seed);
            seed = random.nextInt(rangeInt);
            if(seed % 10 == 0){
                seed = 1557;
            }
        }
        //取值范围
        int[] rangeTemp = {range[0] - 1, range[1] + 1};
        in = flag == true ? (in + seed % (rangeInt)) : (in - seed % (rangeInt));
        if (flag) {
            //正向脱敏
            if (in > range[1]) {
                in = in - range[1] + rangeTemp[0];
            }
        } else {
            //逆向脱敏
            if (in < range[0]) {
                in = in - range[0] + rangeTemp[1];
            }
        }
        return in;
    }

    /**
     * long  下标 脱敏公共方法
     * 日期转成Long类型，int类型不够用
     *
     * @return
     */
    public static Long maskBaseForLong(Long[] range, Long in, int seed, boolean flag) {
        //出现整数是修改偏移量  (修改种子值)
        Long rangeLong = range[1] - range[0] + 1;
        if (seed % (rangeLong) == 0) {
            Random random = new Random(seed);
            seed = random.nextInt(10000000);
            if(seed % 10 == 0){
                seed = 1557;
            }
        }
        //取值范围
        Long[] rangeTemp = {range[0] - 1, range[1] + 1};
        in = flag == true ? (in + seed % (rangeLong)) : (in - seed % (rangeLong));
        if (flag) {
            //正向脱敏
            if (in > range[1]) {
                in = in - range[1] + rangeTemp[0];
            }
        } else {
            //逆向脱敏
            if (in < range[0]) {
                in = in - range[0] + rangeTemp[1];
            }
        }
        return in;
    }


    /**
     * 根据年份和月份脱敏天
     *
     * @param seed
     * @param flag
     * @return
     */
    public static String maskDayByYearAndMonth(String oldBirth, String newBirth, int seed, boolean flag) {
        //日期的范围
        Integer result = null;
        String oldMonthRange = getDayRange(oldBirth);
        String newMonthRange = getDayRange(newBirth);
        result = maskBaseForInteger(changeDayLast(Integer.parseInt(newMonthRange)), Integer.parseInt(oldBirth.substring(oldBirth.length() - 2)), seed, flag);
        if (flag = false) {
            result = result + (Integer.parseInt(oldMonthRange) - Integer.parseInt(newMonthRange));
        }
        return CommonUtil.fillNumberStr(Integer.toString(result), 2);
    }

    //计算某年的某个月份有多少天
    public static String getDayRange(String birth) {
        String year = null;
        String month = null;
        String day = null;
        if (birth.length() == 8) {
            year = birth.substring(0, 4);
            month = birth.substring(4, 6);
        } else if (birth.length() == 6) {
            year = "19" + birth.substring(0, 2);
            month = birth.substring(2, 4);
        }
        if (Integer.parseInt(year) % 4 == 0) {
            //是闰年，2月份有29天
            if (Integer.parseInt(month) == 2) {
                day = Integer.toString(getMonthRange[1]);
            } else {
                if (Arrays.binarySearch(longMonth, Integer.parseInt(month)) > 0 ? true : false) {
                    day = Integer.toString(getMonthRange[3]);
                } else {
                    day = Integer.toString(getMonthRange[2]);
                }
            }
        } else {
            //不是闰年，2月份有28天
            if (Integer.parseInt(month) == 2) {
                day = Integer.toString(getMonthRange[0]);
            } else {
                if (Arrays.binarySearch(longMonth, Integer.parseInt(month)) > 0 ? true : false) {
                    day = Integer.toString(getMonthRange[3]);
                } else {
                    day = Integer.toString(getMonthRange[2]);
                }
            }
        }
        return day;
    }

    public static int[] changeDayLast(int last) {
        int[] dayRange = {1, 28};
        dayRange[1] = last;
        return dayRange;
    }

    //校验组织机构代码 中的校验码 的正确性
    public static boolean validateOrign(String orignCode) {
        Map map = new HashMap();
        for (int i = 0; i < codeNo.length; i++) {
            map.put(codeNo[i], staVal[i]);
        }
        final int[] wi = {3, 7, 9, 10, 5, 8, 4, 2};
        final char[] values = orignCode.substring(0, 8).toCharArray();
        int parity = 0;
        for (int i = 0; i < values.length; i++) {
            final String val = Character.toString(values[i]);
            parity += wi[i] * Integer.parseInt(map.get(val).toString());
        }
        String cheak = (11 - parity % 11) == 10 ? "X" : (11 - parity % 11) == 11 ? 0 + "" : (11 - parity % 11) + "";
        return orignCode.substring(8).equals(cheak);
    }

    //计算组织机构代码的校验码
    public static String computeOrignCheck(String orignCode) {
        Map map = new HashMap();
        for (int i = 0; i < codeNo.length; i++) {
            map.put(codeNo[i], staVal[i]);
        }
        final int[] wi = {3, 7, 9, 10, 5, 8, 4, 2};
        final char[] values = orignCode.toCharArray();
        int parity = 0;
        for (int i = 0; i < values.length; i++) {
            final String val = Character.toString(values[i]);
            parity += wi[i] * Integer.parseInt(map.get(val).toString());
        }
        String cheak = (11 - parity % 11) == 10 ? "X" : (11 - parity % 11) == 11 ? 0 + "" : (11 - parity % 11) + "";
        return cheak;
    }


    /**
     * 随机生成一个月份
     *
     * @return
     */
    public static String getMonthRandom(int monthLenth) {
        String month = Integer.toString(getNumByRange(1, 12));
        if (monthLenth == 2) {
            if (month.length() == 2) {
                return month;
            }
            if (month.length() == 1) {
                return "0" + month;
            }
        }
        if (monthLenth == 1) {
            return month;
        }
        return null;
    }

    /**
     * 通过二分查找的方式判断地区编码是否在 地区编码数组中
     * 如果找到就返回该元素在数组中的坐标， 如果没有找到就返回 -1
     *
     * @param key
     * @return
     */
    public static int arraySearch(int[] srcArray, int key) {
        return Arrays.binarySearch(srcArray, key);
    }
}
