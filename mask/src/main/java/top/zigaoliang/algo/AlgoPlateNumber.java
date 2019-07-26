package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Arrays;
import java.util.List;


/**
 * 车牌号算法
 *
 * @author byc
 * @date 10/24/18
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPlateNumber extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoPlateNumber.class);

    public AlgoPlateNumber() {
        super(AlgoId.PLATENUMBER);
        attr = 27;
    }

    @Override
    public boolean find(String in) {
        //车牌号长度是8位字符
        //提示：如果长度不满足 后面的字符串截取就会报错
        if (in.length() < 7 || in.length() > 8) {
            return false;
        }
        if(!PlateNumberContans.carNumberProvinceMap.containsKey(in.substring(0, 1))){
            return false;
        }
        if (PlateNumberContans.carLetterMap.get(in.substring(1, 2)) == null) {
            return false;
        }
        String[] plateArray = splitPlateNumber(in, getPlateNumberSplit(in));
        if (!checkPlateNumber(plateArray[1])) {
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskPlateNumber confMaskPlateNumber = (Conf.ConfMaskPlateNumber) confMask;
        ErrorCode errorCode = null;
        StringBuilder result = new StringBuilder();
        try {
            //仿真省
            result.append(confMaskPlateNumber.province == true ? in.substring(0, 1)
                        : randomPlateSuffix(PlateNumberContans.carNumberProvince));
            //仿真地区
            result.append(confMaskPlateNumber.region ? in.substring(1, 2)
                        : randomPlateSuffix(PlateNumberContans.carLetter));

            String split = getPlateNumberSplit(in);
            result.append(split == null ? "" : split);
            String[] plateNumber = splitPlateNumber(in, split);

            result.append(confMaskPlateNumber.number == true ? plateNumber[1]
                        : getRandomPlateNumber(plateNumber[1]));
            out.append(result);
        } catch (Exception e) {
            errorCode = ErrorCode.PLATENUMBER_RANDOM_ERROR;
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
        Conf.ConfMaskPlateNumber confMaskPlateNumber = (Conf.ConfMaskPlateNumber) confMask;
        String province = in.substring(0, 1);
        String region = in.substring(1, 2);

        String split = getPlateNumberSplit(in);

        String number = split == null?in.substring(2):in.substring(3);
        switch (confMaskPlateNumber.coverType) {
            case 1:
                out.append(confMaskPlateNumber.symbol).append(region)
                            .append(split == null?"":split).append(number);
                break;
            case 2:
                out.append(province).append(confMaskPlateNumber.symbol)
                            .append(split == null?"":split).append(number);
                break;
            case 3:
                out.append(province).append(region).append(split == null?"":split)
                            .append(CommonUtil.coverBySymbol(confMaskPlateNumber.symbol, number.length()));
                break;
            default:
                out.append(in);
        }
        return 0;
    }

    public String[] splitPlateNumber(String in,String split){
        String[] plateArray = new String[2];
        if (split != null) {
            if (split.equals(".")) {
                //"." 需要转义
                plateArray = in.split("\\.");
            } else {
                plateArray = in.split(split);
            }
        } else {
            plateArray[0] = in.substring(0, 2);
            plateArray[1] = in.substring(2);

        }
        return plateArray;
    }

    public int maskBase(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskPlateNumber confMaskPlateNumber = (Conf.ConfMaskPlateNumber) confMask;
        ErrorCode errorCode = null;
        StringBuilder result = new StringBuilder();
        try {
            result.append(in.substring(0, 2));
            String split = getPlateNumberSplit(in);
            result.append(split == null ? "" : split);

            //对车牌编号进行脱敏
            String[] plateNumber = splitPlateNumber(in, split);

            result.append(maskPlateNumber(plateNumber[1], confMaskPlateNumber.seed, flag));
            out.append(result);
        } catch (Exception e) {
            errorCode = ErrorCode.PLATENUMBER_MASK_ERROR;
            log.info(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    //车牌号上的字母进行脱敏
    public String maskPlateNumber(String in, int seed, boolean flag) {
        StringBuilder result = new StringBuilder();
        char[] charArr = in.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (CommonUtil.isChinese(String.valueOf(charArr[i]))) {
                result.append(charArr[i]);
            } else if (Character.isLetter(charArr[i])) {
                int oldIndex = PlateNumberContans.carLetterMap.get(String.valueOf(charArr[i]));
                int[] indexRange = {0, PlateNumberContans.carLetter.length - 1};
                int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, seed, flag);
                result.append(PlateNumberContans.carLetter[newIndex]);
            } else if (Character.isDigit(charArr[i])) {
                result.append(Util.maskBaseForInteger(Util.numberArray, charArr[i] - 48, seed, flag));
            } else {
                result.append(charArr[i]);
            }
        }
        return result.toString();
    }


    //获得车牌号中的分割字符
    public String getPlateNumberSplit(String in) {
        String split = CommonUtil.getDomFromStr(in, CommonUtil.plateNumberSplitChar);
        if (split != null) {
            return split;
        }
        return null;
    }

    //验证车牌数字
    public boolean checkPlateNumber(String plateNumber) {
        if(StringUtils.isBlank(plateNumber)){
            return false;
        }
        if (CommonUtil.strHasArrayDom(plateNumber, PlateNumberContans.carNotLetter)) {
            return false;
        }
        if (plateNumber.length() != 5) {
            return false;
        } else {
            char[] charArr = plateNumber.toCharArray();
            for (int i = 0; i < charArr.length - 2; i++) {
                if (!Character.isLetter(charArr[i]) && !Character.isDigit(charArr[i])) {
                    return false;
                }
            }
            if (Character.isDigit(charArr[charArr.length - 1]) && Character.isLetter(charArr[charArr.length - 1])
                        && PlateNumberContans.carWithChinese.contains(charArr[charArr.length - 1])
            ) {
                return false;
            }
        }
        return true;
    }

    //随机生成一个车牌号 编号字符组合
    public String getRandomPlateNumber(String number) {
        StringBuilder result = new StringBuilder();
        char[] charArr = number.toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (Character.isLetter(charArr[i])) {
                int index = Util.getNumByRange(0, PlateNumberContans.carLetter.length - 1);
                result.append(PlateNumberContans.carLetter[index]);
            } else if (Character.isDigit(charArr[i])) {
                int index = Util.getNumByRange(0, PlateNumberContans.carNumber.length - 1);
                result.append(PlateNumberContans.carNumber[index]);
            } else {
                result.append(charArr[i]);
            }
        }
        return result.toString();
    }

    //车牌省 地区随机
    public String randomPlateSuffix(String[] srcArray) {
        int randomIndex = Util.getNumByRange(0, srcArray.length - 1);
        return srcArray[randomIndex];
    }

    //对车牌号中的字符进行脱敏
    public String maskPlateChar(String[] srcArray, String src, int seed, boolean flag) {
        int oldIndex = Arrays.binarySearch(srcArray, src);
        int[] regionRange = {0, srcArray.length - 1};
        int newIndex = Util.maskBaseForInteger(regionRange, oldIndex, seed, flag);
        return srcArray[newIndex];
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("鄂A-X4819");
        array.add("鄂A-X3532");
        Conf.ConfMaskPlateNumber conf = new Conf.ConfMaskPlateNumber();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.province = Util.getNumByRange(0, 1) == 1;
        conf.region = Util.getNumByRange(0, 1) == 1;
        conf.number = Util.getNumByRange(0, 1) == 1;

        this.confMask = conf;
        this.mask(array.get(Util.getNumByRange(0, 1)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskPlateNumber cf = (Conf.ConfMaskPlateNumber) conf;

        String inProvince = in.substring(0, 1);
        String outProvince = out.substring(0, 1);

        String inRegion = in.substring(1, 2);
        String outRegion = out.substring(1, 2);

        String inNumber = getPlateNumber(in);
        String outNumber = getPlateNumber(out);

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //根据当前的算法，在保留的情况下，脱敏前后一定相等，在不保留的情况向，前后可能相等也可能不等
                if (cf.province && !inProvince.equals(outProvince)) {
                    return new Object[]{false, "违反保留省策略"};
                }

                if (cf.region && !inRegion.equals(outRegion)) {
                    return new Object[]{false, "违反保留地区策略"};
                }

                if (cf.number && !inNumber.equals(outNumber)) {
                    return new Object[]{false, "违反保留数字策略"};
                }

                break;
            case COVER:

                switch (cf.coverType) {
                    //遮蔽省
                    case 1: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inProvince.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outProvince.equals(coverPart.toString())
                                    && inNumber.equals(outNumber)
                                    && inRegion.equals(outRegion))) {
                            return new Object[]{false, "违反遮蔽省策略"};
                        }
                    }
                    break;
                    //遮蔽地区
                    case 2: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inRegion.length(); i++) {
                            coverPart.append(cf.symbol);
                        }
                        if (!(outRegion.equals(coverPart.toString())
                                    && inProvince.equals(outProvince)
                                    && inNumber.equals(outNumber))) {
                            return new Object[]{false, "违反遮蔽地区策略"};
                        }
                    }
                    break;
                    //遮蔽数字
                    case 3: {
                        StringBuilder coverPart = new StringBuilder();
                        for (int i = 0; i < inNumber.length(); i++) {
                            coverPart.append(cf.symbol);
                        }

                        if (!(outNumber.equals(coverPart.toString())
                                    && inProvince.equals(outProvince)
                                    && inRegion.equals(outRegion))) {
                            return new Object[]{false, "违反遮蔽市策略"};
                        }
                    }
                    break;
                }
                break;

        }
        return new Object[]{true, null};
    }

    //获取车牌号中的数字
    private String getPlateNumber(String in) {
        String split = getPlateNumberSplit(in);
        String[] plateNumber = null;
        if (split.equals(".")) {
            //"." 需要转义
            plateNumber = in.split("\\.");
        } else {
            plateNumber = in.split(split);
        }

        if (plateNumber.length >= 2) {
            return plateNumber[1];
        }
        return "";
    }
}
