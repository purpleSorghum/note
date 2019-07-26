package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.PlateNumberContans;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 车架号算法
 *
 * @author byc
 * @date 10/24/18
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoFrameNumber extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoFrameNumber.class);

    public AlgoFrameNumber() {
        super(AlgoId.FRAMENUMBER);
        attr = 28;
    }

    @Override
    public boolean find(String in) {
        ErrorCode errorCode = null;
        if (in.length() != 17) {
            return false;
        }
        //校验组合的字母是否合法
        if (!checkPlateNumber(in)) {
            errorCode = ErrorCode.FRAMENUMBER_FORMAT_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        ErrorCode errorCode = null;
        try {
            Conf.ConfMaskFrameNumber confMaskFrameNumber = (Conf.ConfMaskFrameNumber) confMask;
            out.append(confMaskFrameNumber.country == true ? in.substring(0, 1) : getRandomFrameNumber(in.substring(0, 1)));
            out.append(confMaskFrameNumber.factory == true ? in.substring(1, 2) : getRandomFrameNumber(in.substring(1, 2)));
            out.append(confMaskFrameNumber.type == true ? in.substring(2, 3) : getRandomFrameNumber(in.substring(2, 3)));
            out.append(getRandomFrameNumber(in.substring(3)));
        } catch (Exception e) {
            errorCode = ErrorCode.FRAMENUMBER_RANDOM_ERROR;
            log.info(errorCode.getMsg() + "; 输入数据：" + in);
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
        Conf.ConfMaskFrameNumber confMaskFrameNumber = (Conf.ConfMaskFrameNumber) confMask;
        switch (confMaskFrameNumber.coverType) {
            case 1:
                out.append(confMaskFrameNumber.symbol).append(in.substring(1));
                break;
            case 2:
                out.append(in.substring(0, 1)).append(confMaskFrameNumber.symbol).append(in.substring(2));
                break;
            case 3:
                out.append(in.substring(0, 2)).append(confMaskFrameNumber.symbol).append(in.substring(3));
                break;
            default:
                out.append(in);
        }
        return 0;
    }

    public int maskBase(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskFrameNumber confMaskFrameNumber = (Conf.ConfMaskFrameNumber) confMask;
        ErrorCode errorCode = null;
        try {
            out.append(confMaskFrameNumber.country == true ?
                    in.substring(0, 1) : maskCarRrameChar(in.substring(0, 1), confMaskFrameNumber.seed, flag));
            out.append(confMaskFrameNumber.factory == true ?
                    in.substring(1, 2) : maskCarRrameChar(in.substring(1, 2), confMaskFrameNumber.seed, flag));
            out.append(confMaskFrameNumber.type == true ?
                    in.substring(2, 3) : maskCarRrameChar(in.substring(2, 3), confMaskFrameNumber.seed, flag));
            String[] carFrameArr = in.substring(3).split("");
            for (int i = 0; i < carFrameArr.length; i++) {
                out.append(maskCarRrameChar(carFrameArr[i], confMaskFrameNumber.seed, flag));
            }
        } catch (Exception e) {
            errorCode = ErrorCode.FRAMENUMBER_MASK_ERROR;
            log.info(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }


    //验证车架数字
    public boolean checkPlateNumber(String carFrameNumber) {
        if (CommonUtil.strHasArrayDom(carFrameNumber, PlateNumberContans.carNotFrameLetter)) {
            return false;
        }
        //车牌的编号  5位数字和大写字母的组合
        String regEex = "^[A-Z0-9]{17}$";
        Pattern pattern = Pattern.compile(regEex);
        if (!pattern.matcher(carFrameNumber).matches()) {
            return false;
        }
        return true;
    }

    //随机生成一个车架的字母或数字
    public String getRandomFrameNumber(String number) {
        StringBuilder result = new StringBuilder();
        char[] charArr = number.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (Character.isLetter(charArr[i])) {
                int index = Util.getNumByRange(0, PlateNumberContans.carFrameLetter.length - 1);
                result.append(PlateNumberContans.carFrameLetter[index]);
            } else if (Character.isDigit(charArr[i])) {
                int index = Util.getNumByRange(0, PlateNumberContans.carFrameNumber.length - 1);
                result.append(PlateNumberContans.carFrameNumber[index]);
            } else {
                result.append(charArr[i]);
            }
        }
        return result.toString();
    }

    //对车架号的某个字母进行脱敏
    public String maskCarRrameChar(String src, int seed, boolean flag) {
        StringBuilder result = new StringBuilder();
        char[] charArr = src.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (Character.isLetter(charArr[i])) {
                int oldIndex = PlateNumberContans.carFrameLetterMap.get(String.valueOf(charArr[i]));
                int[] indexRange = {0, PlateNumberContans.carFrameLetter.length - 1};
                int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
                result.append(PlateNumberContans.carFrameLetter[newIndex]);
            } else if (Character.isDigit(charArr[i])) {
                result.append(Util.maskBaseForInteger(Util.numberArray, charArr[i] - 48, seed, flag));
            } else {
                result.append(charArr[i]);
            }
        }
        return result.toString();
    }


    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("H946Y3GU8AT7DS83S");
        array.add("P880Z6HJ7ZN8HF78F");
        Conf.ConfMaskFrameNumber conf = new Conf.ConfMaskFrameNumber();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.country = Util.getNumByRange(0, 1) == 1;
        conf.factory = Util.getNumByRange(0, 1) == 1;
        conf.type = Util.getNumByRange(0, 1) == 1;

        this.confMask = conf;
        this.mask(array.get(Util.getNumByRange(0, 1)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskFrameNumber cf = (Conf.ConfMaskFrameNumber) conf;

        String inCountry = in.substring(0, 1);
        String inFactory = in.substring(1, 2);
        String inType = in.substring(2, 3);
        String inOther = in.substring(3);

        String outCountry = out.substring(0, 1);
        String outFactory = out.substring(1, 2);
        String outType = out.substring(2, 3);
        String outOther = out.substring(3);


        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //根据当前的算法，在保留的情况下，脱敏前后一定相等，在不保留的情况向，前后可能相等也可能不等
                if (cf.country && !inCountry.equals(outCountry)) {
                    return new Object[]{false, "违反保留国籍策略"};
                }

                if (cf.factory && !inFactory.equals(outFactory)) {
                    return new Object[]{false, "违反保留制造厂策略"};
                }

                if (cf.type && !inType.equals(outType)) {
                    return new Object[]{false, "违反保留汽车类型策略"};
                }

                break;
            case COVER:

                switch (cf.coverType) {
                    //遮蔽国别
                    case 1: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inCountry.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outCountry.equals(coverPart.toString())
                                && inFactory.equals(outFactory)
                                && inType.equals(outType)
                                && inOther.equals(outOther))) {
                            return new Object[]{false, "违反遮蔽省策略"};
                        }
                    }
                    break;
                    //遮蔽制造厂
                    case 2: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inFactory.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outFactory.equals(coverPart.toString())
                                && inCountry.equals(outCountry)
                                && inType.equals(outType)
                                && inOther.equals(outOther))) {
                            return new Object[]{false, "违反遮蔽地区策略"};
                        }
                    }
                    break;
                    //遮蔽汽车类型
                    case 3: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inType.length(); i++) {
                            coverPart.append(cf.symbol);
                        }

                        if (!(outType.equals(coverPart.toString())
                                && inCountry.equals(outCountry)
                                && inFactory.equals(outFactory)
                                && inOther.equals(outOther))) {
                            return new Object[]{false, "违反遮蔽市策略"};
                        }
                    }
                    break;
                }
                break;

        }
        return new Object[]{true, null};
    }


}
