package top.zigaoliang.conf.Region;

import org.apache.commons.lang3.StringUtils;
import top.zigaoliang.common.FileHelper;
import top.zigaoliang.contant.RegionContants;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdressCode {
    //只存放省市县地区编号
    public static List<Provice> proviceCodeList = new ArrayList<Provice>();
    public static List<Provice> proviceList = new ArrayList<>();
    public static List<City> cityList = new ArrayList<>();
    public static List<County> countyList = new ArrayList<>();
    public static List<Town> townList = new ArrayList<>();
    public static List<Village> villageList = new ArrayList<>();
    public static List<Street> streetList = new ArrayList<>();

    public static IndexMapList proviceMapList = null;
    public static IndexMapList cityMapList = null;
    public static IndexMapList countyMapList = null;

    static {
        proviceMapList = HashMapUtil.convertToIndexMap("/prov.txt",Provice.class,"name");
        cityMapList = HashMapUtil.convertToIndexMap("/city.txt",City.class,"name");
        countyMapList = HashMapUtil.convertToIndexMap("/county.txt",County.class,"name");
    }
    static {
        //建立省市县编号的数据结构三维数组(集合)结构，方便二分查找
        if(AdressCode.proviceCodeList.size() == 0){
            Arrays.sort(RegionContants.regionCodeArray);
            AdressCode.initRegionCode();
        }
    }

    public static void init() {
        Arrays.sort(addressMinKeyword);
        Arrays.sort(townName);
        //这三个方法的调用顺序是固定的
        readResource();
        SortRegion.sort(true);
        initRegionInfor();
        SortRegion.sort(false);
    }

    public static RegionSplit regionSplit = null;

    //省的同级别的别称
    public static String[] proviceName = {"省", "自治区"};

    //市的同等级的别称
    public static String[] cityName = {"市", "州", "盟"};

    //县的同等级的别称
    public static String[] countyName = {"林区", "县", "旗", "区", "市"};

    //直辖市
    public static String[] proviceCity = {"北京", "天津", "上海", "重庆"};

    //镇的同一级别的别称
    public static String[] townName = {"镇", "街道", "乡"};

    //村的同一级别的别称
    public static String[] villageName = {"村","村镇"};

    //详细家庭地址的关键字
    public static String[] addressMinKeyword = {"里","横", "街", "道","坑","桥","厦", "总","庄","岗","河","屯","段", "弄", "分",
            "组","行", "局", "所", "院","寓","期","宅","区","巷", "路", "大队", "农场", "胡同", "湾", "坪", "山", "岭","苑", "公馆",
            "庭", "府","墅", "亭", "国际", "广场", "居", "江畔","楼", "塔", "城", "园","栋", "大学", "中学", "小学", "学校", "厂",
            "第","洞", "单元", "门","房", "号", "室", "层","机场"};


    //根据省份 随机获得一个市编号
    public static String getCityCodeByProvice(String proviceCode) {
        Provice currentProvice = proviceCodeList.get(RegionBase.arraysearchByCode(proviceCodeList, proviceCode));
        List<City> cityList = currentProvice.getCityList();
        return cityList.get(Util.getNumByRange(0, cityList.size() - 1)).getCode();
    }


    //根据县和市随机生成一个县code
    public static String getCountyCodeByCity(String proviceCode, String cityCode) {
        Provice currentProvice = proviceCodeList.get(RegionBase.arraysearchByCode(proviceCodeList, proviceCode));
        List<City> cityList = currentProvice.getCityList();
        for (City city : cityList) {
            if (city.getCode().equals(cityCode)) {
                List<County> countyList = city.getCountyList();
                return countyList.get(Util.getNumByRange(0, countyList.size() - 1)).getCode();
            }
        }
        return null;
    }

    /**
     * 读取静态资源文件
     */
    public static void readResource() {
        proviceList = FileHelper.readSource("/prov.txt", Provice.class);
        cityList = FileHelper.readSource("/city.txt", City.class);
        countyList = FileHelper.readSource("/county.txt", County.class);
        townList = FileHelper.readSource("/town.txt", Town.class);
        villageList = FileHelper.readSource("/village.txt", Village.class);
        streetList = FileHelper.readSource("/street.txt", Street.class);
    }

    /**
     * 加载地域信息
     * 读取地址文件中的地址信息 解析数据结构并封装到对象中
     *
     * @return
     */
    public static void initRegionInfor() {
        //对镇进行遍历放到对应的县对象中
        for (Town townTemp : townList) {
            //在地域的集合还没有按照地域的name进行排序之前 根据code进行二分查找
            int countyIndex = RegionBase.arraysearchByCode(countyList, townTemp.getParentCode());
            countyList.get(countyIndex).getTownList().add(townTemp);
        }
        //对市进行遍历放到对应的省对象中
        for (City cityTemp : cityList) {
            int proviceIndex = RegionBase.arraysearchByCode(proviceList, cityTemp.getParentCode());
            proviceList.get(proviceIndex).getCityList().add(cityTemp);
        }
        //对县进行遍历放到对应的市对象中
        for (County countyTemp : countyList) {
            int cityIndex = RegionBase.arraysearchByCode(cityList, countyTemp.getParentCode());
            cityList.get(cityIndex).getCountyList().add(countyTemp);
        }
    }


    public static void initRegionCode() {
        for (int i = 0; i < RegionContants.regionCodeArray.length; i++) {
            String str = Integer.toString(RegionContants.regionCodeArray[i]);
            String prov = str.substring(0, 2);
            int pos = RegionBase.arraysearchByCode(proviceCodeList, prov);
            if (pos == -1) {
                //省份集合中没有这个元素
                Provice provice = new Provice();
                provice.setCode(prov);
                City city = new City();
                city.setCode(str.substring(2, 4));
                County county = new County();
                county.setCode(str.substring(4, 6));
                city.getCountyList().add(county);
                provice.getCityList().add(city);
                proviceCodeList.add(provice);
            } else {
                //省份集合中有这个元素
                Provice provice = proviceCodeList.get(pos);
                String ci = str.substring(2, 4);
                //判断某个省份集合里面是否有这个市
                int cityIndex = RegionBase.arraysearchByCode(provice.getCityList(), ci);
                if (cityIndex == -1) {
                    //省份中的城市集合中没有这个元素
                    City city = new City();
                    city.setCode(ci);
                    County county = new County();
                    county.setCode(str.substring(4, 6));
                    city.getCountyList().add(county);
                    provice.getCityList().add(city);
                } else {
                    //省份中的城市集合中有这个元素
                    //对省下的市下的县进行判断
                    City city = proviceCodeList.get(pos).getCityList().get(cityIndex);
                    String co = str.substring(4, 6);
                    //判断市里面有没有这个县
                    int countyIndex = RegionBase.arraysearchByCode(city.countyList, co);
                    if (countyIndex == -1) {
                        //城市集合中没有这个县
                        County county = new County();
                        county.setCode(co);
                        city.getCountyList().add(county);
                        provice.getCityList().add(city);
                    } else {
                        //城市的集合中有这个县
                        continue;
                    }
                }
            }
        }
    }

    //判断省份是否合法
    public static boolean proviceCheck() {
        return HashMapUtil.containsKey(proviceMapList.getMap(),regionSplit.proviceName);
    }

    /**
     * 对省进行随机脱敏
     * 脱敏成字数相等的一个省  那么省下的市县等都得脱
     * @return
     */
    public static String[] getRandomProvice() {
        Provice provice = null;
        if (StringUtils.isNotBlank(regionSplit.proviceName)) {
            provice = findRandomProvice();
        }
        City city = null;
        if (StringUtils.isNotBlank(regionSplit.cityName)) {
            city = provice == null?getRandomFromCityList():getCityByProvice(provice);
        }

        County county = null;
        if (StringUtils.isNotBlank(regionSplit.countyName)) {
            if (city == null) {
                county = provice == null?getCountyFromCountyList():getCountyByProvice(provice);
            } else {
                //在指定的市下找一个县
                county = getCountyByCity(city);
            }
        }

        String randomProice = provice == null?"":provice.getName();
        String randomCity = city == null?"":city.getName();
        String randomCounty = county == null?"":county.getName();
        String[] regionRandom = {randomProice, randomCity, randomCounty};
        return regionRandom;
    }

    //随机获得一个省
    public static Provice findRandomProvice(){
        Provice provice = null;
        int proviceSum = 0;
        do {
            proviceSum += 1;
            int proviceIndex = Util.getNumByRange(0, proviceList.size() - 1);
            provice = proviceList.get(proviceIndex);
            if(proviceSum>=5){
                break;
            }
        } while (provice.getName().length() > regionSplit.proviceName.length() && proviceSum <= 5);
        if (proviceSum >= 5) {
            int indexTemp = RegionBase.arraysearchByName(proviceList,regionSplit.proviceName);
            if(indexTemp > 0){
                provice = proviceList.get(indexTemp);
            }
        }
        return provice;
    }

    //市集合下随机找一个市
    public static City getRandomFromCityList(){
        City city = null;
        int sumCity = 0;
        do {
            sumCity += 1;
            int cityIndex = Util.getNumByRange(0, cityList.size() - 1);
            city = cityList.get(cityIndex);
            if(sumCity>=5){
                break;
            }
        } while (city.getName().length() > regionSplit.cityName.length());
        if (sumCity >= 5) {
            int indexTemp = RegionBase.arraysearchByName(cityList, regionSplit.cityName);
            if (indexTemp > 0) {
                city = cityList.get(indexTemp);
            }
        }
        return city;
    }

    //县集合中随机找一个县
    public static County getCountyFromCountyList(){
        County county = null;
        int sumCounty = 0;
        do {
            sumCounty += 1;
            int index = Util.getNumByRange(0, countyList.size() - 1);
            county = countyList.get(index);
            if(sumCounty>=5){
                break;
            }
        } while (county.getName().length() > regionSplit.countyName.length()
                 || county.getName().equals("市辖区") || county.getName().equals("市辖县") ||
                county.getName().equals("县")
        );
        if(sumCounty >= 5){
            int indexTemp = RegionBase.arraysearchByName(countyList, regionSplit.countyName);
            if (indexTemp > 0) {
                county = countyList.get(indexTemp);
            }
        }
        return county;
    }

    //在指定的省下随机找一个市
    public static City getCityByProvice(Provice provice) {
        City city = null;
        int citySum = 0;
        do {
            citySum += 1;
            int cityIndex = Util.getNumByRange(0, provice.getCityList().size() - 1);
            city = provice.getCityList().get(cityIndex);
            if(citySum>=5){
                break;
            }
        }while (city.getName().length() > regionSplit.cityName.length() && citySum <= 5);
        if(citySum >= 5){
            int indexTemp = RegionBase.arraysearchByName(cityList, regionSplit.cityName);
            if (indexTemp > 0) {
                city = cityList.get(indexTemp);
            }
        }
        return city;
    }
    //在指定的省下随机找一个市
    public static String getCityByProvice(String proviceName,String cityName){
        if(StringUtils.isBlank(cityName)){
            return "";
        }
        Provice provice = getProviceByName(proviceName);
        return getCityByProvice(provice).getName();
    }

    // 省下随机找到一个县
    public static County getCountyByProvice(Provice provice){
        County county = null;
        int cityIndex = Util.getNumByRange(0, provice.getCityList().size() - 1);
        int countyIndex = Util.getNumByRange(0,provice.getCityList().get(cityIndex).getCountyList().size()-1);
        county = provice.getCityList().get(cityIndex).getCountyList().get(countyIndex);
        return county;
    }
    public static String getCountyByProvice(String proviceName){
        Provice provice = getProviceByName(proviceName);
        County county = getCountyByProvice(provice);
        return county.getName();
    }

    //在指定的市下找一个县
    public static County getCountyByCity(City city){
        County county = null;
        int countyIndex = Util.getNumByRange(0,city.getCountyList().size()-1);
        county = city.getCountyList().get(countyIndex);
        return county;
    }
    public static String getCountyByCity(String cityName){
        City city = getCityByName(cityName);
        County county = getCountyByCity(city);
        if(county != null){
            return county.getName();
        }
        return "";
    }

    //镇集合中随机找一个镇
    public static Town getTownByTownLit(){
        Town town = null;
        int sumTown = 0;
        do {
            sumTown += 1;
            int index = Util.getNumByRange(0, townList.size() - 1);
            town = townList.get(index);
            if(sumTown>=5){
                break;
            }
        } while (town.getName().length() > regionSplit.townName.length() && sumTown  <= 5);
        if(sumTown >= 5){
            int indexTemp = RegionBase.arraysearchByName(townList, regionSplit.townName);
            if (indexTemp > 0) {
                town = townList.get(indexTemp);
            }
        }
        return town;
    }
    public static String getTownByTownLit(String townName){
        if(StringUtils.isBlank(townName)){
            return "";
        }else{
            return  getTownByTownLit().getName();
        }
    }

    //在指定的省和市下随机找一个县
    public static String getCountyByProAndCity(String proviceName, String cityName, String countyName) {
        if(StringUtils.isBlank(countyName)){
            return "";
        }
        Provice provice = null;
        County county = null;
        City city = null;
        if(StringUtils.isNotBlank(cityName)){
            city = getCityByName(cityName);
        }
        if(city != null){
            county = getCountyByCity(city);
        }else{
            //从省里面找到一个县
            provice = getProviceByName(proviceName);
            if(provice != null){
                county = getCountyByProvice(provice);
            }
        }
        return county.getName();
    }

    //随机找一个村
    public static Village getRandomVillage() {
        Village village = null;
        int villageSum = 0;
        do {
            villageSum += 1;
            int villageIndex = Util.getNumByRange(0, villageList.size() - 1);
            village = villageList.get(villageIndex);
            if(villageSum>=5){
                break;
            }
        } while (village.getName().length() > regionSplit.villageName.length() && villageSum <= 5);
        if(villageSum >= 5){
            int indexTemp = RegionBase.arraysearchByName(villageList, regionSplit.villageName);
            if (indexTemp > 0) {
                village = villageList.get(indexTemp);
            }
        }
        return village;
    }
    public static String getRandomVillage(String villageName){
        if(StringUtils.isBlank(villageName)){
            return "";
        }else{
            return getRandomVillage().getName();
        }
    }

    //随机找一条街
    public static String getRandomSteet() {
        //判断地址是否存在数字  如果是数字  只对数字进行随机脱敏
        if (CommonUtil.containNumber(regionSplit.streetName)) {
            return masktreetNumber(regionSplit.streetName, "random", 0, true);
        } else {
            return getRandomStreetNoNumber(regionSplit.streetName);
        }
    }
    public static String getRandomSteet(String streetName){
        if(StringUtils.isBlank(streetName)){
            return  "";
        }else{
            return getRandomSteet();
        }
    }

    //根据省名找到该省对象
    public static Provice getProviceByName(String proviceName){
        Provice provice = null;
        int proIndex = RegionBase.arraysearchByName(proviceList,proviceName);
        if(proIndex > 0){
            provice = proviceList.get(proIndex);
        }
        return provice;
    }
    //根据市名找到该市对象
    public static City getCityByName(String cityName){
        City city = null;
        int cityIndex = RegionBase.arraysearchByName(cityList,cityName);
        if(cityIndex > 0){
            city = cityList.get(cityIndex);
        }
        return city;
    }
    //根据县名找到县对象
    public County getCountyByName(String countyName){
        County county = null;
        int countyIndex = RegionBase.arraysearchByName(countyList,countyName);
        if(countyIndex > 0){
            county = countyList.get(countyIndex);
        }
        return county;
    }
    //根据镇名找到镇
    public Town getTownByName(String townName){
        Town town = null;
        int townIndex = RegionBase.arraysearchByCode(townList,townName);
        if(townIndex > 0){
            town = townList.get(townIndex);
        }
        return town;
    }

    //对含有数字的详细地址进行随机脱敏
    /**
     * @param street
     * @param type   "random"  随机   "mask" 可逆
     * @return
     */
    public static String masktreetNumber(String street, String type, int seed, boolean flag) {
//        street = street.replaceAll(" ", "");
        StringBuilder streetResult = new StringBuilder(street);
        List<Integer> numberIndex = new ArrayList<>();
        List<String> numberRandom = new ArrayList<>();
        char[] charArray = street.toCharArray();
        StringBuilder result = new StringBuilder();
        int temp = 0;
        for (int i = 0; i < charArray.length; i++) {
            if (CommonUtil.isChineseContain(charArray[i]) != 2 && CommonUtil.isChineseContain(charArray[i]) != 3) {
                temp = 0;
                result.append(" ");
            } else {
                temp += 1;
                if (temp == 1) {
                    numberIndex.add(i);
                }
                result.append(charArray[i]);
            }
        }
        String[] addressNumber = result.toString().split("\\s+");
        for (int i = 0; i < addressNumber.length; i++) {
            if(StringUtils.isBlank(addressNumber[i])){
                continue;
            }
            String numberTemp = "";
            String str = addressNumber[i];
            if (type.equals("random")) {
                if (CommonUtil.isChineseContain(addressNumber[i].charAt(0)) == 3) {
                    numberTemp = Util.getIntegerNoZeroFirst(addressNumber[i].length());
                } else if (CommonUtil.isChineseContain(addressNumber[i].charAt(0)) == 2) {
                    numberTemp = Util.getRandomNumberChinese(addressNumber[i].length());
                }
            }
            if (type.equals("mask")) {
                for (int m = 0; m < str.length(); m++){
                    if (CommonUtil.isChineseContain(str.charAt(m)) == 3) {
                        numberTemp += AlgoMaskUtil.maskNumberStr(str.charAt(m) + "", seed, flag);
                    } else if (CommonUtil.isChineseContain(str.charAt(m)) == 2) {
                        numberTemp += AlgoMaskUtil.maskNumberChineseStr(str.charAt(m) + "", seed, flag);
                    }
                }
            }
            numberRandom.add(numberTemp);
        }

        for (int i = 0; i < numberIndex.size(); i++) {
            streetResult.replace(numberIndex.get(i), numberIndex.get(i) + numberRandom.get(i).length(), numberRandom.get(i));
        }
        return streetResult.toString();
    }


    public static String getRandomStreetNoNumber(String street) {
        String streetResult = "";
        int streetSum = 0;
        do {
            streetSum += 1;
            int streetIndex = Util.getNumByRange(0, streetList.size() - 1);
            streetResult = streetList.get(streetIndex).getName();
            if(streetSum>=5){
                break;
            }
        } while (streetResult.length() > street.length() && streetSum <= 5);
        if(streetSum >= 5){
            return regionSplit.streetName;
        }
        return streetResult;
    }

    public static String maskTwon(String twonName, int seed, boolean flag) {
        return StringUtils.isBlank(twonName) == true? "":maskTwonToRandomTwon(twonName, seed, flag);
    }

    public static String maskVillage(String villageName, int seed, boolean flag) {
        return StringUtils.isBlank(villageName) == true? "":maskVillageToRandomVillage(villageName, seed, flag);
    }


    /**
     * 对街道脱敏 不考虑级联关系 脱敏后的街道或者详细地址不再它的上级镇或村下
     * 含有数字
     * 2号楼2单元403室 -> 3号楼4单元443室
     *
     * @return
     */
    public static String maskStreet(String streetName, int seed, boolean flag) {
        //含有数字
        //     * 2号楼2单元403室 -> 3号楼4单元443室
        if (StringUtils.isBlank(streetName)) {
            return "";
        }
        if (CommonUtil.containNumber(streetName)) {
            return masktreetNumber(streetName, "mask", seed, flag);
        } else {
            return AlgoMaskUtil.maskChinese(streetName.substring(0, streetName.length()-1), seed, flag) +
                    streetName.substring(streetName.length() - 1);
        }
    }




    //将一个镇脱敏成一个随机汉字组成的镇
    public static String maskTwonToRandomTwon(String town, int seed, boolean flag) {
        StringBuilder resultTown = new StringBuilder();
        String[] splitTown = CommonUtil.splitByArray(town, townName);
        for (int i = 0; i < splitTown.length; i += 2) {
            resultTown.append(AlgoMaskUtil.maskChineseForAddr(splitTown[i], seed, flag))
                        .append(splitTown[i + 1]);
        }
        return resultTown.toString();
    }

    //将一个村脱敏成一个随机汉字组成的村
    public static String maskVillageToRandomVillage(String village, int seed, boolean flag) {
        String[] villageName = {"村"};
        StringBuilder resultTown = new StringBuilder();
        String[] splitTown = CommonUtil.splitByArray(village, villageName);
        resultTown.append(AlgoMaskUtil.maskChinese(splitTown[0], seed, flag))
                .append(splitTown[1]);
        return resultTown.toString();
    }

    //根据县名称查找县对象
    public static County getCountyByname(String countyName) {
        County county = null;
        for (int i = 0; i < countyList.size(); i++) {
            if (countyList.get(i).getName().equals(countyName)) {
                county = countyList.get(i);
            }
        }
        return county;
    }

    //判断某省下有没有该市
    public static boolean proviceHasCity(){
        Provice provice = null;

        int proviceIndex = RegionBase.arraysearchByName(proviceList,regionSplit.proviceName);

        if(proviceIndex >= 0){
            provice = proviceList.get(proviceIndex);
        }
        for(int i = 0; i < provice.getCityList().size(); i++){
            if(provice.getCityList().get(i).getName().equals(regionSplit.cityName)){
                return true;
            }
        }
        return false;
    }
    //判断该市是否合法
    public static boolean cityCheck(){
        return HashMapUtil.containsKey(cityMapList.getMap(),regionSplit.cityName);
    }

    //判断该市下有没有该县
    public static boolean cityHasCounty(){
        City city = null;
        int cityIndex = RegionBase.arraysearchByName(cityList,regionSplit.cityName);
        if(cityIndex > 0){
            city = cityList.get(cityIndex);
        }
        for(int i = 0; i < city.getCountyList().size(); i ++){
            if(city.getCountyList().get(i).getName().equals(regionSplit.countyName)){
                return true;
            }
        }
        return false;
    }


    //判断某省下有没有该县
    public static boolean proviceHasCounty(){
            if(regionSplit.countyName.equals("市辖区") || regionSplit.countyName.equals("市辖县")){
                return true;
            }
            County county = null;
            int countyIndex = RegionBase.arraysearchByName(countyList, regionSplit.countyName);
            if (countyIndex > 0) {
                county = countyList.get(countyIndex);
            }
            if (county == null) {
                return false;
            }
            City city = null;
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).getCode().equals(county.getParentCode())) {
                    city = cityList.get(i);
                }
            }
            if (city == null) {
                return false;
            }
            Provice provice = null;
            int proviceIndex = RegionBase.arraysearchByName(proviceList, regionSplit.proviceName);
            if (proviceIndex > 0) {
                provice = proviceList.get(proviceIndex);
            }
            for (int i = 0; i < provice.getCityList().size(); i++) {
                if (provice.getCityList().get(i).getName().equals(city.getName())) {
                    return true;
                }
            }
        return false;
    }


    //判断该县是否合法
    public static boolean countyCheck(){
        int countIndex = RegionBase.arraysearchByName(countyList,regionSplit.countyName);
        if(countIndex <  0){
            return false;
        }
        return  true;
    }

    //判断一个镇是否合法
    public static boolean townCheck(){
        if(regionSplit.townName.length() > 10){
            return false;
        }
        if(!CommonUtil.endByKeyword(townName,regionSplit.townName)){
            return false;
        }
        return true;
    }

    //判断一个村是否合法
    public static boolean villageCheck(){
        if(regionSplit.villageName.length() > 10){
            return false;
        }
        if(!regionSplit.villageName.endsWith("村")){
            return false;
        }
        return true;
    }

    //判断一个街道是否合法
    public static boolean streetCheck(){
        if(regionSplit.streetName.length() > 40){
            return false;
        }
        if(!CommonUtil.endByKeyword(addressMinKeyword,regionSplit.streetName)){
            if(computeAddressNumber() < 2){
                //不是以关键字结尾的，并且 满足"省","市","县","镇","村"的地区的个数为 <2, 不是地址
                return false;
            }
        }
        return true;
    }

    //计算满足"省","市","县","镇","村"的地区的个数
    public static int computeAddressNumber(){
        int sum = 0;
        if(StringUtils.isNotBlank(regionSplit.proviceName)){
            sum ++;
        }
        if(StringUtils.isNotBlank(regionSplit.cityName)){
            sum ++;
        }
        if(StringUtils.isNotBlank(regionSplit.countyName)){
            sum ++;
        }
        if(StringUtils.isNotBlank(regionSplit.townName)){
            sum ++;
        }
        if(StringUtils.isNotBlank(regionSplit.villageName)){
            sum ++;
        }
        return sum;
    }



    public static boolean singleRegion(){
        boolean proviceNotBlank = StringUtils.isNotBlank(regionSplit.proviceName);
        if (StringUtils.isNotBlank(regionSplit.proviceName)) {
            return proviceCheck();
        }

        boolean cityNotBlank = StringUtils.isNotBlank(regionSplit.cityName);
        if (cityNotBlank) {
            return proviceNotBlank?proviceHasCity():cityCheck();
        }

        boolean countyNotBlank = StringUtils.isNotBlank(regionSplit.countyName);
        if (countyNotBlank) {
            if (cityNotBlank) {
                //判断该市下有没有该县
                return cityHasCounty();
            } else {
                //判断某省下有没有该县
                return proviceNotBlank?proviceHasCounty():countyCheck();
            }
        }
        //判断一个镇是否合法
        if (StringUtils.isNotBlank(regionSplit.townName)) {
            return townCheck();
        }
        //判断一个村是否合法
        if (StringUtils.isNotBlank(regionSplit.villageName)) {
            return villageCheck();
        }
        //判断一个街道是否合法
        /**
         *   如果不是按照特殊字符结尾的
         *   省市县镇村  满足两个不为空，就认为是地址
         */
        if (StringUtils.isNotBlank(regionSplit.streetName)) {
            return streetCheck();
        }
        return true;
    }

}
