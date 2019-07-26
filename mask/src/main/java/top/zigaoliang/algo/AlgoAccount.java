package top.zigaoliang.algo;

import com.google.common.base.Strings;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.Util;

import java.util.Random;

/**
 * 开户账号算法
 *
 * @author byc
 * @date 10/24/18
 * @Update zaj
 */
public class AlgoAccount extends AlgoBase {
    private Random random = null;
    public AlgoAccount() {
        super(AlgoId.ACCOUNT);
    }
    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskAccount)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        if (Strings.isNullOrEmpty(in) || in.length() != 15) {
            return false;
        }
        for (int i = 0; i < in.length(); i++) {
            if (!Character.isDigit(in.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        random = new Random(confMask.seed);
        for (int i = 0; i < in.length(); i++) {
            out.append(Util.getNumByRange(0,9));
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        for (int i = 0; i < in.length(); i++) {
            out.append(Util.maskBaseForInteger(Util.numberArray,in.charAt(i) - 48,confMask.seed,true));
        }
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        for (int i = 0; i < in.length(); i++) {
            out.append(Util.maskBaseForInteger(Util.numberArray,in.charAt(i) - 48,confMask.seed,false));
        }
        return 0;
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in,out);
    }

    public AlgoCover coverPramConf(){
        Conf.ConfMaskAccount confMaskAccount = (Conf.ConfMaskAccount) confMask;
        Conf.ConfMaskCover confMaskCover =new  Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskAccount.symbol;
        confMaskCover.begin = confMaskAccount.begin;
        confMaskCover.end = confMaskAccount.end;
        confMaskCover.direction = confMaskAccount.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }
}
