package top.zigaoliang.algo;


import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import java.util.List;

/**
 * 整数区间算法
 * Created by byc on 10/24/18.
 */
public class AlgoIntegerRange extends AlgoBase {
    public AlgoIntegerRange() {
        super(AlgoId.INTEGERRANGE);
    }
    private List<Integer> listColData = null;//保存一列数据
    private int index = 0;
    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskIntegerRange)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        confMask.process = Conf.MaskType.MASK;
        return super.init(confMask);
    }
    public void setListColData(List<Integer> listColData) {
        this.listColData = listColData;
    }
    @Override
    public boolean find(String in) {
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        out.append(listColData.get(index++));
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
         //库脱敏的时候调用
        random(in, out);
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        random(in, out);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    //生成一列数据
    public int randomSingle(String in, StringBuilder out) {
        Conf.ConfMaskIntegerRange confMaskIntegerRange = (Conf.ConfMaskIntegerRange) confMask;
        //脱敏单独测试的时候调用
        //判断第10个数是否大于设置的最大值
        int indexTen = confMaskIntegerRange.begin + (10 - 1) * confMaskIntegerRange.step;
        int number = confMaskIntegerRange.begin;
        StringBuilder result = new StringBuilder();
        if (indexTen >= confMaskIntegerRange.max) {
            //展示所有的数据(10个)
            for (int i = 0; i < 10; i++) {
                number = number + confMaskIntegerRange.step;
                if(number <= confMaskIntegerRange.max){
                    result.append(number).append(",");
                }else{
                    break;
                }
            }
            //去掉最后的逗号
            out.append(result.substring(0,result.length()-1));
            return 0;
        } else {
            //展示前面8个+ "..." + 最后两个
            for (int i = 0; i < 8; i++) {
                number = number + confMaskIntegerRange.step;
                out.append(number);
                if (i != 7) {
                    out.append(",");
                }
            }
            out.append("...");
//                    .append(confMaskIntegerRange.begin + (10 - 1) * confMaskIntegerRange.step).append(", ")
//                    .append(confMaskIntegerRange.begin + (11 - 1) * confMaskIntegerRange.step);
            return 0;
        }
    }



}
