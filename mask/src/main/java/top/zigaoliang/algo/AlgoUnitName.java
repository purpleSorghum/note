package top.zigaoliang.algo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.ComanyName;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.Util;
import top.zigaoliang.util.quickStringMatch;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 单位名称
 * @author yangying
 * @date 19-1-4
 */
public class AlgoUnitName extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoDate.class);
    public AlgoUnitName() {
        super(AlgoId.UNITNAME);
    }
    static IndexMapList indexMapList = null;
    static quickStringMatch cityMatch = new quickStringMatch(10240);
    static {
        indexMapList = HashMapUtil.convertToIndexMap(ComanyName.strCityArray);
        cityMatch.LoadData(ComanyName.strCityArray, true);
    }

    @Override
    public boolean find(String in) {
        //判断公司名称长度是否合法
        if (in.length() < 4 || in.length() > 100) {
            return false;
        }
        String companySuffix = getUnitSuffix(in);
        if (StringUtils.isBlank(companySuffix)) {
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskUnitName confMaskUnitName = new Conf.ConfMaskUnitName();
        return randomUnit(in,out,confMaskUnitName);
    }

    public int random(String in, StringBuilder out, Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskUnitName  confMaskUnitName= new Conf.ConfMaskUnitName();
        confMaskUnitName.address = confMaskCustomerName.address;
        confMaskUnitName.seed = confMaskCustomerName.seed;
        return randomUnit(in,out,confMaskUnitName);
    }

    public int randomUnit(String in, StringBuilder out, Conf.ConfMaskUnitName confMaskUnitName){
        ErrorCode errorCode = null;
        StringBuilder res = new StringBuilder();
        try {
            //去掉 公司名称 中的中心词
            String suffix = getUnitSuffix(in);
            String newUnit = in.substring(0, in.length() - suffix.length());

            ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
            cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);

            if (pattern.size() == 0){
                res.append(randomChinese(newUnit)).append(suffix);
                out.append(res.length()>in.length()?in : res);
                return 0;
            }

            if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));
            int[] indexRange = {0, indexMapList.getList().size() - 1};
            int newIndex = Util.getNumByRange(indexRange[0],indexRange[1]);
            for (int i = 0; i < pattern.size(); i++) {
                quickStringMatch.match_Result result = pattern.get(i);
                result.idx_context -= 1;
                if (i == 0 && result.idx_context >= 0) {
                    res.append(randomChinese(newUnit.substring(0, result.idx_context)));
                    if(confMaskUnitName.address){
                        res.append(indexMapList.getList().get(result.idx_keyword));
                    }else{
                        res.append(indexMapList.getList().get(newIndex));
                    }
                }
                if (i > 0) {
                    quickStringMatch.match_Result previous = pattern.get(i - 1);
                    int preEndIdx = previous.idx_context + indexMapList.getList().get(previous.idx_keyword).length();
                    if (result.idx_context > preEndIdx) {
                        res.append(randomChinese(newUnit.substring(preEndIdx, result.idx_context)));
                    }
                    if(confMaskUnitName.address){
                        res.append(indexMapList.getList().get(result.idx_keyword));
                    }else{
                        res.append(indexMapList.getList().get(newIndex));
                    }
                }
                if (i == pattern.size() - 1) {
                    int endIdx = result.idx_context + indexMapList.getList().get(result.idx_keyword).length();
                    if (endIdx < newUnit.length()) {
                        res.append(randomChinese(newUnit.substring(endIdx)));
                    }
                }
            }
            res.append(suffix);
            out.append(res.length()>in.length()?in : res);
        } catch (Exception e) {
            errorCode = ErrorCode.UNIT_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }


    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskUnitName confMaskUnitName = (Conf.ConfMaskUnitName) confMask;
        return maskBase(in,out,true,confMaskUnitName);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskUnitName confMaskUnitName = (Conf.ConfMaskUnitName) confMask;
        return maskBase(in,out,false,confMaskUnitName);
    }

    public AlgoCover coverPramConf(){
        Conf.ConfMaskUnitName confMaskUnitName = (Conf.ConfMaskUnitName) confMask;
        Conf.ConfMaskCover confMaskCover =new  Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskUnitName.symbol;
        confMaskCover.begin = confMaskUnitName.begin;
        confMaskCover.end = confMaskUnitName.end;
        confMaskCover.direction = confMaskUnitName.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in,out);
    }

    /**
     * 根据参数脱敏单位中的地域信息
     * @return
     */
    public String randomRegionByParam(String region, int seed, boolean flag) {
        if (flag) {
            return region;
            //保留地址信息
        } else {
            //不保留地址信息
            if (HashMapUtil.containsKey(indexMapList.getMap(), region)) {
                int oldIndex = indexMapList.getMap().get(region);
                int[] indexRange = {0, indexMapList.getList().size() - 1};
                int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
                return indexMapList.getList().get(newIndex);
            } else {
                return region;
            }
        }
    }

    /**
     *  获得单位的后缀名称 比如"单位","所","局"
     * @param unitName
     * @return
     */
    public String getUnitSuffix(String unitName){
        for (int i = 0; i < ComanyName.unitFindType.length; i++) {
            if (unitName.endsWith(ComanyName.unitFindType[i])) {
                return ComanyName.unitFindType[i];
            }
        }
        return "";
    }

    //获得单位中的地域信息
    public String getUnitRegion(String unitName){
        return CommonUtil.strHasArray(unitName,ComanyName.strCityArray);
    }

    //对单位中的汉字经行仿真
    public String randomChinese(String src){
        String[] array = src.split("");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (CommonUtil.isChinese(array[i])) {
                result.append(AlgoMaskUtil.randomChinese(ComanyName.strComNameArray,1));
            }else{
                //不是中文，返回
                result.append(array[i]);
            }
        }
        return result.toString();
    }

    //对单位中的汉字经行脱敏
    public String maskChinese(String src,int seed,boolean flag){
        String[] array = src.split("");
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            if(CommonUtil.isChinese(array[i])){
                result.append(AlgoMaskUtil.maskChinese(array[i],seed,flag));
            }else{
                //不是中文，返回
                result.append(array[i]);
            }
        }
        return result.toString();
    }

    public int maskBase(String in, StringBuilder out,boolean flag,Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskUnitName confMaskUnitName = new Conf.ConfMaskUnitName();
        confMaskUnitName.address = confMaskCustomerName.address;
        confMaskUnitName.seed = confMaskCustomerName.seed;
        return maskBase(in,out,flag,confMaskUnitName);
    }
    public int maskBase(String in,StringBuilder out, boolean flag,Conf.ConfMaskUnitName confMaskUnitName){
        ErrorCode errorCode = null;
        StringBuilder res = new StringBuilder();
        try {
            //去掉 公司名称 中的中心词
            String suffix = getUnitSuffix(in);
            String newUnit = in.substring(0, in.length() - suffix.length());

            ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
            cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);

            if (pattern.size() == 0){
                res.append(maskChinese(newUnit,confMaskUnitName.seed,flag)).append(suffix);
                out.append(res.length()>in.length()?in : res);
                return 0;
            }

            if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));
            for (int i = 0; i < pattern.size(); i++) {
                quickStringMatch.match_Result result = pattern.get(i);
                result.idx_context -= 1;
                if (i == 0 && result.idx_context >= 0) {
                    res.append(maskChinese(newUnit.substring(0, result.idx_context), confMaskUnitName.seed, flag));
                    res.append(indexMapList.getList().get(result.idx_keyword));
                }
                if (i > 0) {
                    quickStringMatch.match_Result previous = pattern.get(i - 1);
                    int preEndIdx = previous.idx_context + indexMapList.getList().get(previous.idx_keyword).length();
                    if (result.idx_context > preEndIdx) {
                        res.append(maskChinese(newUnit.substring(preEndIdx, result.idx_context), confMaskUnitName.seed, flag));
                    }
                    res.append(indexMapList.getList().get(result.idx_keyword));
                }
                if (i == pattern.size() - 1) {
                    int endIdx = result.idx_context + indexMapList.getList().get(result.idx_keyword).length();
                    if (endIdx < newUnit.length()) {
                        res.append(maskChinese(newUnit.substring(endIdx), confMaskUnitName.seed, flag));
                    }
                }
            }
            res.append(suffix);
            out.append(res.length()>in.length()?in : res);
        } catch (Exception e) {
            errorCode = ErrorCode.UNIT_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

}
