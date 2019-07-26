package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

/**
 * 手机号码算法
 * Created by byc on 10/24/18.
 */
public class AlgoCellphone extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoCellphone.class);
    public AlgoCellphone() {
        super(AlgoId.CELLPHONE);
    }

    static IndexMapList indexMapList = null;
    static {
        indexMapList = HashMapUtil.convertToIndexMap("/cellPhoneCode.txt");
    }

    @Override
    public boolean find(String in) {
        try {
            //预处理之后在判断手机号的长度
            if(in.length() > 17 || in.length() < 11){
                return  false;
            }
            in = cellPhonePreProcess(in,null);
            if(!indexMapList.getMap().containsKey(in.substring(0,3))){
                return false;
            }
            if(!CommonUtil.isDigit(in)){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskCellphone confMaskCellphone = (Conf.ConfMaskCellphone)confMask;
        ErrorCode errorCode = null;
        in = cellPhonePreProcess(in,out);
        try{
            if(confMaskCellphone.keep == 1){
              //保留前三位
                out.append(in.substring(0,3)).append(Util.getRandowNumber(8));
            }else if(confMaskCellphone.keep == 2){
                // 保留中间四位
                out.append(getRandomCellPhoneSuffix()).append(in.substring(3,7))
                        .append(Util.getRandowNumber(4));
            }else if(confMaskCellphone.keep == 3){
                // 保留后四位
                out.append(getRandomCellPhoneSuffix()) .append(Util.getRandowNumber(4)).append(in.substring(7));
            }else{
                errorCode = ErrorCode.CELLPHONE_PARAM_ERROR;
                return errorCode.getCode();
            }
        }catch (Exception e){
            errorCode = ErrorCode.CELLPHONE_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskCellphone confMaskCellphone = (Conf.ConfMaskCellphone)confMask;
        ErrorCode errorCode = null;
        in = cellPhonePreProcess(in,out);
        try{
            out.append(in.substring(0,3))
                    .append(maskBase(in.substring(3), confMaskCellphone.seed, true));
        }catch (Exception e){
            errorCode = ErrorCode.CELLPHONE_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskCellphone confMaskCellphone = (Conf.ConfMaskCellphone)confMask;
        ErrorCode errorCode = null;
        in = cellPhonePreProcess(in,out);
        try{
            out.append(in.substring(0,3))
                    .append(maskBase(in.substring(3), confMaskCellphone.seed, false));
        }catch (Exception e){
            errorCode = ErrorCode.CELLPHONE_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskCellphone confMaskCellphone = (Conf.ConfMaskCellphone) confMask;
        in = cellPhonePreProcess(in, out);
        switch (confMaskCellphone.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(confMaskCellphone.symbol, 3))
                        .append(in.substring(3));
                break;
            case 2:
                out.append(in.substring(0,3))
                        .append(CommonUtil.coverBySymbol(confMaskCellphone.symbol, 4))
                        .append(in.substring(7));
                break;
            case 3:
                out.append(in.substring(0, 8)).append(CommonUtil.coverBySymbol(confMaskCellphone.symbol, 4));
                break;
            default:
                out.append(in);
        }
        return 0;
    }


    public String maskBase(String in, int seed, boolean flag){
        StringBuilder out  =  new StringBuilder();
        char[]  charArray = in.toCharArray();
        for(int i = 0;i < charArray.length; i++){
            out.append(Util.maskBaseForInteger(Util.numberArray,charArray[i]-48,seed,flag));
        }
        return out.toString();
    }

    /**
     * 随机获得一个电话号码段
     * @return
     */
    public String getRandomCellPhoneSuffix(){
        int index = Util.getNumByRange(0,indexMapList.getList().size()-1);
        return indexMapList.getList().get(index);
    }

    /**
     * 电话号码预处理
     * @param in
     * @param out
     * @return
     */
    public String cellPhonePreProcess(String in, StringBuilder out){
        boolean flag = in.contains(" ")?true:false;
        String cellPhoneStart = "";
        if(in.contains("+86")){
            cellPhoneStart = "+86";
        }
        if(in.contains("+0086") && in.indexOf("+0086") == 0){
            cellPhoneStart = "+0086";
        }
        if(cellPhoneStart.equals("+86")){
            if(out != null){
                out.append("+86").append(flag == true?" ":"");
            }
            in = in.substring(3).trim();
        }
        if(cellPhoneStart.equals("+0086")){
            if(out != null){
                out.append("+0086").append(flag == true?" ":"");
            }
            in = in.substring(5).trim();
        }
        return in;
    }
}
