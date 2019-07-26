package top.zigaoliang.util;


import top.zigaoliang.conf.FundCodeName;

/**
 * 基金名称
 */
public class FundName {
    private String code;

    private static  IndexMapList dictIndMapList;
    static{
        dictIndMapList =   HashMapUtil.convertToIndexMap("/fundcode.txt",FundCodeName.class,"name");
    }

    public static FundName create(String code) {
        return new FundName(code);
    }

    private FundName(String code) {
        this.code = code;
    }

    public boolean isValid() {
//        String temp = fundNameRemoveAbb(this.code);
        return HashMapUtil.containsKey(dictIndMapList.getMap(),this.code);
    }

    public FundName random() {
        int seed = (int) (Math.random() * dictIndMapList.getList().size());
        return mask(seed);
    }

    public FundName mask(long seed) {
        int index = HashMapUtil.getMapValue(dictIndMapList.getMap(),this.code);
        int maskIndex = (int) MaskUtil.maskIndex(seed, dictIndMapList.getList().size(), index);
        this.code = dictIndMapList.getList().get(maskIndex);
        return this;
    }

    public FundName unMask(long seed) {
        int index = HashMapUtil.getMapValue(dictIndMapList.getMap(),this.code);
        int maskIndex = (int) MaskUtil.unMaskIndex(seed, dictIndMapList.getList().size(), index);
        this.code = dictIndMapList.getList().get(maskIndex);
        return this;
    }

    public String get() {
        return this.code;
    }
    //字符串去掉后面的简称
    public static String fundNameRemoveAbb(String code){
        StringBuffer out = new StringBuffer(code);
        StringBuffer temp = new StringBuffer();
        String[] charArray = out.reverse().toString().split("");
        boolean flag = false;
        for(int i = 0; i < charArray.length; i++){
            if(CommonUtil.hasChinese(charArray[i])){
                temp.append(charArray[i]);
                flag =true;
            }else{
                if(flag){
                    temp.append(charArray[i]);
                }
            }
        }
        return temp.reverse().toString();
    }
}
