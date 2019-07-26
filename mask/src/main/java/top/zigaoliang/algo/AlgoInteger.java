package top.zigaoliang.algo;

import org.apache.commons.lang3.StringUtils;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoNumberUtil;
import top.zigaoliang.util.CommonUtil;


/**
 * 整数算法
 *
 * @author byc
 * @date 10/24/18
 */
public class AlgoInteger extends AlgoBase {
    public AlgoInteger() {
        super(AlgoId.INTEGER);
    }
    @Override
    public boolean find(String in) {
        return AlgoNumberUtil.findInteger(in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskInteger confMaskInteger = (Conf.ConfMaskInteger)confMask;
        return AlgoNumberUtil.integerByRandom(in, out,confMaskInteger);
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskInteger confMaskInteger = (Conf.ConfMaskInteger)confMask;
        return AlgoNumberUtil.maskInteger(in,out,confMaskInteger,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskInteger confMaskInteger = (Conf.ConfMaskInteger)confMask;
        return AlgoNumberUtil.maskInteger(in,out,confMaskInteger,false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskInteger confMaskInteger = (Conf.ConfMaskInteger) confMask;
        String splitChar = "";
        if (in.contains("-")) {
            splitChar = "-";
        } else if (in.contains("+")) {
            splitChar = "+";
        }
        if (StringUtils.isNotBlank(splitChar)) {
            in = in.substring(1);
        }
        out.append(splitChar);
        switch (confMaskInteger.coverType) {
            case 1:
                if (in.length() <= 2) {
                    out.append(in);
                } else {
                    out.append(CommonUtil.coverBySymbol(confMaskInteger.symbol, 2))
                            .append(in.substring(2));
                }
                break;
            case 2:
                if (in.length() == 3) {
                    out.append(in.substring(0, 1))
                            .append(confMaskInteger.symbol)
                            .append(in.substring(2));
                } else {
                    int indexDecimal = in.length() % 2;
                    out.append(in.substring(0, indexDecimal - 1))
                            .append(CommonUtil.coverBySymbol(confMaskInteger.symbol, 2))
                            .append(in.substring(indexDecimal + 1));
                }
                break;
            case 3:
                if (in.length() <= 2) {
                    out.append(in);
                } else {
                    out.append(in.substring(0, in.length() - 1))
                            .append(CommonUtil.coverBySymbol(confMaskInteger.symbol, 2));
                }
                break;
            default:
                out.append(in);
        }
        return 0;
    }


}
