package top.zigaoliang.algo;

import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Region.AdressCode;
import top.zigaoliang.contant.RegionContants;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.IdCardMaskUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 身份证算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoIdCard extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoIdCard.class);
    public AlgoIdCard() {
        super(AlgoId.IDCARD);
    }
    static {
        //对地区编号进行排序
        sortRegionCode();
        initRegionCodeOnList();
    }
    @Override
    public boolean find(String in) {
        return IdCardMaskUtil.validateIdCard(in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskIdCard confMaskIdCard = (Conf.ConfMaskIdCard)confMask;
        return IdCardMaskUtil.getRandomIdCard(confMaskIdCard,in,out);
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskIdCard confMaskIdCard = (Conf.ConfMaskIdCard)confMask;
        ErrorCode errorCode = null;
        try{
            IdCardMaskUtil.maskBase(in, confMaskIdCard, true, out);
        }catch (Exception e){
            errorCode = ErrorCode.ID_MASK_UNKNOWN;
            log.debug(e.getMessage() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskIdCard confMaskIdCard = (Conf.ConfMaskIdCard)confMask;
        ErrorCode errorCode = null;
        try{
            IdCardMaskUtil.maskBase(in, confMaskIdCard, false, out);
        }catch (Exception e){
            errorCode = ErrorCode.ID_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }
    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskIdCard confMaskIdCard = (Conf.ConfMaskIdCard) confMask;
        List<String> idcoardDom = splitIdCard(in);
        out.append(coverCommon(idcoardDom.get(0), confMaskIdCard.coverProvice, confMaskIdCard.symbol))
                    .append(coverCommon(idcoardDom.get(1), confMaskIdCard.coverCity, confMaskIdCard.symbol))
                    .append(coverCommon(idcoardDom.get(2), confMaskIdCard.coverCounty, confMaskIdCard.symbol))
                    .append(coverCommon(idcoardDom.get(3).concat(idcoardDom.get(4)), confMaskIdCard.coverYearAndMonth, confMaskIdCard.symbol))
                    .append(idcoardDom.get(5)).append(idcoardDom.get(6));
        //15位身份证 没有校验位
        out.append(in.length() == 18 ? coverCommon(idcoardDom.get(7), confMaskIdCard.coverCheck, confMaskIdCard.symbol) : "");
        return 0;
    }
    public String coverCommon(String in,boolean flag, String symbol){
        if(!flag){
            return in;
        }
        return CommonUtil.coverBySymbol(symbol,in.length());
    }

    private static void initRegionCodeOnList() {
        if(AdressCode.proviceCodeList.size() == 0){
            AdressCode.initRegionCode();
        }
    }

    public static void sortRegionCode(){
        Arrays.sort(RegionContants.regionCodeArray);
    }

    /**
     * 分割身份证，存入集合中，依此为：省份，市，县，年，月，日，3位顺序号，校验位
     * @param in
     * @return
     */
    public List<String> splitIdCard(String in) {
        List<String> idcardDom = new ArrayList<>();
        if (in.length() == 18) {
            idcardDom.add(in.substring(0, 2));   //省份
            idcardDom.add(in.substring(2, 4));   //市
            idcardDom.add(in.substring(4, 6));     //县
            idcardDom.add(in.substring(6, 10));    //年
            idcardDom.add(in.substring(10, 12));   //月
            idcardDom.add(in.substring(12, 14));   //日
            idcardDom.add(in.substring(14, 17));   //3位顺序号
            idcardDom.add(in.substring(17));           //校验位
        } else if (in.length() == 15) {
            idcardDom.add(in.substring(0, 2));   //省份
            idcardDom.add(in.substring(2, 4));   //市
            idcardDom.add(in.substring(4, 6));     //县
            idcardDom.add(in.substring(6, 8));    //年
            idcardDom.add(in.substring(8, 10));   //月
            idcardDom.add(in.substring(10, 12));   //日
            idcardDom.add(in.substring(12));   //3位顺序号
        }
        return idcardDom;
    }

    @Override
    public int random(StringBuilder out) {
        String in = "511025199310083286";

        Conf.ConfMaskIdCard conf = new Conf.ConfMaskIdCard();

        conf.province = Util.getNumByRange(0, 1) == 0;
        conf.city = Util.getNumByRange(0, 1) == 0;
        conf.county = Util.getNumByRange(0, 1) == 0;
        conf.birthday = Util.getNumByRange(0, 1) == 0;
        conf.sex = Util.getNumByRange(0, 1) == 0;
        conf.check = Util.getNumByRange(0, 1) == 0;

        conf.coverProvice = Util.getNumByRange(0, 1) == 0;
        conf.coverCity = Util.getNumByRange(0, 1) == 0;
        conf.coverCounty = Util.getNumByRange(0, 1) == 0;
        conf.coverYearAndMonth = Util.getNumByRange(0, 1) == 0;
        conf.coverCheck = Util.getNumByRange(0, 1) == 0;

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (in == null || out == null) {
            return new Object[]{false, "in or out data is invalid idCard"};
        }

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (((Conf.ConfMaskIdCard)conf).province){
                    if (!(in.substring(0,2).equals(out.substring(0,2)) && (!in.substring(2).equals(out.substring(2))))){
                        return new Object[]{false, "违反保留省策略"};
                    }
                }

                if (((Conf.ConfMaskIdCard) conf).city){
                    if (!in.substring(2,4).equals(out.substring(2,4))){
                        return new Object[]{false, "违反保留市策略"};
                    }
                }

                if (((Conf.ConfMaskIdCard) conf).county){
                    if (!in.substring(4,6).equals(out.substring(4,6))){
                        return new Object[]{false, "违反保留区县策略"};
                    }
                }

                if (((Conf.ConfMaskIdCard) conf).birthday){
                    if (in.length() == 18){
                        if (!in.substring(6,14).equals(out.substring(6,14))){
                            return new Object[]{false, "违反保留生日策略"};
                        }
                    }

                    if (in.length() == 15){
                        if (!in.substring(6,12).equals(out.substring(6,12))){
                            return new Object[]{false, "违反保留生日策略"};
                        }
                    }
                }

                if (((Conf.ConfMaskIdCard) conf).sex){
                    if (in.length() == 18){
                        if (!in.substring(16,17).equals(out.substring(16,17))){
                            return new Object[]{false, "违反保留性别策略"};
                        }
                    }
                    if (in.length() == 15){
                        if (!in.substring(14).equals(out.substring(14))){
                            return new Object[]{false, "违反保留性别策略"};
                        }
                    }

                }

                break;
            case COVER:
                if (((Conf.ConfMaskIdCard) conf).coverProvice){
                    if (!out.substring(0,2).equals("**")){
                        return new Object[]{false, "违反遮蔽省策略"};
                    }
                }
                if (((Conf.ConfMaskIdCard) conf).coverCity){
                    if (!out.substring(2,4).equals("**")){
                        return new Object[]{false, "违反遮蔽市策略"};
                    }
                }
                if (((Conf.ConfMaskIdCard) conf).coverCounty){
                    if (!out.substring(4,6).equals("**")){
                        return new Object[]{false, "违反遮蔽区县策略"};
                    }
                }
                if (((Conf.ConfMaskIdCard) conf).coverYearAndMonth){
                    if (in.length() == 18){
                        if (!out.substring(6,12).equals("******")){
                            return new Object[]{false, "违反遮蔽年月策略"};
                        }
                    }

                    if (in.length() == 15){
                        if (!out.substring(6,10).equals("****")){
                            return new Object[]{false, "违反遮蔽年月策略"};
                        }
                    }
                }
                if (((Conf.ConfMaskIdCard) conf).check) {
                    if (in.length() == 18) {
                        if (!out.substring(17).equals("*")) {
                            return new Object[]{false, "违反遮蔽校验位策略"};
                        }
                    }

                }
                break;
        }
        return new Object[]{true, null};
    }

}
