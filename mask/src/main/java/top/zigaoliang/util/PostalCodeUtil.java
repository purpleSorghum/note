package top.zigaoliang.util;


import org.apache.log4j.Logger;
import top.zigaoliang.algo.AlgoPostalCode;
import top.zigaoliang.common.FileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将邮编按照市分组加载到内存中
 */
public class PostalCodeUtil {
    public static List<String> postCodeList = null;
    public static List<String> postCodeList_0 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_0 = new HashMap<String, Integer>();

    public static List<String> postCodeList_1 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_1 = new HashMap<String, Integer>();

    public static List<String> postCodeList_2 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_2 = new HashMap<String, Integer>();

    public static List<String> postCodeList_3 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_3 = new HashMap<String, Integer>();

    public static List<String> postCodeList_4 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_4 = new HashMap<String, Integer>();

    public static List<String> postCodeList_5 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_5 = new HashMap<String, Integer>();

    public static List<String> postCodeList_6 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_6 = new HashMap<String, Integer>();

    public static List<String> postCodeList_7 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_7 = new HashMap<String, Integer>();


    public static List<String> postCodeList_8 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_8 = new HashMap<String, Integer>();

    public static List<String> postCodeList_9 = new ArrayList<>();
    public static Map<String,Integer> postCodeMap_9 = new HashMap<String, Integer>();

    static {
        loadCodeDictionary();
        loadCodeByCity();
    }
    /**
     * 初始化邮编字典
     */
    public static void loadCodeDictionary() {
        if (postCodeList == null) {
            synchronized (AlgoPostalCode.class) {
                if (postCodeList == null) {
                    try {
                        postCodeList = FileHelper.readSource("/postcode.txt",String.class);
                    }
                    catch (Exception e) {
                        Logger.getLogger(AlgoPostalCode.class.getClass().getSimpleName()).error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *
     */
    public static void loadCodeByCity() {
        Integer data0 = 0;
        Integer data1 = 0;
        Integer data2 = 0;
        Integer data3 = 0;
        Integer data4 = 0;
        Integer data5 = 0;
        Integer data6 = 0;
        Integer data7 = 0;
        Integer data8 = 0;
        Integer data9 = 0;
        for (int i = 0; i < postCodeList.size(); i++) {
            switch (getCity(postCodeList.get(i))) {
                case 0:
                    postCodeList_0.add(postCodeList.get(i));
                    postCodeMap_0.put(postCodeList.get(i), data0);
                    data0++;
                    break;
                case 1:
                    postCodeList_1.add(postCodeList.get(i));
                    postCodeMap_1.put(postCodeList.get(i), data1);
                    data1++;
                    break;
                case 2:
                    postCodeList_2.add(postCodeList.get(i));
                    postCodeMap_2.put(postCodeList.get(i), data2);
                    data2++;
                    break;
                case 3:
                    postCodeList_3.add(postCodeList.get(i));
                    postCodeMap_3.put(postCodeList.get(i), data3);
                    data3++;
                    break;
                case 4:
                    postCodeList_4.add(postCodeList.get(i));
                    postCodeMap_4.put(postCodeList.get(i), data4);
                    data4++;
                    break;
                case 5:
                    postCodeList_5.add(postCodeList.get(i));
                    postCodeMap_5.put(postCodeList.get(i), data5);
                    data5++;
                    break;
                case 6:
                    postCodeList_6.add(postCodeList.get(i));
                    postCodeMap_6.put(postCodeList.get(i), data6);
                    data6++;
                    break;
                case 7:
                    postCodeList_7.add(postCodeList.get(i));
                    postCodeMap_7.put(postCodeList.get(i), data7);
                    data7++;
                    break;
                case 8:
                    postCodeList_8.add(postCodeList.get(i));
                    postCodeMap_8.put(postCodeList.get(i), data8);
                    data8++;
                    break;
                case 9:
                    postCodeList_9.add(postCodeList.get(i));
                    postCodeMap_9.put(postCodeList.get(i), data9);
                    data9++;
                    break;
                default:
            }
        }
    }

    public static int getCity(String postalCode){
        return Integer.parseInt(postalCode.substring(3,4));
    }

}
