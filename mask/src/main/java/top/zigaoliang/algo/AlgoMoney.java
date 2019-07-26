package top.zigaoliang.algo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.AlgoNumberUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;

/**
 * 金额数字算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoMoney extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoMoney.class);
    public AlgoMoney() {
        super(AlgoId.MONEY);
    }
    @Override
    public boolean find(String in) {
        ErrorCode errorCode = null;
        if (StringUtils.isBlank(in) || CommonUtil.outOfDoubleRange(in)) {
            return false;
        }
        if (!AlgoNumberUtil.findDecimal(in)) {
            //不是整数也不是小数 返回false
            errorCode = ErrorCode.MONEY_INPUT;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskMoney confMaskMoney = (Conf.ConfMaskMoney) confMask;
        ErrorCode errorCode = null;
        try {
            //记录数据的正负性  true 正数  false 负数
//
            //分别获得整数和小数的部分
            String[] array = in.split("\\.");
            if (array.length != 2) {
                //不是小数
                out.append(in);
                return 0;
            }
//            out.append(relation);
            if (confMaskMoney.number) {
                //保留整数部分
                out.append(array[0])
                            .append(".").append(Util.getRandowNumber(array[1].length()));
            } else {
                String relation = Double.parseDouble(in) >= 0 ? "" : "-";
                //保留小数部分
                out.append(relation)
                            .append(Util.getIntegerNoZeroFirst(relation == ""?array[0].length():array[0].length()-1))
                            .append(".").append(array[1]);
            }
        } catch (Exception e) {
            errorCode = ErrorCode.MONEY_UNDERMIN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, out, true);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        if(!in.contains(".")) {
            out.append(in);
            return 0;
        }
        String[] array = in.split("\\.");
        Conf.ConfMaskMoney confMaskMoney = (Conf.ConfMaskMoney)confMask;
        if(confMaskMoney.coverType){
            out.append(CommonUtil.coverBySymbol(confMaskMoney.symbol,array[0].length())).append(".").append(array[1]);
        }else{
            out.append(array[0]).append(".").append(CommonUtil.coverBySymbol(confMaskMoney.symbol,array[1].length()));
        }
        return 0;
    }

    public int maskBase(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskMoney confMaskMoney = (Conf.ConfMaskMoney) confMask;
        ErrorCode errorCode = null;
        try {
            if(!in.contains(".")){
                out.append(in);
                return 0;
            }else {
                String[] array = in.split("\\.");
                //保留整数部分
                out.append(array[0]).append(".");

                boolean flagNumber = false;
                String dicimal = AlgoMaskUtil.maskNumberStr(array[1],confMaskMoney.seed,flag);
                /**
                 * 如果是1.9  正向脱敏之后是2.0
                 * 存到数据库变成2
                 * 逆向的时候2是整数，就不会脱敏
                 */
                if(Integer.parseInt(dicimal) == 0){
                    out.append(array[1]);
                }else {
                    out.append(dicimal);
                }
            }
//            //记录数据的正负性  true 正数  false 负数
//            String relation = Double.parseDouble(in) >= 0 ? "" : "-";
//            if (confMaskMoney.decimal) {
//                //保留精度
//                if (in.contains(".")) {
//                    //如果是小数
//                    //分别获得整数和小数的部分
//                    String[] array = in.split("\\.");
//                    out.append(relation).append(AlgoMaskUtil.maskNumberStrNoZeroFirst(array[0], confMaskMoney.seed, flag)).append(".");
//                    out.append(getDecimalPart(array[1], confMaskMoney.seed, flag));
//                } else {
//                    //如果是整数
//                    out.append(AlgoMaskUtil.maskNumberStrNoZeroFirst(in, confMaskMoney.seed, flag));
//                }
//            } else {
//                //不保留精度
//                //小数后保留1或2或3位小数
//                if (in.contains(".")) {
//                    String[] array = in.split("\\.");
//                    out.append(relation).append(AlgoMaskUtil.maskNumberStrNoZeroFirst(array[0], confMaskMoney.seed, flag)).append(".");
//                    out.append(AlgoMaskUtil.maskByRandomSeed(Integer.parseInt(array[1]), confMaskMoney.seed, flag));
//                } else {
//                    out.append(AlgoMaskUtil.maskNumberStrNoZeroFirst(in, confMaskMoney.seed, flag));
//                }
//            }
        } catch (Exception e) {
            errorCode = ErrorCode.MONEY_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * 保留精度的同时对小数部分进行脱敏
     * @param decimal
     * @param seed
     * @param flag
     * @return
     */
    public String getDecimalPart(String decimal,int seed, boolean flag){
        StringBuilder out = new StringBuilder();
        String[] decimalRange = decimal.split("");
        for (int i = 0; i < decimalRange.length; i++) {
            int result = Util.maskBaseForInteger(Util.numberArray, Integer.parseInt(decimalRange[i]), seed, flag);
            out.append(result);
        }
        return out.toString();
    }


    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>();
        array.add("12.34");
        array.add("12.0");
        array.add("0.34");
        array.add("-12.34");
        array.add("-12.0");
        array.add("-0.34");

        Conf.ConfMaskMoney conf = new Conf.ConfMaskMoney();

        conf.number = Util.getNumByRange(0, 1) == 0;
        conf.max = Util.getNumByRange(51, 100);
        conf.min = Util.getNumByRange(0, 50);

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(array.get(Util.getNumByRange(0, 5)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (in == null || out == null) {
            return new Object[]{false, "in or out data is invalid money"};
        }

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (((Conf.ConfMaskMoney) conf).number){
                    if (!((in.substring(0,in.indexOf(".")).equals(out.substring(0,out.indexOf("."))) ) && !(in.substring(in.indexOf(".")).equals(out.substring(out.indexOf("."))))&& Integer.parseInt(out) > ((Conf.ConfMaskMoney) conf).min && Integer.parseInt(out) < ((Conf.ConfMaskMoney) conf).max)){
                        return new Object[]{false, "违反保留数字整数部分策略"};
                    }
                }

                if (!((Conf.ConfMaskMoney) conf).number){
                    if (!(!(in.substring(0,in.indexOf(".")).equals(out.substring(0,out.indexOf(".")))) && out.substring(out.indexOf(".")).equals(in.substring(in.indexOf("."))) && Integer.parseInt(out) > ((Conf.ConfMaskMoney) conf).min && Integer.parseInt(out) < ((Conf.ConfMaskMoney) conf).max)){
                        return new Object[]{false, "违反保留数字小数部分策略"};
                    }
                }


                break;

            case COVER:
                if (((Conf.ConfMaskMoney) conf).coverType){
                    StringBuffer coverPart = new StringBuffer();
                    for (int i = 0; i < in.substring(0,in.indexOf(".")).length(); i++) {
                        coverPart.append(conf.symbol);
                    }
                    if (!coverPart.equals(out.substring(0,out.indexOf("."))) && in.substring(in.indexOf(".")).equals(out.substring(out.indexOf(".")))){
                        return new Object[]{false, "违反遮蔽整数位策略"};
                    }
                }

                if (!((Conf.ConfMaskMoney) conf).coverType){
                    StringBuffer coverPart = new StringBuffer();
                    for (int i = 0; i < in.substring(in.indexOf(".") +1).length(); i++) {
                        coverPart.append(conf.symbol);
                    }
                    if (!(in.substring(0,in.indexOf(".")).equals(out.substring(0,out.indexOf("."))) && coverPart.toString().equals(out.substring(out.indexOf(".") +1)))){
                        return new Object[]{false, "违反遮蔽小数位策略"};
                    }
                }

                break;
        }
        return new Object[]{true, null};
    }




}
