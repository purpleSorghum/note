package top.zigaoliang.algo;


import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.ComanyName;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.Util;
import top.zigaoliang.util.quickStringMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 客户名称算法
 *
 * @author byc
 * @date 10/24/18
 * @Update zaj
 */
public class AlgoCustomerName extends AlgoBase {

    private AlgoChineseName chineseName = new AlgoChineseName();
    private AlgoCompany company = new AlgoCompany();
    private AlgoUnitName unitName = new AlgoUnitName();

    public AlgoCustomerName() {
        super(AlgoId.CUSTOMERNAME);
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskCustomerName)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        if(in.length()<8 || in.length() > 100){
            return false;
        }
        //客户名称分割
        String[] array = splitCustomerName(in);
        boolean companFlag = false;
        boolean chineseNameFlag = false;
        for (int i = 0; i < array.length; i++) {
            if(company.find(array[i]) || unitName.find(array[i])){
                companFlag = true;
            }
            if(chineseName.find(array[i]) ){
                chineseNameFlag = true;
            }
        }
        if(!companFlag || !chineseNameFlag){
            //同时包含中文姓名（单位名称）和公司名称合法
            return false;
        }
        return true;
    }

    private String[] splitCustomerName(String in) {
        return in.split("[-,_.，：:| ]");
    }
    //获取字符串中分隔符
    private String getSplitChar(String in){
        String[] splitChar = {",","-","."," ","_","，",":","：","|"};
        for (int i = 0; i < splitChar.length; i++) {
            if(in.contains(splitChar[i])){
                return splitChar[i];
            }
        }
        return ",";
    }


    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskCustomerName confMaskCustomerName = (Conf.ConfMaskCustomerName)confMask;
        String[] array = splitCustomerName(in);
        for (int i = 0; i < array.length; i++) {
            StringBuilder sb = new StringBuilder();
            if(company.find(array[i])){
                company.random(array[i], sb,confMaskCustomerName);
                array[i] = sb.toString();
            }else if(unitName.find(array[i])){
                unitName.random(array[i], sb,confMaskCustomerName);
                array[i] = sb.toString();
            }else if(chineseName.find(array[i])){
                chineseName.random(array[i], sb,confMaskCustomerName);
                array[i] = sb.toString();
            }
        }
        //还原字符串的格式
        String splitChar = getSplitChar(in);
        rebackFormat(array,splitChar,out);
        return 0;
    }

    /**
     * 还原字符串格式
     * @param array  输入值切分后的数组
     * @param splitChar  分隔符
     * @param out    脱敏后的结果
     */
    private void rebackFormat(String[] array, String splitChar, StringBuilder out) {
        for (int i = 0; i < array.length; i++) {
            out.append(array[i]);
            if(i != array.length-1){
                out.append(splitChar);
            }
        }
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in,out,true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in,out,false);
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in,out);
    }


    public AlgoCover coverPramConf(){
        Conf.ConfMaskCustomerName confMaskCustomerName = (Conf.ConfMaskCustomerName) confMask;
        Conf.ConfMaskCover confMaskCover =new  Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskCustomerName.symbol;
        confMaskCover.begin = confMaskCustomerName.begin;
        confMaskCover.end = confMaskCustomerName.end;
        confMaskCover.direction = confMaskCustomerName.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    public int maskBase(String in, StringBuilder out,boolean flag){
        Conf.ConfMaskCustomerName confMaskCustomerName = (Conf.ConfMaskCustomerName)confMask;
        String[] array = splitCustomerName(in);
        String[] resultArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            StringBuilder sb = new StringBuilder();
            if(company.find(array[i])){
                company.maskBase(array[i], sb,flag,confMaskCustomerName);
                resultArray[i] = sb.toString();
            }else if(unitName.find(array[i])){
                unitName.maskBase(array[i], sb,flag,confMaskCustomerName);
                resultArray[i] = sb.toString();
            }else if(chineseName.find(array[i])){
                chineseName.maskBase(array[i], sb,flag,confMaskCustomerName);
                resultArray[i] = sb.toString();
            }else{
                resultArray[i] = array[i];
            }
        }
        //还原字符串的格式
        String splitChar = getSplitChar(in);
        rebackFormat(resultArray,splitChar,out);
        return 0;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("张三丰-中安威士（北京）科技有限公司");
        array.add("中安威士（北京）科技有限公司-张三丰");
        String in = array.get(Util.getNumByRange(0, 1));

        Conf.ConfMaskCustomerName conf = new Conf.ConfMaskCustomerName();

        conf.address = Util.getNumByRange(0, 1) == 0;
        conf.firstName = Util.getNumByRange(0, 1) == 0;
        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);

        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (in == null || out == null) {
            return new Object[]{false, "in or out data is invalid phone"};
        }

        AlgoCompany algoCompany  = new AlgoCompany();
        AlgoUnitName algoUnitName  = new AlgoUnitName();
        AlgoChineseName algoChineseName = new AlgoChineseName();
        List<String> arrayResIn = new ArrayList<>();
        List<String> arrayResOut = new ArrayList<>();
        List<String> type = new ArrayList<>();

        String[] arrayIn = splitCustomerName(in);
        String[] arrayOut = splitCustomerName(out);
        String[] resultArrayIn = new String[arrayIn.length];
        String[] resultArrayOut = new String[arrayOut.length];
        StringBuffer newIn = new StringBuffer();
        StringBuffer newOut = new StringBuffer();
        String infirstName = "";
        String insecondName = "";
        String outfirstName = "";
        String outsecondName = "";

        for (int i = 0; i < arrayIn.length; i++) {
            if(algoCompany.find(arrayIn[i])){
                type.add("1");
                resultArrayIn[i] = arrayIn[i].toString();

                String suffix = algoCompany.getCompanySuffix(arrayIn[i]);
                String newUnit = arrayIn[i].substring(0, arrayIn[i].length() - suffix.length());

                ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
                algoCompany.cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);
                if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));

                if (pattern.size() == 0) {
                    newIn.append(algoCompany.randomChinese(newUnit)).append(suffix);
                }

                for (int m = 0; m < pattern.size(); m++) {
                    quickStringMatch.match_Result result = pattern.get(m);
                    newIn.append(algoCompany.indexMapList.getList().get(result.idx_keyword));
                }

                arrayResIn.add(newIn.toString());

            }else if(unitName.find(arrayIn[i])){
                type.add("2");
                resultArrayIn[i] = arrayIn[i].toString();

                String suffix = algoUnitName.getUnitSuffix(arrayIn[i]);
                String newUnit = arrayIn[i].substring(0, arrayIn[i].length() - suffix.length());

                ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
                algoUnitName.cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);

                arrayResIn.add(pattern.toString());

            }else if(chineseName.find(arrayIn[i])){
                type.add("3");
                infirstName = algoChineseName.getSurname(arrayIn[i]);
                insecondName = arrayIn[i].substring(arrayIn[i].length()-infirstName.length()-1);
                if (((Conf.ConfMaskCustomerName) conf).firstName)
                    arrayResIn.add(infirstName);
                else
                    arrayResIn.add(insecondName);

            }else{
                type.add("4");
                arrayResIn.add(arrayIn[i]);
            }
        }



        for (int j = 0; j < arrayOut.length; j++) {
            if(algoCompany.find(arrayOut[j])){
                resultArrayOut[j] = arrayOut[j].toString();

                String suffix = algoCompany.getCompanySuffix(arrayOut[j]);
                String newUnit = arrayOut[j].substring(0, arrayOut[j].length() - suffix.length());

                ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
                algoCompany.cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);
                if (pattern.size() > 1) pattern.sort(Comparator.comparingInt(c -> c.idx_context));

                if (pattern.size() == 0) {
                    newOut.append(algoCompany.randomChinese(newUnit)).append(suffix);
                }

                for (int m = 0; m < pattern.size(); m++) {
                    quickStringMatch.match_Result result = pattern.get(m);
                    newOut.append(algoCompany.indexMapList.getList().get(result.idx_keyword));
                }

                arrayResOut.add(newOut.toString());

            }else if(unitName.find(arrayOut[j])){
                resultArrayOut[j] = arrayOut[j].toString();

                String suffix = algoUnitName.getUnitSuffix(arrayOut[j]);
                String newUnit = arrayOut[j].substring(0, arrayOut[j].length() - suffix.length());

                ArrayList<quickStringMatch.match_Result> pattern = new ArrayList<>();
                algoUnitName.cityMatch.Match(newUnit, pattern, ComanyName.strCityArray.length);

                arrayResOut.add(pattern.toString());

            }else if(chineseName.find(arrayOut[j])){
                outfirstName = algoChineseName.getSurname(arrayOut[j]);
                outsecondName = arrayIn[j].substring(arrayIn[j].length()-infirstName.length()-1);
                if (((Conf.ConfMaskCustomerName) conf).firstName)
                    arrayResOut.add(outfirstName);
                else
                    arrayResOut.add(outsecondName);

            }else{
                arrayResOut.add(arrayOut[j]);
            }
        }
//
//        Conf.ConfMaskCustomerName confMaskCustomerName = (Conf.ConfMaskCustomerName)confMask;
//        Conf.ConfMaskCompany confMaskCompany = new Conf.ConfMaskCompany();
//        confMaskCompany.address = confMaskCustomerName.address;
//        StringBuilder companyOut = new StringBuilder();
//        algoCompany.maskBase(in,companyOut,true,confMaskCompany);
//        System.out.println();

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                for (int i=0,j=0,k=0; i<arrayResIn.size() && j<arrayResOut.size()&& k<type.size();++i,++j,++k){
                    switch (type.get(k)){
                        case "1":
                            if (((Conf.ConfMaskCustomerName) conf).address) {

                                if (!arrayResIn.get(i).equals(arrayResOut.get(j))) {
                                    return new Object[]{false, "违反保留注册地区信息策略"};
                                }
                            }

                        case "2":
                            if (((Conf.ConfMaskCustomerName) conf).address) {

                                if (!arrayResIn.get(i).equals(arrayResOut.get(j))) {
                                    return new Object[]{false, "违反保留注册地区信息策略"};
                                }
                            }

                        case "3":
                            if (((Conf.ConfMaskCustomerName) conf).firstName) {

                                if (!arrayResIn.get(i).equals(arrayResOut.get(j))) {
                                    return new Object[]{false, "违反保留姓策略"};
                                }
                            }

                            if (!((Conf.ConfMaskCustomerName) conf).firstName) {

                                if (!arrayResIn.get(i).equals(arrayResOut.get(j))) {
                                    return new Object[]{false, "违反保留姓策略"};
                                }
                            }
                    }



                }

                break;
            case COVER:
                return validateCover(conf, in,out);

        }
        return new Object[]{true, null};
    }

}
