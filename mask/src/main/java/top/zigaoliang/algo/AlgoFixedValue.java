package top.zigaoliang.algo;

import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;

/**
 * 固定值算法
 *
 * @author byc
 * @date 10/24/18
 */
public class AlgoFixedValue extends AlgoBase {
    public AlgoFixedValue() {
        super(AlgoId.FIXEDVALUE);
    }

    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskFixedValue conf = (Conf.ConfMaskFixedValue)confMask;
        String value = conf.getValue();
        out.append(value);
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return random(in, out);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }
}
