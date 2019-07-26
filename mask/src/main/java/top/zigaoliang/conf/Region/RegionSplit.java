package top.zigaoliang.conf.Region;

import org.apache.log4j.Logger;
import top.zigaoliang.util.CommonUtil;

/**
 *   切分字符串得到地址的省市县 镇 等信息
 */
public class RegionSplit {
    private static Logger log = Logger.getLogger(RegionSplit.class.getSimpleName());

    public String proviceName = "";
    public String cityName = "";
    public String countyName = "";
    public String townName = "";
    public String villageName = "";
    public String streetName = "";

    public String address = "";

    public RegionSplit(String address){
        this.address = address;
        proviceName = getProvice();
        cityName = getCity();
        countyName = getCounty();
        townName = getTown();
        villageName  = getVilage();
        streetName = getStreet();
    }

    /**
     * 获得地址的省份 或者直辖市名称
     * @return  省或者直辖市的名称
     */
    public String getProvice() {
        int index = address.indexOf(AdressCode.proviceName[0]);
        String result = "";
        if (index != -1) {
            result = address.substring(0,index + AdressCode.proviceName[0].length());
            address = address.substring(index + AdressCode.proviceName[0].length());
            return result;
        } else {
            index = address.indexOf(AdressCode.proviceName[1]);
            if (index != -1) {
                result = address.substring(0,index + AdressCode.proviceName[1].length());
                address = address.substring(index + AdressCode.proviceName[1].length());
                return result;
            } else {
                String proviceCity = CommonUtil.getDomFromStr(address, AdressCode.proviceCity);
                if (proviceCity != null) {
                    result = address.substring(0,address.indexOf(proviceCity) + proviceCity.length() + 1);
                    address = address.substring(address.indexOf(proviceCity) + proviceCity.length() + 1);
                    return result;
                }
            }
        }
        return "";
    }

    /**
     * 获得地址的市信息
     * @return
     */
    public String getCity(){
        if(address == ""){
            return "";
        }
        //如果山西省市辖区  返回的市为 ""
        if( address.startsWith("市辖区") || address.startsWith("市辖县")){
            return  "";
        }
        String cityKeyWord = CommonUtil.getDomFromStr(address,AdressCode.cityName);

        if(cityKeyWord == null){
            return "";
        }
        String cityName = address.substring(0,address.indexOf(cityKeyWord) +1);
        //针对  兰州，北市这种含有市关键字单不是一个市的情况
        if(cityName.length() < 3){
            return "";
        }
        address = address.substring(address.indexOf(cityKeyWord) +1);
        return cityName;
    }



    //获得地址的县 (区县（旗、林区）)
    public String getCounty() {
        if (address == "") {
            return "";
        }
        String countyKeyWord = CommonUtil.getDomFromStr(address, AdressCode.countyName);
        if (countyKeyWord == null) {
            return "";
        }
        String countyName = address.substring(0, address.indexOf(countyKeyWord) + countyKeyWord.length());
        address = address.substring(address.indexOf(countyKeyWord) + countyKeyWord.length());
        return countyName;
    }

    //获得地区的镇信息
    public String getTown() {
        if (address == "") {
            return "";
        }
        String townKeyWord = CommonUtil.getDomFromStr(address, AdressCode.townName);
        if (townKeyWord == null) {
            return "";
        }
        String townName = address.substring(0, address.indexOf(townKeyWord) + townKeyWord.length());
        address = address.substring(address.indexOf(townKeyWord) + townKeyWord.length());
        return townName;
    }

    //获得地区的村信息
    public String getVilage(){
        if(address == ""){
            return "";
        }
        String villageKeyWord = CommonUtil.getDomFromStr(address, AdressCode.villageName);
        if(villageKeyWord == null){
            return "";
        }
        String villageName = address.substring(0, address.indexOf(villageKeyWord) + villageKeyWord.length());
        address = address.substring(address.indexOf(villageKeyWord) + villageKeyWord.length());
        return villageName;
    }

    //获得地区的路 街道 巷信息
    public String getStreet(){
        return address;
    }

}
