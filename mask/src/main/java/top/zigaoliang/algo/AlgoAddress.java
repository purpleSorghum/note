package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Region.AdressCode;
import top.zigaoliang.conf.Region.RegionSplit;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 中文地址算法
 * Created by byc on 10/22/18.
 */
public class AlgoAddress extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoAddress.class.getSimpleName());
    public AlgoAddress() {
        super(AlgoId.ADDRESS);
        attr = 15;
    }
    static {
        AdressCode.init();
    }
    @Override
    public boolean find(String in) {
        if(in.length() < 3 || in.length() > 100){
            return false;
        }
        try {
            //得到地区的省 市 县
            RegionSplit regionSplit = new RegionSplit(in);
            AdressCode.regionSplit = regionSplit;
            //判断省份是否合法
            return AdressCode.singleRegion();
        }catch (Exception e){
            return  false;
        }
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskAddress confMaskAddress = (Conf.ConfMaskAddress)confMask;
        ErrorCode errorCode = null;
        RegionSplit regionSplit = new RegionSplit(in);
        AdressCode.regionSplit = regionSplit;
        String[] proCityCountyArray = new String[3];
        StringBuilder result = new StringBuilder();
        try {
            //1.省 保留    市 不保留   县 不保留
            if (confMaskAddress.province && !confMaskAddress.city && !confMaskAddress.county) {
                proCityCountyArray = remainProNotCityCounty(regionSplit);
            }
            //2.省 不保留    市 保留   县 不保留
            if (confMaskAddress.city && !confMaskAddress.county) {
                proCityCountyArray = remainCityNotProCounty(regionSplit);
            }
            //3.省 不保留    市 不保留   县 保留
            if (confMaskAddress.county) {
                proCityCountyArray = remainCountyNotProCity(regionSplit);
            }
            //4.省 保留    市 保留   县 不保留
            if (confMaskAddress.city && !confMaskAddress.county) {
                proCityCountyArray = remainProCityNotCounty(regionSplit);
            }
            //8.省 不保留    市 不保留   县 不保留
            if (!confMaskAddress.province && !confMaskAddress.city && !confMaskAddress.county) {
                proCityCountyArray = notProCityCounty();
            }
            //5.省 不保留    市 保留   县 保留
            if (!confMaskAddress.province && confMaskAddress.city && confMaskAddress.county) {
                proCityCountyArray = remainCityCountNotPro(regionSplit);
            }
            //6.省 保留    市 不保留   县 保留
            if (confMaskAddress.province && !confMaskAddress.city && confMaskAddress.county) {
                proCityCountyArray = remainProCountNotCity(regionSplit);
            }
            //7.省 保留    市 保留   县 保留
            if (confMaskAddress.province && confMaskAddress.city && confMaskAddress.county) {
                proCityCountyArray = remainProCityCounty(regionSplit);
            }
            // 对镇 村 街道仿真
            String randomTown = confMaskAddress.town == true ? regionSplit.townName : AdressCode.getTownByTownLit(regionSplit.townName);
            String randomVillage = confMaskAddress.village == true ? regionSplit.villageName : AdressCode.getRandomVillage(regionSplit.villageName);
            String randomStreet = confMaskAddress.street == true ? regionSplit.streetName : AdressCode.getRandomSteet(regionSplit.streetName);
            result.append(proCityCountyArray[0]).append(proCityCountyArray[1]).append(proCityCountyArray[2])
                    .append(randomTown).append(randomVillage).append(randomStreet);
            out.append(result.toString().length() > in.length()?in : result.toString());
        } catch (Exception e) {
            errorCode = ErrorCode.ADDRESS_RANDOM_UNKNOW;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskAddress confMaskAddress = (Conf.ConfMaskAddress) confMask;
        RegionSplit regionSplit = new RegionSplit(in);
        out.append(coverCommon(regionSplit.proviceName, confMaskAddress.coverProvice, confMaskAddress.symbol))
                .append(coverCommon(regionSplit.cityName, confMaskAddress.coverCity, confMaskAddress.symbol))
                .append(coverCommon(regionSplit.countyName, confMaskAddress.coverCounty, confMaskAddress.symbol))
                .append(coverCommon(regionSplit.townName, confMaskAddress.coverTown, confMaskAddress.symbol))
                .append(coverCommon(regionSplit.villageName, confMaskAddress.coverVillage, confMaskAddress.symbol))
                .append(coverCommon(regionSplit.streetName, confMaskAddress.coverStreet, confMaskAddress.symbol));
        return 0;
    }
    public String coverCommon(String in, Boolean flag,String symbol){
        if(StringUtils.isBlank(in)){
            return "";
        }
        if(!flag || in.length() ==1){
            return in;
        }
        StringBuilder result = new StringBuilder();
        result.append(CommonUtil.coverBySymbol(symbol,in.length()-1));
        result.append(in.substring(in.length()-1));
        return result.toString();
    }




    public int maskBase(String in,StringBuilder out, boolean flag){
        Conf.ConfMaskAddress confMaskAddress = (Conf.ConfMaskAddress)confMask;
        ErrorCode errorCode = null;
        RegionSplit regionSplit = new RegionSplit(in);
        StringBuilder result = new StringBuilder();
        //得到地区的省 市 县
        try{
        result.append(regionSplit.proviceName).append(regionSplit.cityName).append(regionSplit.countyName);
        //对镇进行脱敏
        result.append(AdressCode.maskTwon(regionSplit.townName,confMaskAddress.seed,flag));
        //地村进行脱敏
        result.append(AdressCode.maskVillage(regionSplit.villageName,confMaskAddress.seed,flag));
        //对具体的街道进行脱敏
        result.append(AdressCode.maskStreet(regionSplit.streetName,confMaskAddress.seed,flag));
        out.append(result.toString().length() > in.length()?in : result.toString());
        }catch (Exception e){
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * 1.省 保留    市 不保留   县 不保留
     * 2.省 不保留    市 保留   县 不保留
     * 3.省 不保留    市 不保留   县 保留
     * 4.省 保留    市 保留   县 不保留
     * 5.省 不保留    市 保留   县 保留
     * 6.省 保留    市 不保留   县 保留
     * 7.省 保留    市 保留   县 保留
     * 8.省 不保留    市 不保留   县 不保留
     */
    //1.省 保留    市 不保留   县 不保留
    public String[] remainProNotCityCounty(RegionSplit regionSplit){
        String[] regionStr = new String[3];
        regionStr[0] = regionSplit.proviceName;
        if(StringUtils.isBlank(regionSplit.cityName)){
            regionStr[1] = "";
        }else {
            if(StringUtils.isBlank(regionSplit.proviceName)){
                regionStr[1] = AdressCode.getRandomFromCityList().getName();
            }else{
                regionStr[1] = AdressCode.getCityByProvice(regionSplit.proviceName,regionSplit.cityName);
            }
        }
        if(StringUtils.isBlank(regionSplit.countyName)){
            regionStr[2] = "";
        }else{
            if(StringUtils.isNotBlank(regionStr[1])){
                //在该市下找一个县
                regionStr[2] = AdressCode.getCountyByCity(regionStr[1]);
            }else{
                if(StringUtils.isNotBlank(regionSplit.proviceName)){
                    //在该省下找一个县
                    regionStr[2] = AdressCode.getCountyByProvice(regionSplit.proviceName);
                }else{
                    //在县集合中随机找一个县
                    regionStr[2] = AdressCode.getCountyFromCountyList().getName();
                }
            }
        }
        return  regionStr;
    }


    //2.省 不保留    市 保留   县 不保留
    public String[] remainCityNotProCounty(RegionSplit regionSplit){
        String[] regionStr = new String[3];
        regionStr[1] = regionSplit.cityName;
        if (StringUtils.isBlank(regionSplit.cityName)) {
            if (StringUtils.isBlank(regionSplit.proviceName)) {
                regionStr[0] = "";
                if (StringUtils.isBlank(regionSplit.countyName)) {
                    regionStr[2] = "";
                } else {
                    regionStr[2] = AdressCode.getCountyFromCountyList().getName();
                }
            } else {
                regionStr[0] = AdressCode.findRandomProvice().getName();
                if (StringUtils.isBlank(regionSplit.countyName)) {
                    regionStr[2] = "";
                } else {
                    regionStr[2] = AdressCode.getCountyByProvice(regionStr[0]);
                }
            }
        } else {
            //市不为空
            if (StringUtils.isBlank(regionSplit.proviceName)) {
                regionStr[0] = "";
                if (StringUtils.isBlank(regionSplit.countyName)) {
                    regionStr[2] = "";
                } else {
                    regionStr[2] = AdressCode.getCountyByCity(regionSplit.cityName);
                }
            } else {
                regionStr[0] = regionSplit.proviceName;
                if (StringUtils.isBlank(regionSplit.countyName)) {
                    regionStr[2] = "";
                } else {
                    regionStr[2] = AdressCode.getCountyByCity(regionSplit.cityName);
                }
            }
        }
        return regionStr;
    }

    //3.省 不保留    市 不保留   县 保留
    public String[] remainCountyNotProCity(RegionSplit regionSplit){
        String[] regionStr = new String[3];
        regionStr[2] = regionSplit.countyName;
        regionStr[0] = regionSplit.proviceName;
        regionStr[1] = regionSplit.cityName;
        return regionStr;
    }

    //4.省 保留    市 保留   县 不保留
    public String[] remainProCityNotCounty(RegionSplit regionSplit){
        String[] regionStr = new String[3];
        regionStr[0] = regionSplit.proviceName;
        regionStr[1] = regionSplit.cityName;
        if(StringUtils.isBlank(regionSplit.countyName)){
            regionStr[2] = "";
        }else{
            if(StringUtils.isBlank(regionSplit.cityName)){
                if(StringUtils.isBlank(regionSplit.proviceName)){
                    regionStr[2] = AdressCode.getCountyFromCountyList().getName();
                }else{
                    regionStr[2] = AdressCode.getCountyByProvice(regionSplit.proviceName);
                }
            }else {
                  //市不为空，在该市下找一个县
                regionStr[2] = AdressCode.getCountyByCity(regionSplit.cityName);
            }
        }
        return regionStr;
    }

    //5.省 不保留    市 保留   县 保留
    public String[] remainCityCountNotPro(RegionSplit regionSplit){

        return remainProCountNotCity(regionSplit);
    }

    //6.省 保留    市 不保留   县 保留
    public String[] remainProCountNotCity(RegionSplit regionSplit){
        String[] regionStr = new String[3];
        regionStr[0] = regionSplit.proviceName;
        regionStr[2] = regionSplit.countyName;
        regionStr[1] = regionSplit.cityName;
        return regionStr;
    }

    //7.省 保留    市 保留   县 保留
    public String[] remainProCityCounty(RegionSplit regionSplit){
        return remainProCountNotCity(regionSplit);
    }

    //8.省 不保留    市 不保留   县 不保留
    public String[] notProCityCounty(){
        return AdressCode.getRandomProvice();
    }



    List<String> array=null;
    private List<String>  buildTestInitData(){
        if(this.confMask ==null){
            Conf.ConfMaskAddress ad = new Conf.ConfMaskAddress();
            ad.city = false;
            ad.county = false;
            ad.coverCity =false;
            ad.coverCounty = false;
            ad.coverTown = false;
            ad.coverProvice = false;
            ad.coverStreet = false;
            ad.coverVillage = false;
            ad.province =false;
            ad.street = false;
            ad.village =false;
            ad.town=false;
            ad.suffix=false;
            this.confMask = ad;
        }

        if(array==null){
            array = new ArrayList();
            array.add("安徽省龙子湖区解放街道通济办事处小李村南中十一巷");
            array.add("福建省仙游县西苑乡冲表村巷院");
            array.add("黑龙江省集贤县爱林林场令狐村县阳城大街");
            array.add("江苏省市辖区羊岩村溪龙三路");
            array.add("西藏自治区当雄县公塘乡乡小砩村铜铁厂胡同");
            array.add("北京市海淀区甘家口街道丁占自然村乡均户庄");
            array.add("湖北省鄂城区凡口街道国旗村一大队");
            array.add("宁夏回族自治区市辖区红寺堡开发区红寺堡镇镇辛冬二村十六弓路");
            array.add("河南省柘城县梁庄乡担水岭村定海三组");
            array.add("贵州省水城县发耳乡委会龙东村金湖文化中心大厦");
            array.add("江西省南丰县莱溪乡岗社区大直沽六号");
            array.add("江西省安远县凤山乡东纸坊村气象局楼");
            array.add("北京市海淀区西二旗大街铭科苑小区13号楼1305室");
        }
        return  array;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = buildTestInitData();
        String in = array.get(Util.getNumByRange(0,array.size()-1));
        this.random(in,out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf,String in,String out){
        if(Strings.isNullOrEmpty(in) && Strings.isNullOrEmpty(out)){
            return new Object[]{true,null};
        }
        if(Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)){
            return new Object[]{false,"in or out is null"};
        }

        RegionSplit rsIn = new RegionSplit(in);
        RegionSplit rsOut = new RegionSplit(out);

        switch (conf.process) {
            case RANDOM:
            case MASK:
            case UNMASK:
                if(!Strings.isNullOrEmpty(rsIn.proviceName)){
                    if(((Conf.ConfMaskAddress)conf).province){
                        if(!rsIn.proviceName.equals(rsOut.proviceName)){
                            return new Object[]{false,"违反保留省策略"};
                        }
                    }else {
                        if(rsIn.proviceName.equals(rsOut.proviceName)){
                            return new Object[]{false,"违反保留省策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.cityName)){
                    if(((Conf.ConfMaskAddress)conf).city){
                        if(!(in.substring(0,in.indexOf(rsIn.cityName))+rsIn.cityName).equals(out.substring(0,out.indexOf(rsOut.cityName))+rsOut.cityName)){
                            return new Object[]{false,"违反保留市策略"};
                        }
                    }else {
                        if(rsIn.cityName.equals(rsOut.cityName)){
                            return new Object[]{false,"违反保留市策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.countyName)){
                    if(((Conf.ConfMaskAddress)conf).county){
                        if(!(in.substring(0,in.indexOf(rsIn.countyName))+rsIn.countyName).equals(out.substring(0,out.indexOf(rsOut.countyName))+rsOut.countyName)){
                            return new Object[]{false,"违反保留县策略"};
                        }
                    }else {
                        if(rsIn.countyName.equals(rsOut.countyName)){
                            return new Object[]{false,"违反保留县策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.townName)){
                    if(((Conf.ConfMaskAddress)conf).town){
                        /*if(!(in.substring(0,in.indexOf(rsIn.townName))+rsIn.townName).equals(out.substring(0,out.indexOf(rsOut.townName))+rsOut.townName)){
                            return false;
                        }*/
                        if(!rsIn.townName.equals(rsOut.townName)){
                            return new Object[]{false,"违反保留镇策略"};
                        }
                    }else {
                        if(rsIn.townName.equals(rsOut.townName)){
                            return new Object[]{false,"违反保留镇策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.villageName)){
                    if(((Conf.ConfMaskAddress)conf).village){
                        /*if(!(in.substring(0,in.indexOf(rsIn.villageName))+rsIn.villageName).equals(out.substring(0,out.indexOf(rsOut.villageName))+rsOut.villageName)){
                            return false;
                        }*/
                        if(!rsIn.villageName.equals(rsOut.villageName)){
                            return new Object[]{false,"违反保留村策略"};
                        }
                    }else {
                        if(rsIn.villageName.equals(rsOut.villageName)){
                            return new Object[]{false,"违反保留村策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.streetName)){
                    if(((Conf.ConfMaskAddress)conf).street){
                        /*if(!(in.substring(0,in.indexOf(rsIn.streetName))+rsIn.streetName).equals(out.substring(0,out.indexOf(rsOut.streetName))+rsOut.streetName)){
                            return false;
                        }*/
                        if(!rsIn.streetName.equals(rsOut.streetName)){
                            return new Object[]{false,"违反保留街道策略"};
                        }
                    }else {
                        if(rsIn.streetName.equals(rsOut.streetName)){
                            return new Object[]{false,"违反保留街道策略"};
                        }
                    }
                }
                break;
            case COVER:
                if(!Strings.isNullOrEmpty(rsIn.proviceName)){
                    if(((Conf.ConfMaskAddress)conf).coverProvice){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.proviceName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽省策略"};
                        }
                    }else {
                        if(!rsIn.proviceName.equals(rsOut.proviceName)){
                            return new Object[]{false,"违反遮蔽省策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.cityName)){
                    if(((Conf.ConfMaskAddress)conf).coverCity){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.cityName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽市策略"};
                        }
                    }else {
                        if(!rsIn.cityName.equals(rsOut.cityName)){
                            return new Object[]{false,"违反遮蔽市策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.countyName)){
                    if(((Conf.ConfMaskAddress)conf).coverCounty){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.countyName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽县策略"};
                        }
                    }else {
                        if(!rsIn.countyName.equals(rsOut.countyName)){
                            return new Object[]{false,"违反遮蔽县策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.townName)){
                    if(((Conf.ConfMaskAddress)conf).coverTown){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.townName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽镇策略"};
                        }
                    }else {
                        if(!rsIn.townName.equals(rsOut.townName)){
                            return new Object[]{false,"违反遮蔽镇策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.villageName)){
                    if(((Conf.ConfMaskAddress)conf).coverVillage){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.villageName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽村策略"};
                        }
                    }else {
                        if(!rsIn.villageName.equals(rsOut.villageName)){
                            return new Object[]{false,"违反遮蔽村策略"};
                        }
                    }
                }
                if(!Strings.isNullOrEmpty(rsIn.streetName)){
                    if(((Conf.ConfMaskAddress)conf).coverStreet){
                        StringBuilder sb = new StringBuilder();
                        for (int j=0;j<rsIn.streetName.length();j++){
                            sb.append(((Conf.ConfMaskAddress)conf).symbol);
                        }
                        if(!rsOut.equals(sb.toString())){
                            return new Object[]{false,"违反遮蔽街道策略"};
                        }
                    }else {
                        if(!rsIn.streetName.equals(rsOut.streetName)){
                            return new Object[]{false,"违反遮蔽街道策略"};
                        }
                    }
                }
                break;
            default:
                return new Object[]{true,null};
        }
        return new Object[]{true,null};
    }

}
