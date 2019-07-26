package top.zigaoliang.conf.Region;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 对省市县等集合排序  按照名称的拼音首字母的顺序 方便二分查找
 */
public class SortRegion {
    /**
     * sortType = true   将地域按照code进行排序
     * sortType = false   将地域按照name进行排序
     * @param sortType
     */
    public static void sort(boolean sortType){
        sortRegionList(sortType, AdressCode.proviceList);
        sortRegionList(sortType, AdressCode.cityList);
        sortRegionList(sortType, AdressCode.countyList);
//        sortRegionList(sortType, AdressCode.townList);
//        sortVillageList(sortType, AdressCode.streetList);
    }



    public static void sortRegionList(boolean sortType, Object obj){
        List<RegionBase> regionBaseList = (List<RegionBase>) obj;
        if(sortType){
            sortRegionByCode(regionBaseList);
        }else{
            sortRegionByName(regionBaseList);
        }
    }
    //对villageList进行排序  排序规则按照镇的village值的汉字拼音首字母出现的顺序
    //方便二分查找
    public static void sortVillageList(boolean sortType,Object obj){
        List<RegionBase> regionBaseList = (List<RegionBase>) obj;
        if(!sortType){
            sortRegionByName(regionBaseList);
        }
    }

    /**
     * 根据地区名称进行排序
     */
    public static void sortRegionByName(Object obj){
        List<RegionBase> regionBaseList = (List<RegionBase>) obj;
        Collections.sort(regionBaseList, new Comparator<RegionBase>() {
            @Override
            public int compare(RegionBase regionBase1, RegionBase regionBase2) {
                Collator instance = Collator.getInstance(Locale.CHINA);
                return instance.compare(regionBase1.getName(), regionBase2.getName());
            }
        });
    }

    /**
     * 根据地区编号进行排序
     */
    public static void sortRegionByCode(Object obj){
        List<RegionBase> regionBaseList = (List<RegionBase>) obj;
        //按照地域编号进行排序
        Collections.sort(regionBaseList, new Comparator<RegionBase>() {
            @Override
            public int compare(RegionBase regionBase1, RegionBase regionBase2) {
                if (Integer.parseInt(regionBase1.getCode()) > Integer.parseInt(regionBase2.getCode())) {
                    return 1;
                }
                if (Integer.parseInt(regionBase1.getCode()) == Integer.parseInt(regionBase2.getCode())) {
                    return 0;
                }
                return -1;
            }
        });
    }

}
