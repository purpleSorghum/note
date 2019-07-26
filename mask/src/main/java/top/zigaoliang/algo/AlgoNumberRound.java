package top.zigaoliang.algo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoNumberUtil;


public class AlgoNumberRound extends AlgoBase{
    private static Logger log = Logger.getLogger(AlgoNumberRound.class.getSimpleName());

    public AlgoNumberRound() {
        super(AlgoId.NUMBERROUND);
    }

    @Override
    public boolean find(String in) {
        if(!AlgoNumberUtil.findInteger(in)){
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskNumberRound confNumberRound = (Conf.ConfMaskNumberRound)confMask;
        ErrorCode errorCode = null;
        try{
            if(confNumberRound.type == 1){
                //直接舍弃
                out.append(giveUp(in));
            }
            if(confNumberRound.type == 2){
                //四舍五入
                out.append(rounding(in));
            }
            if(confNumberRound.type == 3){
                //向上取整
                out.append(ceil(in));
            }
        }catch (Exception e){
            errorCode = ErrorCode.EMAIL_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out)
    {
        out.append(in);
        return 0;
    }

    //直接舍弃
    public String giveUp(String in){
        if(!AlgoNumberUtil.findInteger(in)){
            return in;
        }
        StringBuilder result = new StringBuilder();
        String symbol = "";
        if(in.contains("-")){
            symbol = "-";
        }else if(in.contains("+")){
            symbol = "+";
        }
        if(StringUtils.isNotBlank(symbol)){
            in = in.substring(1);
        }
        if(in.length() ==1){
            result.append("0");
            return result.toString();
        }
        result.append(symbol);
        result.append(in.substring(0,1));
        for (int i = 0; i < in.length() -1; i++) {
            result.append("0");
        }
        return result.toString();
    }

    //四舍五入
    public int rounding(String in){
        StringBuilder result = new StringBuilder();
        if(Integer.parseInt(in) > 0){
            //正数
            String mainNum = giveUp(in);
            //获取进制
            String binary = getBinarySystem(in);
            if(Integer.parseInt(in.substring(1)) > Integer.parseInt(binary)/2){
               return Integer.parseInt(mainNum) + Integer.parseInt(binary);
            }else{
               return  Integer.parseInt(mainNum);
            }
        }else{
            //负数
            String mainNum = giveUp(in);
            String binary = getBinarySystem(in);
            if(Integer.parseInt(in.substring(1)) > Integer.parseInt(binary)/2){
                return  Integer.parseInt(mainNum) - Integer.parseInt(binary);
            }else{
                return  Integer.parseInt(mainNum);
            }
        }
    }

    //向上取整
    public int ceil(String in){
        StringBuilder result = new StringBuilder();
        if(Integer.parseInt(in) == 0){
            return 0;
        }
        if(Integer.parseInt(in) > 0){
            //正数
            if(Integer.parseInt(in) < 10){
                return 10;
            }
            String mainNum = giveUp(in);
            if(Integer.parseInt(in.substring(1)) == 0){
                return Integer.parseInt(mainNum);
            }else{
                return Integer.parseInt(mainNum) - Integer.parseInt(getBinarySystem(in));
            }
        }else{
            //负数
            int newIn = Integer.parseInt(in.substring(1));
            if(newIn < 10){
                return -10;
            }
            String mainNum = giveUp(in);
            if(Integer.parseInt(Integer.toString(newIn).substring(1)) == 0){
                return Integer.parseInt(mainNum);
            }else{
                return Integer.parseInt(mainNum) + Integer.parseInt(getBinarySystem(in));
            }

        }
    }

    //获取进制
    public String getBinarySystem(String in) {
        StringBuilder result = new StringBuilder();
        String newIn = Integer.parseInt(in) > 0 ? in : in.substring(1);
        if (Integer.parseInt(in) < 10) {
            result.append("10");
        } else {
            result.append("1").append(in.length() - 1);
        }
        return newIn;
    }

}







