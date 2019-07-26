package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.FundName;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 基金名称算法
 *
 * @author byc
 * @date 10/24/18
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoFundName extends AlgoBase {
    public AlgoFundName() {
        super(AlgoId.FUNDNAME);
    }

    @Override
    public boolean find(String in) {
        if(in.length() < 4 || in.length() > 20){
            return false;
        }
        return FundName.create(in).isValid();
    }

    @Override
    public int random(String in, StringBuilder out) {
        FundName fundName = FundName.create(in);
        out.append(fundName.random().get());
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        FundName fundName = FundName.create(in);
        if (!fundName.isValid()) {
            out.append(in);
            return 0;
        } else {
            out.append(fundName.mask(confMask.seed).get());
            return 0;
        }
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        FundName fundName = FundName.create(in);
        if (!fundName.isValid()) {
            out.append(in);
            return 0;
        } else {
            out.append(fundName.unMask(confMask.seed).get());
            return 0;
        }
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskFundName confMaskFundName = (Conf.ConfMaskFundName) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskFundName.symbol;
        confMaskCover.begin = confMaskFundName.begin;
        confMaskCover.end = confMaskFundName.end;
        confMaskCover.direction = confMaskFundName.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int random(StringBuilder out) {
        String in = "嘉实增强信用定期债券";

        Conf.ConfMaskFundName conf = new Conf.ConfMaskFundName();

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }


    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskFundName cf = (Conf.ConfMaskFundName) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (in.equals(out)) {
                    return new Object[]{false, "违反基金名称脱敏策略"};
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }
}
