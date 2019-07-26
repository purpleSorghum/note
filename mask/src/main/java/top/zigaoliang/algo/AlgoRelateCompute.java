package top.zigaoliang.algo;


import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import java.util.LinkedList;
import java.util.List;

/**
 * 关联算法--计算关联算法
 * C=A+B
 * Created by byc on 10/24/18.
 */
public class AlgoRelateCompute extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoRelateCompute.class);

    public AlgoRelateCompute() {
        super(AlgoId.RELATECOMPUTE);
        attr = 2;
    }

    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        this.mask(in, out);
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskRelateCompute confMaskRelateCompute = (Conf.ConfMaskRelateCompute)confMask;
        List<Long> split = new LinkedList<>();
        // 1. 输入值验证
        // 2. 切分输入值
        if (!splitInput(in, split)) {
            out.append("数据格式不符合");
            ErrorCode errorCode = ErrorCode.RELATE_COMPUTE_INPUT;
            String errorMsg = errorCode.getMsg() + "; 输入数据：" + in;
            log.debug(errorMsg);
            return errorCode.getCode();
        }
        // 3. 计算输入值
        String ch = ((Conf.ConfMaskRelateCompute) confMask).symbol;
        Double result = 0d;
        if (split.size() == 2) {
            result = Double.valueOf(this.compute(Double.valueOf(split.get(0)), Double.valueOf(split.get(1)), ch));
        } else {
            for (int i = 2; i < split.size(); i++) {
                result = Double.valueOf(this.compute(Double.valueOf(split.get(0)), Double.valueOf(split.get(1)), ch));
                result = this.compute(result, Double.valueOf(split.get(i)), ch);
            }
        }

        //控制数据计算后的范围
        /**
         * 如果计算的结果违反梳理那里该字段类型的范围
         * 进行特殊处理 比如：计算的结果超出该列的最
         * 大存储值那么就返回该列的最大值
         */
        if (result > Long.parseLong(confMaskRelateCompute.max)) {
            out.append(confMaskRelateCompute.max);
        } else if (result < Long.parseLong(confMaskRelateCompute.min)) {
            out.append(confMaskRelateCompute.min);
        } else {
            String[] floatArray = Double.toString(result).split("\\.");
            if (confMaskRelateCompute.decimal == 0) {
                out.append(floatArray[0]);
            } else {
                if (floatArray[1].length() < confMaskRelateCompute.decimal) {
                    out.append(floatArray[0]).append(".").append(floatArray[1]);
                } else {
                    out.append(floatArray[0]).append(".")
                            .append(floatArray[1].substring(0, confMaskRelateCompute.decimal));
                }
            }
        }
        return 0;
    }

    private Double compute(Double v1, Double v2, String ch) {
        Double res = 0d;
        switch (ch) {
            case "0":
                res = v1 + v2;
                break;
            case "1":
                res = v1 - v2;
                break;
            case "2":
                res = v1 * v2;
                break;
            case "3":
                if (v2 == 0) {
                    res = 0d;
                } else {
                    res = v1 / v2;
                }
                break;
            default:
        }
        return res;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        this.mask(in, out);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    /**
     * 切分输入值
     *
     * @param in    输入值
     * @param split 切分结果
     * @return 切分成功，返回true，否则返回false
     */
    private boolean splitInput(String in, List<Long> split) {
        if (!in.contains(";")) {
            return false;
        }

        String[] splitIn = in.split(";");
        if (splitIn.length < 2) {
            return false;
        }

        for (int i = 0; i < splitIn.length; i++) {
            String item = splitIn[i];
            try {
                split.add(Long.valueOf(item));
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
