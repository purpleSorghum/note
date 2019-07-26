package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.CommonContants;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Hong Kong and Macao passport港澳通行证算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPassportHKAM extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoPassportHKAM.class);

    public AlgoPassportHKAM() {
        super(AlgoId.PASSPORTHKAM);
    }

    static {
        Arrays.sort(CommonContants.hkAndMacLetter);
    }

    @Override
    public boolean find(String in) {
        if (in.length() != 9) {
            return false;
        }
        String regEx = "^([W,C]\\d{8})$";
        if (!Pattern.compile(regEx).matcher(in).matches()) {
            //数字格式不正确
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskPassportHKAM confMaskPassportHKAM = (Conf.ConfMaskPassportHKAM) confMask;
        ErrorCode errorCode = null;
        try {
            if (confMaskPassportHKAM.handLetter) {
                //保留号码前的字母
                out.append(in.substring(0, 1));
            } else {
                int temp = Util.getNumByRange(0, CommonContants.hkAndMacLetter.length - 1);
                out.append(CommonContants.hkAndMacLetter[temp]);
            }
            if (confMaskPassportHKAM.number) {
                //保留证件前四位数字
                out.append(in.substring(1, 5));
            } else {
                out.append(Util.getRandowNumber(4));
            }
            out.append(Util.getRandowNumber(4));
        } catch (Exception e) {
            errorCode = ErrorCode.PASSPORTHKAM_INPUT;
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

    public AlgoCover coverPramConf() {
        Conf.ConfMaskPassportHKAM confMaskPassportHKAM = (Conf.ConfMaskPassportHKAM) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskPassportHKAM.symbol;
        confMaskCover.begin = confMaskPassportHKAM.begin;
        confMaskCover.end = confMaskPassportHKAM.end;
        confMaskCover.direction = confMaskPassportHKAM.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public int maskBase(String in, StringBuilder out, boolean flag) {
        ErrorCode errorCode = null;
        Conf.ConfMaskPassportHKAM confMaskPassportHKAM = (Conf.ConfMaskPassportHKAM) confMask;
        try {
            out.append(confMaskPassportHKAM.handLetter == true ?
                    in.substring(0, 1) : maskPassportHKAM(in.substring(0, 1), confMaskPassportHKAM.seed, flag));
            char[] numberArray = null;
            if (confMaskPassportHKAM.number == true) {
                //保留前四位数字
                out.append(in.substring(1, 5));
                numberArray = in.substring(5).toCharArray();
            } else {
                numberArray = in.substring(1).toCharArray();
            }
            for (int i = 0; i < numberArray.length; i++) {
                out.append(Util.maskBaseForInteger(Util.numberArray, numberArray[i] - 48, confMaskPassportHKAM.seed, flag));
            }
        } catch (Exception e) {
            errorCode = ErrorCode.PASSPORTHKAM_MASK_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * 判断港澳通行证开头字符是否合理
     *
     * @return
     */
    public boolean passportLetter(String str) {
        for (int i = 0; i < CommonContants.hkAndMacLetter.length; i++) {
            if (str.equals(CommonContants.hkAndMacLetter[i])) {
                return true;
            }
        }
        return false;
    }

    //对港澳通行证上的字母进行脱敏
    public String maskPassportHKAM(String hkamletter, int seed, boolean flag) {
        int index = Arrays.binarySearch(CommonContants.hkAndMacLetter, hkamletter);
        int[] hkamIndexRange = {0, CommonContants.hkAndMacLetter.length - 1};
        return CommonContants.hkAndMacLetter[Util.maskBaseForInteger(hkamIndexRange, index, seed, flag)];
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(2);
        array.add("W12345678");
        array.add("C23456789");
        Conf.ConfMaskPassportHKAM conf = new Conf.ConfMaskPassportHKAM();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.handLetter = Util.getNumByRange(0, 1) == 1;
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

        Conf.ConfMaskPassportHKAM cf = (Conf.ConfMaskPassportHKAM) conf;

        char inHandLetter = in.charAt(0);
        char outHandLetter = out.charAt(0);
        String inNumber = in.substring(1, 5);
        String outNumber = out.substring(1, 5);

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (cf.handLetter) {
                    if (inHandLetter != outHandLetter) {
                        return new Object[]{false, "违反保留字母策略"};
                    }
                } else {
                    if (inHandLetter == outHandLetter) {
                        return new Object[]{false, "违反保留字母策略"};
                    }
                }

                if (cf.number) {
                    if (!inNumber.equals(outNumber)) {
                        return new Object[]{false, "违反保留前4个数字策略"};
                    }
                } else {
                    if (inNumber.equals(outNumber)) {
                        return new Object[]{false, "违反保留前4个数字策略"};
                    }
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }
}
