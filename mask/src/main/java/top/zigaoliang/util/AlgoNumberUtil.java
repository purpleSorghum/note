package top.zigaoliang.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;

import java.util.regex.Pattern;

/**
 * 数字脱敏的公共算法
 * Created by yehuan on 12/25/18.
 */
public class AlgoNumberUtil {
    private static Logger log = Logger.getLogger(AlgoMaskUtil.class);

    // 匹配整数
    public static boolean findInteger(String in) {
        if (in.equals("+0") || in.equals("-0")) {
            return false;
        }
        //整数正则
        String regEx = "^[+-]?(\\d*)$";
        Pattern pattern = Pattern.compile(regEx);
        if (!pattern.matcher(in).matches()) {
            return false;
        }
        if (CommonUtil.outOfLong(in)) {
            return false;
        }
        in = in.replaceAll("[+-]", "");
        if (in.length() > 1 && in.substring(0, 1).equals("0")) {
            return false;
        }
        return true;
    }

    //验证是否是小数
    public static boolean findDecimal(String in) {
        //金额数字的验证规则 带小数点
        String regEx = "^[+-]?(([1-9]{1}\\d*)|([0]{1}))\\.(\\d{1,6})$";
        ErrorCode errorCode = null;
        if (!Pattern.compile(regEx).matcher(in).matches()) {
            //数字格式不正确
            return false;
        }
        if (CommonUtil.outOfDoubleRange(in)) {
            errorCode = ErrorCode.MONEY_SHIFT_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    /**
     * @param in
     * @return
     */
    public static int maskInteger(String in, StringBuilder out, Conf.ConfMaskInteger confMaskInteger, boolean flag){
        if(Long.parseLong(in) > confMaskInteger.max){
            out.append(in);
        }else if(Long.parseLong(in) < confMaskInteger.min){
            out.append(in);
        }else {
            Long[] longRange = {confMaskInteger.min,confMaskInteger.max};
            out.append(Util.maskBaseForLong(longRange,Long.parseLong(in),confMaskInteger.seed,flag));
        }
        return 0;
    }

    public static int integerByRandom(String in, StringBuilder out, Conf.ConfMaskInteger confMaskInteger) {
        /*
         * 下面的代码不要轻易动，一步一坑踩出来的
         */
        if(StringUtils.isBlank(in)){
            out.append(in);
            return 0;
        }
        /**
         *  梳理那里min  max会根据字段类型进行校验，
         * 传到的数据一定实在min 和 max范围之类的
         * 但是对于文件到库，梳理那里没法指定min,max
         * 的大小，所以默认的0 - 99，所以针对文件到库
         * 不再这个范围内的就不脱敏
         */
        if(Long.parseLong(in) > confMaskInteger.max || Long.parseLong(in) < confMaskInteger.min){
           out.append(in);
           return 0;
        }
        if (confMaskInteger.isKeepLength()) {
            StringBuilder start = new StringBuilder();
            StringBuilder end = new StringBuilder();
            int length = in.length();
            String symbol = "";
            if (in.contains("-")) {
                length -= 1;
                symbol = "-";
            }
            start.append(symbol).append("1");
            for (int i = 0; i < length - 1; i++) {
                start.append("0");
            }
            end.append(symbol);
            for (int i = 0; i < length; i++) {
                end.append("9");
            }

            //如果是负数，那么最大值，最小值替换
            if(symbol.equals("-")){
                StringBuilder temp = start;
                start = end;
                end = temp;
            }

            if (Long.parseLong(start.toString()) < confMaskInteger.min) {
                start = new StringBuilder(String.valueOf(confMaskInteger.min));
            }
            if (CommonUtil.outOfLong(end.toString()) || Long.parseLong(end.toString()) > confMaskInteger.max) {
                end = new StringBuilder(String.valueOf(confMaskInteger.max));
            }
            //如果起始值大于最大值，选用页面设置的最小值
            if(Long.parseLong(start.toString()) >= Long.parseLong(end.toString())){
                start = new StringBuilder(String.valueOf(confMaskInteger.min));
            }
            out.append(Util.getNumByRange(Long.parseLong(start.toString())
                        , Long.parseLong(end.toString())));
        } else {
            //说明默认的参数被客户修改过，就使用客户修改的范围
            out.append(Util.getNumByRange(confMaskInteger.min, confMaskInteger.max));
        }
        return 0;
    }
}
