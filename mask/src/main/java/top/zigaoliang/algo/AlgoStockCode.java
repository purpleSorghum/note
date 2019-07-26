package top.zigaoliang.algo;



import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.Stock;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 股票代码算法
 *
 * @author byc
 * @date 10/24/18
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoStockCode extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoStockCode.class);

    public AlgoStockCode() {
        super(AlgoId.STOCKCODE);
    }

    static IndexMapList indexMapList = null;
    static Map<String, List<String>> groupMap = new HashMap<>();

    {
        indexMapList = HashMapUtil.convertToIndexMap("/stock.txt", Stock.class, "code");
        groupBySuffixThree();
    }

    @Override
    public boolean find(String in) {
        if (in.length() != 6) {
            return false;
        }
        return HashMapUtil.containsKey(indexMapList.getMap(), in);
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskStockCode confMaskStockCode = (Conf.ConfMaskStockCode)confMask;
        if(confMaskStockCode.prefix){
            List<String> groupThreeList = groupMap.get(in.substring(0,3));
            out.append(groupThreeList.get(Util.getNumByRange(0,groupThreeList.size()-1)));
        }else{
            int index = Util.getNumByRange(0,indexMapList.getList().size()-1);
            out.append(indexMapList.getList().get(index));
        }
        return 0;
    }

    //根据股票的前三位分到不同的组中
    private static void groupBySuffixThree() {
        if (groupMap.size() == 0) {
            for (int i = 0; i < indexMapList.getList().size(); i++) {
                String stockSuffixThree = indexMapList.getList().get(i).substring(0, 3);
                if (groupMap.containsKey(stockSuffixThree)) {
                    List<String> stockCodeList = groupMap.get(stockSuffixThree);
                    stockCodeList.add(indexMapList.getList().get(i));
                } else {
                    List<String> stockCodeList = new ArrayList<>();
                    stockCodeList.add(indexMapList.getList().get(i));
                    groupMap.put(stockSuffixThree, stockCodeList);
                }
            }
        }
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskCommon(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskCommon(in, out, false);
    }

    public int maskCommon(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskStockCode confMaskStockCode = (Conf.ConfMaskStockCode) confMask;
        int oldIndex = HashMapUtil.getMapValue(indexMapList.getMap(), in);
        int[] indexRange = {0, indexMapList.getList().size() - 1};
        int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, confMaskStockCode.seed, flag);
        out.append(indexMapList.getList().get(newIndex));
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskStockCode conf = (Conf.ConfMaskStockCode) confMask;
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
        String in = "000009";
        Conf.ConfMaskStockCode conf = new Conf.ConfMaskStockCode();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.prefix = Util.getNumByRange(0, 1) == 1;
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }


    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        String inPrefix = in.substring(0, 2);
        String outPrefix = out.substring(0, 2);

        Conf.ConfMaskStockCode cf = (Conf.ConfMaskStockCode) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (cf.prefix && inPrefix.equals(outPrefix)) {
                    return new Object[]{false, "违反股票脱敏策略"};
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }

    public static IndexMapList getIndexMapList() {
        return indexMapList;
    }

    public static void setIndexMapList(IndexMapList indexMapList) {
        AlgoStockCode.indexMapList = indexMapList;
    }

    public static Map<String, List<String>> getGroupMap() {
        return groupMap;
    }

    public static void setGroupMap(Map<String, List<String>> groupMap) {
        AlgoStockCode.groupMap = groupMap;
    }
}
