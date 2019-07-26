package top.zigaoliang.util;

import java.util.List;

public class PhoneUtil {
    //获得座机号后四位前的字符串
    public static String getTelePhoneFirst(String in){
       return in.substring(0,in.length()-4);
    }
    //获得座机号的中间三位
    public static String getMiddleTelePhone(String in){
        return in.substring(2,5);
    }
    //得到座机号的后四位
    public static String getTelePhoneLast(String in) {
        return in.substring(in.length() - 4);
    }
    //返回座机号除了中间几位的后几位（7位座机号有2位，8位座机号有3位）
    public static String getTelePhoneLastSome(String in){
        return in.substring(5);
    }


    //得到手机号的中间三位
    public static String getCellPhoneMiddle(String in) {
        return in.substring(4, 7);
    }

    /**
     * 将座机号分割开
     * @return
     */
    public static String[] splitTelePhone(String in){
        in = in.trim();
        in = in.replaceAll(" +", " ");
        String[] tellPhone = new String[3];
        if(in.contains(" ")){
            tellPhone[0] = in.split(" ")[0];
            tellPhone[1] = in.split(" ")[1];
            tellPhone[2] = " ";
        }
        if(in.contains("-")){
            tellPhone[0] = in.split("-")[0];
            tellPhone[1] = in.split("-")[1];
            tellPhone[2] = "-";
        }
        return  tellPhone;
    }

    //座机号码随机获得一个地区编号
    public static String getRandomRegionCode(List<String> telePhoneList){
        int randomIndex = Util.getNumByRange(0,telePhoneList.size() -1);
        return telePhoneList.get(randomIndex);
    }

    //手机号随机获得一个地区编号
    public static String getRandomCellRegionCode(List<String> cellPhoneCodeList){
        int randomIndex = Util.getNumByRange(0, cellPhoneCodeList.size() -1);
        return cellPhoneCodeList.get(randomIndex);
    }

    //对手机号的前三位进行脱敏
    public static String maskCellPhoneRegion(IndexMapList indexMapList, String in, int seed, boolean flag) {
        String region = in.substring(0, 3);
        if(HashMapUtil.containsKey(indexMapList.getMap(),in.substring(0,3))){
            int oldIndex = HashMapUtil.getMapValue(indexMapList.getMap(),region);
            int[] indexRange = {0,indexMapList.getList().size() -1};
            int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
            return indexMapList.getList().get(newIndex);
        }else {
            return region;
        }
    }

}
