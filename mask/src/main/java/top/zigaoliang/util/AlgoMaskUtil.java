package top.zigaoliang.util;

import org.apache.log4j.Logger;
import top.zigaoliang.common.FileHelper;
import top.zigaoliang.conf.ErrorCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


/**
 * 随机脱敏 可逆脱敏的公共类
 * @author root
 */
public class AlgoMaskUtil {
    private static Logger log = Logger.getLogger(AlgoMaskUtil.class);

    static Map<String,Integer> chinese_3500_Map = new HashMap<String, Integer>();
    public static String[] chinese_3500_Array = null;

    static Map<String,Integer> chinese_3500_Map_NoAddrKey = new HashMap<String, Integer>();
    public static String[] chinese_3500_Array_NoAddrKey = null;
    public static Set<String> addrKey = new HashSet<String>() {{add("镇"); add("乡");}};

    static Map<String,Integer> numberAndCharMap = new HashMap<>();
    static String[] numberAndCharArray = null;

    static {
        //对汉字建立HashMap数据结构
        initHashMapForChinese();
        //对所有字符建立HashMap结构(包括数字和字母)
        initHashMapForNumberAndChar();
    }

    //对汉字建立HashMap数据结构
    public static void initHashMapForChinese() {
        if (chinese_3500_Array == null || chinese_3500_Array.length == 0) {
            synchronized (AlgoMaskUtil.class) {
                String[] chineseArr = FileHelper.readResourceToStr("/chineseName/3500_chinese.txt")
                        .split("");
                chinese_3500_Array = new String[chineseArr.length];
                chinese_3500_Array_NoAddrKey = new String[chineseArr.length - addrKey.size()];
                for (int i = 0, j = 0; i < chineseArr.length; i++) {
                    chinese_3500_Map.put(chineseArr[i], i);
                    chinese_3500_Array[i] = chineseArr[i];
                    if (!addrKey.contains(chineseArr[i])) {
                        chinese_3500_Map_NoAddrKey.put(chineseArr[i], j);
                        chinese_3500_Array_NoAddrKey[j] = chineseArr[i];
                        j++;
                    }
                }
            }
        }
    }

    public static void initHashMapForNumberAndChar(){
        if(numberAndCharArray == null || numberAndCharArray.length == 0){
            synchronized (AlgoMaskUtil.class){
                numberAndCharArray = new String[Util.emailBase.length];
                for (int i = 0; i < Util.emailBase.length; i++) {
                    numberAndCharMap.put(String.valueOf(Util.emailBase[i]),i);
                    numberAndCharArray[i] = String.valueOf(Util.emailBase[i]);
                }
            }
        }
    }

    /**
     * 对汉字组成的字符串进行可逆脱敏
     */
    public static String maskChinese(String src, int seed, boolean flag) {
        StringBuffer result = new StringBuffer();
        String[] array = src.split("");
        for (int i = 0; i < array.length; i++) {
            if(CommonUtil.isAllChinese(array[i])){
                /**
                 * 这段代码通过将汉字转换成二进制 -> 十进制 ->汉字
                 * 优点：效率高  缺点：有很多生僻字
                 */
//                String unicode = EncodUtil.string2Unicode(array[i]);
//                int temp = Util.maskBaseForInteger(Util.unicodeRange, Integer.parseInt(unicode.substring(2), 16), seed, flag);
//                result.append(EncodUtil.unicode2String("\\u" + Integer.toHexString(temp)));
                /**
                 * 通过字典的方式：
                 * 优点：避免生成生僻字  缺点：慢
                 */
                 result.append(maskChineseByDictionary(array[i],seed,flag));
            }else {
                result.append(array[i]);
            }
        }
        return result.toString();
    }
    //通过字典的方式对汉字进行脱敏
    public static String maskChineseByDictionary(String src, int seed, boolean flag) {
        int oldIndex = 0;
        try {
            oldIndex = chinese_3500_Map.get(src);
        } catch (Exception e) {
            log.debug("字典中可能没有这个汉字" + "原汉字：" + src);
            return src;
        }
        int[] indexRange = {0, chinese_3500_Array.length - 1};
        int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
        return chinese_3500_Array[newIndex];
    }

    public static String maskChineseForAddr(String src, int seed, boolean flag) {
        StringBuffer result = new StringBuffer();
        String[] array = src.split("");
        for (int i = 0; i < array.length; i++) {
            if(CommonUtil.isAllChinese(array[i])){
                /**
                 * 这段代码通过将汉字转换成二进制 -> 十进制 ->汉字
                 * 优点：效率高  缺点：有很多生僻字
                 */
//                String unicode = EncodUtil.string2Unicode(array[i]);
//                int temp = Util.maskBaseForInteger(Util.unicodeRange, Integer.parseInt(unicode.substring(2), 16), seed, flag);
//                result.append(EncodUtil.unicode2String("\\u" + Integer.toHexString(temp)));
                /**
                 * 通过字典的方式：
                 * 优点：避免生成生僻字  缺点：慢
                 */
                result.append(maskChineseByDictionaryForAddr(array[i],seed,flag));
            }else {
                result.append(array[i]);
            }
        }
        return result.toString();
    }
    //通过字典的方式对汉字进行脱敏
    public static String maskChineseByDictionaryForAddr(String src, int seed, boolean flag) {
        int oldIndex = 0;
        try {
            oldIndex = chinese_3500_Map_NoAddrKey.get(src);
        } catch (Exception e) {
            log.debug("字典中可能没有这个汉字" + "原汉字：" + src);
            return src;
        }
        int[] indexRange = {0, chinese_3500_Array_NoAddrKey.length - 1};
        int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
        return chinese_3500_Array_NoAddrKey[newIndex];
    }

    //对字符经行可逆脱敏(包括字母大小写，数字)
    public static String maskNumberAndChar(String src, int seed, boolean flag) {
        StringBuffer result = new StringBuffer();
        String[] arr = src.split("");
        int oldIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            try {
                oldIndex = numberAndCharMap.get(arr[i]);
            } catch (Exception e) {
                result.append(arr[i]);
                log.debug("字符字典中没有这个字符，返回.  原字符:" + arr[i]);
                continue;
            }
            int[] inexRange = {0, numberAndCharArray.length - 1};
            int newIndex = Util.maskBaseForInteger(inexRange, oldIndex, seed, flag);
            result.append(numberAndCharArray[newIndex]);
        }
        return result.toString();
    }


    //对数字组成的字符串进行可逆脱敏  如"465"
    public static String maskNumberStr(String src, int seed, boolean flag) {
        StringBuffer result = new StringBuffer();
        char[] numberArray = src.toCharArray();
        for (int i = 0; i < numberArray.length; i++) {
            if(numberArray[i] == '0'){
                result.append(0);
            }else{
                result.append(Util.maskBaseForInteger(Util.numberArrayBeginOne, numberArray[i] - 48, seed, flag));
            }
        }
        return result.toString();
    }
    //对数字进行可逆脱敏 第一位不能为0 (保证原有的数据第一位不是0)
    public static String maskNumberStrNoZeroFirst(String src, int seed, boolean flag){
        StringBuffer result = new StringBuffer();
        char[] numberArray = src.toCharArray();
        for (int i = 0; i < numberArray.length; i++) {
            if(i == 0){
                result.append(Util.maskBaseForInteger(Util.numberArrayFirst, numberArray[i] - 48, seed, flag));
            }else{
                result.append(Util.maskBaseForInteger(Util.numberArray, numberArray[i] - 48, seed, flag));
            }
        }
        return result.toString();
    }


    // "二五八" -> "四七六" 可逆
    public static String maskNumberChineseStr(String src, int seed, boolean flag) {
        StringBuffer result = new StringBuffer();
        char[] chineseArray = src.toCharArray();
        int[] chineseIndexRange = {0, Util.numberChinanese.length - 1};
        for (int i = 0; i < chineseArray.length; i++) {
            int currentIndex = CommonUtil.getArrayNodeByIndex(Util.numberChinanese, chineseArray[i] + "");
            int index = Util.maskBaseForInteger(chineseIndexRange, currentIndex, seed, flag);
            result.append(Util.numberChinanese[index]);
        }
        return result.toString();
    }




    //对汉字字符串进行随机
    public static String randomChinese(String srcStr, String goal){
        StringBuffer out = new StringBuffer();
        String[] array = goal.split("");
        for (int i = 0; i < array.length; i++) {
            if(CommonUtil.isAllChinese(array[i])){
                int intdex = Util.getNumByRange(0, srcStr.length() - 1);
                out.append(srcStr.substring(intdex, intdex + 1));
            }else{
                out.append(array[i]);
            }
        }
        return out.toString();
    }
    //将一个汉字随机仿真成一个指定的字典中的汉字
    public static String randomChinese(String srcStr, int goal){
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < goal; i++) {
                int intdex = Util.getNumByRange(0, srcStr.length() - 1);
                out.append(srcStr.charAt(intdex));
        }
        return out.toString();
    }
    //在3500个汉字字典中随机指定长度的汉字

    /**
     * @param length 要随机生成的汉字的长度
     * @return
     */
    public static String getRandomChineseFrom3500(int length){
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int index = Util.getNumByRange(0,chinese_3500_Array.length -1);
            out.append(chinese_3500_Array[index]);
        }
       return out.toString();
    }




    /**
     * 字典算法的匹配方式比较判断
     */
    public static boolean match(String in, String feature, Integer matchWay) {
        switch (matchWay) {
            case 0:
                return false; //!in.matches(feature);
            case 1:
                return in.matches(".*"+feature+".*");
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return matchNum(in, feature, matchWay);
            default:
                return false;
        }
    }

    private static boolean matchNum(String in, String feature, Integer matchWay) {
        if (in.matches("[-+]?[0-9]*\\.?[0-9]*") && feature.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            Double in2 = Double.valueOf(in);
            Double feature2 = Double.valueOf(feature);
            switch (matchWay) {
                case 2:
                    return in2.compareTo(feature2) < 0;
                case 3:
                    return in2.compareTo(feature2) <= 0;
                case 4:
                    return in2.compareTo(feature2) == 0;
                case 5:
                    return in2.compareTo(feature2) > 0;
                case 6:
                    return in2.compareTo(feature2) >= 0;
                default:
                    return false;
            }
        }
        return false;
    }


    /**
     * 根据种子计算区间的整数
     * flag true:正向脱敏  false:逆向脱敏
     * in 要脱敏的数据
     * seed 种子
     * @return
     */
    public static int maskByRandomSeed(int in, int seed,boolean flag){
        Random random = new Random(seed);
        //根据种子生成某个区间的随机数
        int temp = random.nextInt(100)+9;
        //对该数进行可逆脱敏（该数已经在该区间了）
        return flag == true? temp + in:in - temp;
    }

    /**
     * 数据脱敏异常日志打印
     * @return
     */
    public static int maskErrorBack(String in, Logger log, ErrorCode error){
        log.debug(error.getMsg() + "; 输入数据：" + in + "数据格式不正确或着算法异常");
        return error.getCode();
    }




}
