package top.zigaoliang.conf.Region;


import java.util.List;

/**
 * 座机号区号编码
 */
public class TelePhone {
    private String  areaName;  //座机号地区的名字
    private String  areaCode;  //座机号区号编码

    /**
     *  二分查找集合中是否包含某个元素   根据编号
     * @return
     */
    public static int arraysearchByCode(List<TelePhone> telePhoneList, String code) {
        if(telePhoneList.size() == 0){
            return  -1;
        }
        int mid = telePhoneList.size()/2;
        if(code == telePhoneList.get(mid).getAreaCode()){
            return mid;
        }
        int start = 0;
        int end = telePhoneList.size() -1;
        while(start <= end){
            mid = (end - start) /2 +start;
            if(Integer.parseInt(code) < Integer.parseInt(telePhoneList.get(mid).getAreaCode())){
                end = mid -1;
            }else if(Integer.parseInt(code) > Integer.parseInt(telePhoneList.get(mid).getAreaCode())){
                start = mid + 1;
            }else{
                return mid;
            }
        }
        return -1;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
