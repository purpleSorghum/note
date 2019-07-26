package top.zigaoliang.conf.Region;

import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class RegionBase {

    public  String code;  //编号  包括父级的编号 如：4213

    protected String currentCode;  //当前节点的编号

    protected String name;      //地区名称 如：广西壮族自治区

    protected String parentCode;  //父级地区编号 42

    protected String sname;     //名称简写 如：广西

    /**
     * 重写比较大小的方法 按照汉字的拼音首字母的顺序比较
     * 主要为二分查找提供条件
     * @return
     */
    public int compareToName(RegionBase regionBase) {
        Collator instance = Collator.getInstance(Locale.CHINA);
        return instance.compare(this.name,regionBase.getName());
    }
//
//    public int compareToCode(RegionBase regionBase){
//        Collator instance = Collator.getInstance();
//        return instance.compare(this.code,regionBase.getCode());
//    }




    /**
     *  二分查找集合中是否包含某个元素   根据省的编号查找
     * @param obj
     * @param code
     * @return
     */
    public static int arraysearchByCode(Object obj, String code) {

        List<RegionBase> cityList = (List<RegionBase>) obj;

        if(cityList.size() == 0){
            return  -1;
        }
        int mid = cityList.size()/2;
        if(code == cityList.get(mid).getCode()){
            return mid;
        }
        int start = 0;
        int end = cityList.size() -1;
        while(start <= end){

            mid = (end - start) /2 +start;

            if(Integer.parseInt(code) < Integer.parseInt(cityList.get(mid).getCode())){

                end = mid -1;

            }else if(Integer.parseInt(code) > Integer.parseInt(cityList.get(mid).getCode())){

                start = mid + 1;

            }else{

                return mid;
            }
        }
        return -1;
    }

    /**
     *  二分查找集合中是否包含某个元素   根据region的name查找
     * @param name
     * @return
     */
    public static int arraysearchByName(Object obj, String name) {
        List<RegionBase> baseList = (List<RegionBase>) obj;
        RegionBase regionBase = new RegionBase();
        regionBase.setName(name);
        if (baseList.size() == 0) {
            return -1;
        }
        int mid = baseList.size() / 2;
        if (regionBase == baseList.get(mid)) {
            return mid;
        }
        int start = 0;
        int end = baseList.size() - 1;
        while (start <= end) {
            mid = (end - start) / 2 + start;
            if (regionBase.compareToName(baseList.get(mid)) < 0) {
                end = mid - 1;
            } else if (regionBase.compareToName(baseList.get(mid)) > 0) {
                start = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}