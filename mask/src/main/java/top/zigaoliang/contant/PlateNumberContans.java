package top.zigaoliang.contant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车牌号数据字典
 * Created by yehuan on 12/19/18.
 */
public class PlateNumberContans {
    //车牌号省的简称
    public static final String[] carNumberProvince = {"冀", "豫", "云", "辽", "黑", "湘", "皖", "鲁", "新", "苏", "浙", "赣", "鄂", "桂", "甘", "晋", "蒙", "陕", "吉", "闽", "贵", "粤", "川", "青", "藏", "琼", "宁", "渝", "沪", "京"};
    public static final Map<String,Integer> carNumberProvinceMap = new HashMap<String, Integer>(){{
        put("冀",0);put("豫",1);put("云",2);put("辽",3);put("黑",4);put("湘",5);put("皖",6);put("鲁",7);
        put("新",8);put("苏",9);put("浙",10);put("赣",11);put("鄂",12);put("桂",13);put("甘",14);put("晋",15);
        put("蒙",16);put("陕",17);put("吉",18);put("闽",19);put("贵",20);put("粤",21);put("川",22);put("青",23);
        put("藏",24);put("琼",25);put("宁",26);put("渝",27);put("沪",28);put("京",29);
    }};
    //车牌号的组成字母 不包含'I','O'
    public static final String[] carLetter = {"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final Map<String,Integer> carLetterMap = new HashMap<String, Integer>(){{
        put("A",0);put("B",1);put("C",2);put("D",3);put("E",4);put("F",5);put("G",6);put("H",7);put("J",8);
        put("K",9);put("L",10);put("M",11);put("N",12);put("P",13);put("Q",14);put("R",15);put("S",16);
        put("T",17);put("U",18);put("V",19);put("W",20);put("X",21);put("Y",22);put("Z",23);
    }};
    //车牌号中的数字
    public static final String[] carNumber = {"0","1","2","3","4","5","6","7","8","9"};
    //车牌号不能包含的字符
    public static final String[] carNotLetter = {"I","O"};


    //车架号的组成字母  不包含'I','O','Q'
    public static final String[] carFrameLetter = {"A","B","C","D","E","F","G","H","J","K","L","M","N","P","R","S","T","U","V","W","X","Y","Z"};
    public static final Map<String,Integer> carFrameLetterMap = new HashMap<String, Integer>(){{
        put("A",0);put("B",1);put("C",2);put("D",3);put("E",4);put("F",5);put("G",6);put("H",7);put("J",8);
        put("K",9);put("L",10);put("M",11);put("N",12);put("P",13);put("R",14);put("S",15);put("T",16);put("U",17);
        put("V",18);put("W",19);put("X",20);put("Y",21);put("Z",22);
    }};
    //车架号的组成 数字
    public static final String[] carFrameNumber = {"0","1","2","3","4","5","6","7","8","9"};
    //车架号不能包含的字符
    public static final String[] carNotFrameLetter = {"I","O","Q"};

    //车牌号中的汉字
    public static final List<String> carWithChinese = new ArrayList<String>(){{
        add("警");add("学");add("港");add("澳");add("领");add("挂");
    }};

}
