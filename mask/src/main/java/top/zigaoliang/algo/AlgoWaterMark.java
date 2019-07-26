package top.zigaoliang.algo;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgoWaterMark extends AlgoBase{
    private static Logger log = Logger.getLogger(AlgoDate.class);
    public AlgoWaterMark() {
        super(AlgoId.WATERMARK);
    }
    private AlgoInteger  algoInteger = new AlgoInteger();
    private AlgoMoney algoMoney = new AlgoMoney();
    private int seedInit = 100;
    @Override
    public boolean find(String in) {
//        seedInit ++;
//        if (Strings.isNullOrEmpty(in)) {
//            return false;
//        }
//        boolean isName = algoMoney.find(in);
//        return isName ? isName : algoInteger.find(in);
        return  false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        return baseMask(in,out,true);
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return baseMask(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return baseMask(in,out,false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    public int baseMask(String in, StringBuilder out, boolean flag){

//        Conf.ConfMaskWaterMark confMaskWaterMark = (Conf.ConfMaskWaterMark) confMask;
//        if(algoInteger.find(in)){
//            //按照整数来脱敏
//            return maskInteger(in,out,confMaskWaterMark,flag);
//        }
//        if(algoMoney.find(in)){
//            return maskFloat(in, out, confMaskWaterMark, flag);
//        }
        return 0;
    }

    public int maskInteger(String in, StringBuilder out, Conf.ConfMaskWaterMark confMaskWaterMark, boolean flag) {
        in = in.trim();
        ErrorCode errorCode = null;
        try {
            //横向种子变化步长
            int rowsStepSize = CommonUtil.getRanomBySeed(confMaskWaterMark.seed + seedInit, 1, 99);
            //纵向种子变化步长
            int colsStepSize = CommonUtil.getRanomBySeed(confMaskWaterMark.seed + seedInit + 100, 1, 99);
            //种子纵向起始种子值
            int seed = confMaskWaterMark.seed + colsStepSize + seedInit;
            out.append(in);
            /**
             * 逻辑：1.如果万分位脱敏，那么十，白，千都要修改
             *      2.如果千分位脱敏， 那么百，十，都要修改  以此类推
             */
            StringBuilder tempResult = new StringBuilder();
            if(StringUtils.isBlank(confMaskWaterMark.columns) ||
                    StringUtils.isBlank(confMaskWaterMark.columns.substring(1,confMaskWaterMark.columns.length()-1))){
                return 0;
            }
            String columns = confMaskWaterMark.columns.substring(1,confMaskWaterMark.columns.length()-1);
            columns = columns.replace("\"","");
            String[] precisionArray = columns.split(",");
            out.append(precisionArray.length == 0?"":".");
            int maxPrecision = CommonUtil.getMaxFromArray(precisionArray);
            for (int i = maxPrecision; i >= 1; i--) {
                if (CommonUtil.arrayContain(precisionArray, Integer.toString(i))) {
                    //防止种子数太大超过int的取值范围
                    seed = seed > Integer.MAX_VALUE - 100?100:seed;
                    seed = maskPart(seed, rowsStepSize, tempResult);
                } else {
                    tempResult.append(0);
                }
            }
            //小数部分反转
            out.append(new StringBuilder(tempResult.toString()).reverse());
        } catch (Exception e) {
            errorCode = ErrorCode.WATERMARK_MASK_INT_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    public int maskPart(int seed, int rowsStepSize, StringBuilder out){
        seed += rowsStepSize;
        out.append(CommonUtil.getRanomBySeed(seed));
        return seed;
    }

    public int maskFloat(String in, StringBuilder out, Conf.ConfMaskWaterMark confMaskWaterMark, boolean flag) {
        in = in.trim();
        ErrorCode errorCode = null;
        try {
            //横向种子变化步长
            int rowsStepSize = CommonUtil.getRanomBySeed(seedInit, 1, 99);
            //纵向种子变化步长
            int colsStepSize = CommonUtil.getRanomBySeed(seedInit + 100, 1, 99);
            //种子纵向起始种子值
            int seed = seedInit + colsStepSize;
            StringBuilder tempResult = new StringBuilder();
//            String[] precisionArray = confMaskWaterMark.columns.split(",");
            if(StringUtils.isBlank(confMaskWaterMark.columns) ||
                    StringUtils.isBlank(confMaskWaterMark.columns.substring(1,confMaskWaterMark.columns.length()-1))){
                out.append(in);
                return 0;
            }
            out.append(in.split("\\.")[0]).append(".");
            String columns = confMaskWaterMark.columns.substring(1,confMaskWaterMark.columns.length()-1);
            columns = columns.replace("\"","");
            String[] precisionArray = columns.split(",");
            String[] decimalArray = in.split("\\.")[1].split("");
            List<String> decimalList = new ArrayList<>(Arrays.asList(decimalArray));
            //找到要保留的精度和源数据的精度 的最大的那个
            int maxPrecision = CommonUtil.getMaxFromArray(precisionArray) > decimalArray.length ?
                    CommonUtil.getMaxFromArray(precisionArray) : decimalArray.length;
            centreFloat(decimalList, maxPrecision);
            for (int i = maxPrecision; i >= 1; i--) {
                if (CommonUtil.arrayContain(precisionArray, Integer.toString(i))) {
                    //防止种子数太大超过int的取值范围
                    seed = seed > Integer.MAX_VALUE - 100 ? 100 : seed;
                    seed = maskPart(seed, rowsStepSize, tempResult);
                } else {
                    //该位没有指定，该位原来有值返回原来的值，，，如果没有0补位
                    tempResult.append(decimalList.get(i-1));
                }
            }
            //小数部分反转
            out.append(new StringBuilder(tempResult.toString()).reverse());
        } catch (Exception e) {
            errorCode = ErrorCode.WATERMARK_MASK_FLOAT_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    //指定脱敏到哪一位就补位到哪一位
    public void centreFloat(List<String> srcList,int max){
        if(max > srcList.size()){
            int flenth = max - srcList.size();
            for(int i = 0; i < flenth; i++){
                srcList.add("0");
            }
        }
    }


}
