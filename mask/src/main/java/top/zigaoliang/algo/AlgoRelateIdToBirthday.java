package top.zigaoliang.algo;

import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.IdCardMaskUtil;

/**
 * 关联算法--身份证取生日
 * Created by byc on 10/24/18.
 */
public class AlgoRelateIdToBirthday extends AlgoBase {
    public AlgoRelateIdToBirthday() {
        super(AlgoId.RELATEIDTOBIRTHDAY);
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
        //1. 验证身份证的合法性
        //2. 身份证取生日
        out.append(IdCardMaskUtil.getBirthday(in));
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        this.mask(in, out);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }
}
