package top.zigaoliang.algo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Region.TelePhone;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.PhoneUtil;
import top.zigaoliang.util.Util;

import java.util.regex.Pattern;

/**
 * 座机号码算法
 * Created by byc on 10/24/18.
 */
public class AlgoTelephone extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoTelephone.class);
    public AlgoTelephone() {
        super(AlgoId.TELEPHONE);
    }

    static IndexMapList indexMapList  = null;

    static {
        indexMapList = HashMapUtil.convertToIndexMap("/telePhone.txt", TelePhone.class,"areaCode");
    }

    @Override
    public boolean find(String in) {
        if(in.length() < 9 ||in.length() >14){
            return false;
        }
        String[] cellArray = splitTelePhone(in);
        if(cellArray == null){
            String[] cellArrayTemoOne = splitTelePhonePrefix(in,0);
            String[] cellArrayTemoTwo = splitTelePhonePrefix(in,1);
            if(!checkTelePhone(in,cellArrayTemoOne) && !checkTelePhone(in,cellArrayTemoTwo)){
                return false;
            }
        }else{
            if(!checkTelePhone(in,cellArray) || cellArray[2].length() < 5){
                return  false;
            }
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskTelephone confMaskTelephone = (Conf.ConfMaskTelephone)confMask;
        ErrorCode errorCode = null;
        String[] telePhoneArray = getSplitTelePhoneMask(in);
        try{
            if(confMaskTelephone.keep){
                //保留区号
                out.append(telePhoneArray[0]);
            }else{
                out.append(PhoneUtil.getRandomRegionCode(indexMapList.getList()));
            }
            out.append(telePhoneArray[1]);
            if(!confMaskTelephone.keep){
                out.append(telePhoneArray[2].substring(0,4));
                out.append(Util.getRandowNumber(telePhoneArray[2].length()-4));
            }else{
                out.append(Util.getRandowNumber(telePhoneArray[2].length()));
            }
        }catch (Exception e){
            errorCode = ErrorCode.TELLPHONE_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return commonMask(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return commonMask(in,out,false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskTelephone confMaskTelephone = (Conf.ConfMaskTelephone) confMask;
        String[] telePhoneArray = getSplitTelePhoneMask(in);
        switch (confMaskTelephone.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(confMaskTelephone.symbol, telePhoneArray[0].length()))
                            .append(telePhoneArray[1]).append(telePhoneArray[2]);
                break;
            case 2:
                out.append(telePhoneArray[0]).append(telePhoneArray[1])
                            .append(CommonUtil.coverBySymbol(confMaskTelephone.symbol, 4))
                            .append(telePhoneArray[2].substring(4));
                break;
            case 3:
                out.append(telePhoneArray[0]).append(telePhoneArray[1])
                            .append(telePhoneArray[2].substring(0, telePhoneArray[2].length() - 3))
                            .append(CommonUtil.coverBySymbol(confMaskTelephone.symbol, 3));
                break;
            default:
                out.append(in);
        }
        return 0;
    }


    public int commonMask(String in, StringBuilder out, boolean flag){
        ErrorCode errorCode = null;
        try {
            out.append(maskBase(in, flag));
        } catch (Exception e) {
            errorCode = ErrorCode.TELLPHONE_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    public String maskBase(String in, boolean flag){
        Conf.ConfMaskTelephone confMaskTelephone = (Conf.ConfMaskTelephone)confMask;
        String[] telePhoneArray = getSplitTelePhoneMask(in);
        StringBuilder out = new StringBuilder();
        out.append(telePhoneArray[0]).append(telePhoneArray[1]).append(telePhoneArray[2].substring(0,4));
        out.append(AlgoMaskUtil.maskNumberStr(telePhoneArray[2].substring(4),confMaskTelephone.seed,flag));
        return out.toString();
    }

    //脱敏仿真分割座机号
    public static String[]  getSplitTelePhoneMask(String in){
        String[] telePhoneArray = splitTelePhone(in);
        if(telePhoneArray == null){
            telePhoneArray = new String[3];
            if(hasAreaCode(in.substring(0,3))){
                telePhoneArray[0] = in.substring(0,3);
                telePhoneArray[1] = "";
                telePhoneArray[2] = in.substring(3);
            }else if(hasAreaCode(in.substring(0,4))){
                telePhoneArray[0] = in.substring(0,4);
                telePhoneArray[1] = "";
                telePhoneArray[2] = in.substring(4);
            }
        }
        return telePhoneArray;
    }


    /**
     * 验证存不存在该区号
     * @return
     */
    public static boolean hasAreaCode(String code){
        return HashMapUtil.containsKey(indexMapList.getMap(),code);
    }

    /**
     * @param in    源数据
     * @return
     */
    public static String[] splitTelePhone(String in){
        String[] result = new String[3];
        result[1] = getSplitChar(in);
        if(result[1] != null){
            if(in.split(result[1]).length != 2){
                return null;
            }else {
                result[0] = in.split(result[1])[0];
                result[2] = in.split(result[1])[1];
                return result;
            }
        }else {
            //如果座机号区域和号码之间没有分隔符
            return null;
        }
    }

    /**
     *
     * @param in
     * @param state  状态 ==0 取前三位为区域号  ==1取前四位为区域号
     * @return
     */
    public String[] splitTelePhonePrefix(String in,int state){
        String[] result = new String[3];
        if(state == 0){
            result[0] = in.substring(0,3);
            result[1] = "";
            result[2] = in.substring(3);
            return result;
        }else if(state == 1){
            result[0] = in.substring(0,4);
            result[1] = "";
            result[2] = in.substring(4);
            return result;
        }else{
            return null;
        }
    }


    //获得座机号的分隔符
    public static String getSplitChar(String in){
        String[] special = {" ","-","_"};
        for(int i = 0; i < special.length; i++){
            if(in.contains(special[i])){
                return special[i];
            }
        }
        return null;
    }

    //验证座机号区域号和号码是否正确
    public boolean checkTelePhone(String in, String[] cellArray){
        ErrorCode errorCode;
        if(StringUtils.isBlank(cellArray[0])){
            return false;
        }else{
            if(CommonUtil.outOfIntRange(cellArray[0])){
                return false;
            }
            //验证存不存在该区号
            if(!hasAreaCode(cellArray[0])){
                errorCode = ErrorCode.TELLPHONE_AREACODE_ERROR;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        }
        //座机号码正则
        String regExPhoneCode = "^[0-9]{5,10}$";   //验证带区号的
        if (!Pattern.compile(regExPhoneCode).matcher(cellArray[2]).matches()) {
            errorCode = ErrorCode.TELLPHONE_INPUT;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    @Override
    public int random(StringBuilder out) {
        String in = "010-1001056";

        Conf.ConfMaskTelephone conf = new Conf.ConfMaskTelephone();

        conf.keep = Util.getNumByRange(0, 1) == 0;
        conf.coverType = Util.getNumByRange(1, 3);
        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (in == null || out == null) {
            return new Object[]{false, "in or out data is invalid celephone"};
        }
        String[] telePhoneArrayIn = getSplitTelePhoneMask(in);
        String[] telePhoneArrayOut = getSplitTelePhoneMask(out);
        StringBuffer coverPart = new StringBuffer();
        for (int i=0; i<telePhoneArrayIn[0].length();++i){
            coverPart.append("*");
        }


        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (((Conf.ConfMaskTelephone) conf).keep) {
                    if (!telePhoneArrayIn[0].equals(telePhoneArrayOut[0])){
                        return new Object[]{false, "违反保留区号策略"};
                    }
                }

                if (!((Conf.ConfMaskTelephone) conf).keep) {
                    if (!telePhoneArrayIn[2].equals(telePhoneArrayOut[1])){
                        return new Object[]{false, "违反保留前四位策略"};
                    }
                }
                break;
            case COVER:
                switch (((Conf.ConfMaskTelephone) conf).coverType) {
                    case 1:

                        if (!coverPart.toString().equals(telePhoneArrayOut[0]))
                            return new Object[]{false, "违反遮蔽区号策略"};
                        break;

                    case 2:
                        if (!"****".equals(telePhoneArrayOut[2].substring(0,4)))
                            return new Object[]{false, "违反遮蔽中间四位策略"};
                        break;
                    case 3:
                        if (!"***".equals(telePhoneArrayOut[2].substring(4)))
                            return new Object[]{false, "违反遮蔽最后三位策略"};
                        break;
                }
                break;
        }
        return new Object[]{true, null};
    }
}
