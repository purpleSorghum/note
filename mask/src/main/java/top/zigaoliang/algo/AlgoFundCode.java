package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.FundCodeName;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 基金代码算法
 * Created by byc on 10/24/18.
 * Update zaj
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoFundCode extends AlgoBase {
    Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private Random random = null;

    public AlgoFundCode() {
        super(AlgoId.FUNDCODE);
    }

    @Override
    public int init(Conf.ConfFind confFind) {
        if (confFind == null) {
            return ErrorCode.CONF_INIT_FIND.getCode();
        }
        this.confFind = confFind;
        return 0;
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskFundCode)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        random = new Random(confMask.seed);
        return super.init(confMask);
    }

    static IndexMapList indexMapList = null;
    static {
        indexMapList = HashMapUtil.convertToIndexMap("/fundcode.txt", FundCodeName.class,"code");
    }


    @Override
    public boolean find(String in) {
        if (in.length() != 6) {
            return false;
        }
        return indexMapList.getMap().containsKey(in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        int randomIndex = Util.getNumByRange(0,indexMapList.getList().size()-1);
        out.append(indexMapList.getList().get(randomIndex));
        return  0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    public int maskBase(String in, StringBuilder out, boolean blag){
        if(indexMapList.getMap().containsKey(in)){
            int[] indexRange = {0, indexMapList.getList().size() -1};
            int newIndex = Util.maskBaseForInteger(indexRange, indexMapList.getMap().get(in), confMask.seed, blag);
            out.append(indexMapList.getList().get(newIndex));
        } else {
            out.append(in);
        }
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskFundCode confMaskFundCode = (Conf.ConfMaskFundCode) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskFundCode.symbol;
        confMaskCover.begin = confMaskFundCode.begin;
        confMaskCover.end = confMaskFundCode.end;
        confMaskCover.direction = confMaskFundCode.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }


    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("000005");
        array.add("000001");
        Conf.ConfMaskFundCode conf = new Conf.ConfMaskFundCode();

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;
        this.mask(array.get(Util.getNumByRange(0, 1)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskFundCode cf = (Conf.ConfMaskFundCode) conf;

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (in.equals(out)) {
                    return new Object[]{false, "违反脱敏策略"};
                }

                break;
            case COVER:
                return validateCover(cf, in, out);

        }
        return new Object[]{true, null};
    }
}
