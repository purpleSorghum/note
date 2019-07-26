package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import java.util.regex.Pattern;

/**
 * IP地址算法
 *
 * @author byc
 * @date 10/24/18
 */
public class AlgoIpAddress extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoIpAddress.class);

    public AlgoIpAddress() {
        super(AlgoId.IPADDRESS);
    }

    private Conf.ConfMaskIpAddress confMaskIpAddress = new Conf.ConfMaskIpAddress();
    @Override
    public int init(Conf.ConfMask confMask) {
        if (confMask == null) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        if(confMask instanceof Conf.ConfMaskIpAddress){
            confMaskIpAddress = (Conf.ConfMaskIpAddress)confMask;
        }
        return 0;
    }


    @Override
    public boolean find(String in) {
        if(in.length() < 7 || in.length() > 15){
            return false;
        }
        //ipv4正则
        String regEx = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
        Pattern pattern = Pattern.compile(regEx);
        if (!pattern.matcher(in).matches()) {
            //ip格式不正确
            ErrorCode errorCode = ErrorCode.IP_ADDRESS_INPUT;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        ErrorCode errorCode = null;
        try {
            String[] array = in.split("\\.");
            //随机生成一个小于1000的整数
            if (confMaskIpAddress.prefix) {
                //保留前三位
                out.append(array[0]).append(".");
            } else {
                out.append(getInteger()).append(".");
            }
            out.append(getInteger()).append(".").append(getInteger()).append(".");
            if (confMaskIpAddress.suffix) {
                //保留后三位
                out.append(array[3]);
            } else {
                out.append(getInteger());
            }
        } catch (Exception e) {
            errorCode = ErrorCode.IP_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        String[] array = in.split("\\.");
        switch (confMaskIpAddress.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[0].length())).append(".")
                        .append(array[1]).append(".").append(array[2]).append(".").append(array[3]);
                break;
            case 2:
                out.append(array[0]).append(".")
                        .append(array[1]).append(".")
                        .append(array[2]).append(".")
                        .append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[3].length()));
                break;
            case 3:
                out.append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[0].length())).append(".")
                        .append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[1].length())).append(".")
                        .append(array[2]).append(".").append(array[3]);
                break;
            case 4:
                out.append(array[0]).append(".").append(array[1]).append(".")
                        .append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[2].length())).append(".")
                        .append(CommonUtil.coverBySymbol(confMaskIpAddress.symbol, array[3].length()));
            default:
                out.append(in);
        }
        return 0;
    }

    /**
     * 脱敏公共方法    flag true 正向脱敏  false 负向脱敏
     * @param in
     * @param out
     * @return
     */
    public int maskBase(String in, StringBuilder out, Boolean flag){
        ErrorCode errorCode = null;
        String[] array = in.split("\\.");
        int[] intRanger = {0, 255};
        try {
            if (confMaskIpAddress.prefix) {
                out.append(array[0]).append(".");
            } else {
                out.append(Util.maskBaseForInteger(intRanger, Integer.parseInt(array[0]), confMaskIpAddress.seed, flag)).append(".");
            }
            out.append(Util.maskBaseForInteger(intRanger, Integer.parseInt(array[1]), confMaskIpAddress.seed, flag)).append(".")
               .append(Util.maskBaseForInteger(intRanger, Integer.parseInt(array[2]), confMaskIpAddress.seed, flag)).append(".");
            if (confMaskIpAddress.suffix) {
                out.append(array[3]);
            } else {
                out.append(Util.maskBaseForInteger(intRanger, Integer.parseInt(array[3]), confMaskIpAddress.seed, flag));
            }
        } catch (Exception e) {
            errorCode = ErrorCode.IP_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    public static String getInteger(){
        return String.valueOf(Util.getNumByRange(0, 255));
    }
}
