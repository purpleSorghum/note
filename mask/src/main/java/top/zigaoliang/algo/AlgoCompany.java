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
 * 公司（单位）名称算法
 * Created by byc on 10/22/18.
 */
public class AlgoCompany extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoCellphone.class);
    public AlgoCompany() {
        super(AlgoId.COMPANY);
    }
    static IndexMapList indexMapList = null;
    static quickStringMatch cityMatch = new quickStringMatch(50240);
    static {
        indexMapList = HashMapUtil.convertToIndexMap(ComanyName.strCityArray);
        cityMatch.LoadData(ComanyName.strCityArray, true);
    }

    @Override
    public boolean find(String in) {
        //判断公司名称长度是否合法
        if (in.length() < 6 || in.length() > 30) {
            return false;
        }
        if(StringUtils.isBlank(getCompanySuffix(in))){
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskCompany confMaskCompany = (Conf.ConfMaskCompany)confMask;
        return randomCompany(in,out,confMaskCompany);
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
            if (indexMapList.getMap().containsKey(region)) {
                int oldIndex = indexMapList.getMap().get(region);
                int[] indexRange = {0, indexMapList.getList().size() - 1};
                int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
                return indexMapList.getList().get(newIndex);
            } else {
                return region;
            }
        }
    }

    //对单位中的汉字经行仿真
    public String randomChinese(String src){
        if(src.equals("")){
            return "";
        }
        String[] array = src.split("");
        StringBuilder result = new StringBuilder();
        if(array.length <= 2){
            for (int i = 0; i < array.length; i++) {
                result.append(CommonUtil.isChinese(array[i])?
                            AlgoMaskUtil.randomChinese(ComanyName.strComNameArray,1):array[i]);
            }
            return result.toString();
        }else{
            //如果长度大于2，最后两位一般公司的行业信息，不脱敏
            for (int i = 0; i < array.length -2; i++) {
                if (CommonUtil.isChinese(array[i])) {
                    result.append(AlgoMaskUtil.randomChinese(ComanyName.strComNameArray,1));
                }else{
                    //不是中文，返回
                    result.append(array[i]);
                }
            }
            result.append(array[array.length -2]).append(array[array.length -1]);
        }
        return result.toString();
    }

    public int random(String in, StringBuilder out, Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskCompany confMaskCompany = new Conf.ConfMaskCompany();
        confMaskCompany.address = confMaskCustomerName.address;
        return randomCompany(in,out,confMaskCompany);
    }


    public int randomCompany(String in, StringBuilder out, Conf.ConfMaskCompany confMaskCompany){
        ErrorCode errorCode = null;
        StringBuilder res = new StringBuilder();
        try {
            //去掉 公司名称 中的中心词
            String suffix = getCompanySuffix(in);
            String newUnit = in.substring(0, in.length() - suffix.length());

            ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
            cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);
            if (pattern.size() == 0){
                res.append(randomChinese(newUnit)).append(suffix);
                out.append(res.length() > in.length()?in : res);
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
                    if(confMaskCompany.address){
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
                    if(confMaskCompany.address){
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
            out.append(res.length() > in.length()?in : res);
        } catch (Exception e) {
            errorCode = ErrorCode.UNIT_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }



    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskCompany confMaskCompany = (Conf.ConfMaskCompany) confMask;
        return maskBase(in,out,true,confMaskCompany);
    }
    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskCompany confMaskCompany = (Conf.ConfMaskCompany) confMask;
        return maskBase(in,out,false,confMaskCompany);
    }

    public AlgoCover coverPramConf(){
        Conf.ConfMaskCompany confMaskCompany = (Conf.ConfMaskCompany) confMask;
        Conf.ConfMaskCover confMaskCover =new  Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskCompany.symbol;
        confMaskCover.begin = confMaskCompany.begin;
        confMaskCover.end = confMaskCompany.end;
        confMaskCover.direction = confMaskCompany.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in,out);
    }

    public int maskBase(String in, StringBuilder out,boolean flag,Conf.ConfMaskCustomerName confMaskCustomerName) {
        Conf.ConfMaskCompany confMaskCompany = new Conf.ConfMaskCompany();
        confMaskCompany.address = confMaskCustomerName.address;
        confMaskCompany.seed = confMaskCustomerName.seed;
        return maskBase(in,out,flag,confMaskCompany);
    }

    /**
     * 对公司名称脱敏
     * @param
     * @return
     */
    public int maskBase(String in, StringBuilder out, boolean flag, Conf.ConfMaskCompany confMaskCompany){
        ErrorCode errorCode = null;
        try {
            //去掉 公司名称 中的中心词
            String suffix = getCompanySuffix(in);
            String newUnit = in.substring(0, in.length() - suffix.length());

            ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
            cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);

            if (pattern.size() == 0){
                out.append(maskChinese(newUnit,confMaskCompany.seed, flag)).append(suffix);
                return 0;
            }

            if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));

            if (pattern.size() == 0) out.append(maskChinese(newUnit, confMaskCompany.seed, flag));

            for (int i = 0; i < pattern.size(); i++) {
                quickStringMatch.match_Result result = pattern.get(i);
                result.idx_context -= 1;
                if (i == 0 && result.idx_context >= 0) {
                    out.append(maskChinese(newUnit.substring(0, result.idx_context), confMaskCompany.seed, flag));
                    out.append(indexMapList.getList().get(result.idx_keyword));
                }
                if (i > 0) {
                    quickStringMatch.match_Result previous = pattern.get(i - 1);
                    int preEndIdx = previous.idx_context + indexMapList.getList().get(previous.idx_keyword).length();
                    if (result.idx_context > preEndIdx) {
                        out.append(maskChinese(newUnit.substring(preEndIdx, result.idx_context), confMaskCompany.seed, flag));
                    }
                    out.append(indexMapList.getList().get(result.idx_keyword));
                }
                if (i == pattern.size() - 1) {
                    int endIdx = result.idx_context + indexMapList.getList().get(result.idx_keyword).length();
                    if (endIdx < newUnit.length()) {
                        out.append(maskChinese(newUnit.substring(endIdx), confMaskCompany.seed, flag));
                    }
                }
            }
            out.append(suffix);
        } catch (Exception e) {
            errorCode = ErrorCode.UNIT_RANDOM_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    //对公司中的汉字 经行脱敏
    public String maskChinese(String src, int seed, boolean flag) {
        if (src.equals("")) {
            return "";
        }
        String[] array = src.split("");
        StringBuilder result = new StringBuilder();
        if (array.length <= 2) {
            for (int i = 0; i < array.length; i++) {
                result.append(CommonUtil.isChinese(array[i]) ?
                            AlgoMaskUtil.maskChinese(array[i], seed, flag) : array[i]);
            }
            return result.toString();
        } else {
            //如果长度大于2，最后两位一般公司的行业信息，不脱敏
            for (int i = 0; i < array.length - 2; i++) {
                result.append(CommonUtil.isChinese(array[i]) ?
                            AlgoMaskUtil.maskChinese(array[i], seed, flag) : array[i]);
            }
            result.append(array[array.length - 2]).append(array[array.length - 1]);
        }
        return result.toString();
    }

    /**
     * 获得公司的地域信息
     * @return
     */
    public String getCompanyRegion(String in){
        //判断是否有地域信息
        for(int i = 0; i < ComanyName.strCityArray.length; i++){
            if(in.contains(ComanyName.strCityArray[i])){
                return ComanyName.strCityArray[i];
            }
        }
        return "";
    }

    /**
     *  获得公司的后缀名称 比如"公司"，"集团"
     * @param companyName
     * @return
     */
    public String getCompanySuffix(String companyName){
        for (int i = 0; i < ComanyName.comFindType.length; i++) {
            if (companyName.endsWith(ComanyName.comFindType[i])) {
                return ComanyName.comFindType[i];
            }
        }
        return "";
    }



    @Override
    public int random(StringBuilder out) {
        String in = "北京中安威士科技有限公司";

        Conf.ConfMaskCompany conf = new Conf.ConfMaskCompany();

        conf.address = Util.getNumByRange(0, 1) == 0;
        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }


    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (in == null || out == null) {
            return new Object[]{false, "in or out data is invalid company"};
        }

        String suffix = getCompanySuffix(in);
        String newUnit = in.substring(0, in.length() - suffix.length());
        StringBuffer newIn = new StringBuffer();
        StringBuffer newOut = new StringBuffer();

        ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
        cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);
        if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));

        if (pattern.size() == 0) {
            newIn.append(randomChinese(newUnit)).append(suffix);
            newOut.append(randomChinese(newUnit)).append(suffix);
        }

        int[] indexRange = {0, indexMapList.getList().size() - 1};
        for (int i = 0; i < pattern.size(); i++) {
            quickStringMatch.match_Result result = pattern.get(i);
            newIn.append(indexMapList.getList().get(result.idx_keyword));
            newOut.append(indexMapList.getList().get(result.idx_keyword));
        }



        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (!newIn.toString().equals(newOut.toString()))
                    return new Object[]{false, "违反保留所在地信息的策略"};
                break;
            case COVER:
                return validateCover(conf, in,out);
        }
        return new Object[]{true, null};
    }
}
