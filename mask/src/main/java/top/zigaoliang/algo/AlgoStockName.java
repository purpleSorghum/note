package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.conf.Stock;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 股票名称算法
 *
 * @author byc
 * @date 10/24/18
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoStockName extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoStockName.class);
    public AlgoStockName() {
        super(AlgoId.STOCKNAME);
    }

    static IndexMapList indexMapList = null;
    {
        indexMapList = HashMapUtil.convertToIndexMap("/stock.txt",Stock.class,"name");
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskStockName)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        if(in.length() > 6 || in.length() <2){
            return false;
        }
        return HashMapUtil.containsKey(indexMapList.getMap(),in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        out.append(indexMapList.getList().get(Util.getNumByRange(0,indexMapList.getList().size()-1)));
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in,out,false);
    }
    public int maskBase(String in, StringBuilder out, boolean flag){
        Conf.ConfMaskStockName confMaskStockName = (Conf.ConfMaskStockName)confMask;
        int oldIndex = HashMapUtil.getMapValue(indexMapList.getMap(),in);
        int[] indexRange = {0,indexMapList.getList().size() -1};
        int newIndex = Util.maskBaseForInteger(indexRange,confMaskStockName.seed,oldIndex,flag);
        out.append(indexMapList.getList().get(newIndex));
        return 0;
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskStockName conf = (Conf.ConfMaskStockName) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = conf.symbol;
        confMaskCover.begin = conf.begin;
        confMaskCover.end = conf.end;
        confMaskCover.direction = conf.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int random(StringBuilder out) {
        String in = "华锦股份";

        Conf.ConfMaskStockName conf = new Conf.ConfMaskStockName();

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

        Conf.ConfMaskStockName cf = (Conf.ConfMaskStockName) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (in.equals(out)) {
                    return new Object[]{false, "违反股票脱敏策略"};
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }
}
