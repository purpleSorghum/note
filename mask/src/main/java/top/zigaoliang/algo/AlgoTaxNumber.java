package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.contant.RegionContants;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.IdCardMaskUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 税号算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoTaxNumber extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoTaxNumber.class);

    public AlgoTaxNumber() {
        super(AlgoId.TAXNUMBER);
    }

    static {
        Arrays.sort(RegionContants.regionCodeArray);
    }

    @Override
    public boolean find(String in) {
        ErrorCode errorCode = null;
        /**
         * 税号的编码规则：
         *   15位  6位区域号 + 9位组织机构代码  组织机构代码 = 8位数字 + 校验码
         *   17位  15位的身份证号 + 2位编号（如：01 02 03）
         *   18位  18位的身份证
         *   20位  18位身份证号 + 2位编号（如: 01 02 03）
         */

        if (!(in.length() == 15 || in.length() == 17 || in.length() == 18 || in.length() == 20)) {
            return false;
        }
        String regNumber = "[0-9]*";
        String regOrign = "^[0-9A-Z]{8}[0-9X]$";
        if (in.length() == 15) {
            if (CommonUtil.outOfIntRange(in.substring(0, 6))) {
                return false;
            }
            // 如果身份证前6位的地区码不在Contants.regionCodeArray，则地区码有误
            if (Util.arraySearch(RegionContants.regionCodeArray, Integer.parseInt(in.substring(0, 6))) < 0) {
                errorCode = ErrorCode.ID_CODINGERROR;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
            //组织机构代码
            String organizationCode = in.substring(6);
            //验证 组织机构校验码
            if (!Pattern.compile(regOrign).matcher(organizationCode).matches() || !Util.validateOrign(organizationCode)) {
                errorCode = ErrorCode.TAXNUMBER_INPUT_ORIGN;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        }
        if (in.length() == 17) {
            if (!IdCardMaskUtil.validateIdCard(in.substring(0, 15)) ||
                    !Pattern.compile(regNumber).matcher(in.substring(15, 16)).matches()) {
                errorCode = ErrorCode.TAXNUMBER_INPUT;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        }
        if (in.length() == 18) {
            if (!IdCardMaskUtil.validateIdCard(in)) {
                errorCode = ErrorCode.TAXNUMBER_INPUT;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        }
        if (in.length() == 20) {
            if (!IdCardMaskUtil.validateIdCard(in.substring(0, 18)) ||
                    !Pattern.compile(regNumber).matcher(in.substring(18)).matches()) {
                errorCode = ErrorCode.TAXNUMBER_INPUT;
                log.debug(errorCode.getMsg() + "; 输入数据：" + in);
                return false;
            }
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskTaxNumber confMaskTaxNumber = (Conf.ConfMaskTaxNumber) confMask;
        ErrorCode errorCode = null;
        try {
            if (in.length() == 15) {
                //保留区域码
                if (confMaskTaxNumber.region) {
                    out.append(in.substring(0, 6));
                } else {
                    int index = Util.getNumByRange(0, RegionContants.regionCodeArray.length - 1);
                    out.append(RegionContants.regionCodeArray[index]);
                }
                String orignCode = Util.getRandowNumber(8);
                out.append(orignCode).append(Util.computeOrignCheck(orignCode));
            }
            if (in.length() == 17 || in.length() == 18 || in.length() == 20) {
                Conf.ConfMaskIdCard confMaskIdCard = initIdCard(confMaskTaxNumber.region, confMaskTaxNumber.region, confMaskTaxNumber.region, confMaskTaxNumber.seed);
                if (in.length() == 17) {
                    out.append(IdCardMaskUtil.getRandomIdCard(confMaskIdCard, in.substring(0, 15), out))
                            .append(Util.getRandowNumber(2));
                } else if (in.length() == 18) {
                    StringBuilder result = new StringBuilder();
                    IdCardMaskUtil.getRandomIdCard(confMaskIdCard, in, result);
                    out.append(result.toString());
                } else {
                    StringBuilder result = new StringBuilder();
                    IdCardMaskUtil.getRandomIdCard(confMaskIdCard, in.substring(0, 18), result);
                    out.append(result.toString()).append(Util.getRandowNumber(2));
                }
            }
        } catch (Exception e) {
            errorCode = ErrorCode.TAXNUMBER_INPUT_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, true, out);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, false, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskTaxNumber confMaskTaxNumber = (Conf.ConfMaskTaxNumber) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskTaxNumber.symbol;
        confMaskCover.begin = confMaskTaxNumber.begin;
        confMaskCover.end = confMaskTaxNumber.end;
        confMaskCover.direction = confMaskTaxNumber.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }


    public int maskBase(String in, boolean flag, StringBuilder out) {
        Conf.ConfMaskTaxNumber confMaskTaxNumber = (Conf.ConfMaskTaxNumber) confMask;
        ErrorCode errorCode = null;
        try {
            if (in.length() == 15) {
                if (confMaskTaxNumber.region) {
                    out.append(in.substring(0, 6));
                } else {
                    int index = Util.arraySearch(RegionContants.regionCodeArray, Integer.parseInt(in.substring(0, 6)));
                    int[] indexRange = {0, RegionContants.regionCodeArray.length - 1};
                    int newIndex = Util.maskBaseForInteger(indexRange, index, confMaskTaxNumber.seed, flag);
                    out.append(RegionContants.regionCodeArray[newIndex]);
                }
                String orignCode = AlgoMaskUtil.maskNumberStr(in.substring(6, 14), confMaskTaxNumber.seed, flag);
                out.append(orignCode);
                out.append(Util.computeOrignCheck(orignCode));
            }
            if (in.length() == 17 || in.length() == 18 || in.length() == 20) {
                Conf.ConfMaskIdCard confMaskIdCard = initIdCard(confMaskTaxNumber.region, confMaskTaxNumber.region, confMaskTaxNumber.region, confMaskTaxNumber.seed);
                StringBuilder result = new StringBuilder();
                if (in.length() == 17) {
                    IdCardMaskUtil.maskBase(in.substring(0, 15), confMaskIdCard, flag, result);
                    out.append(result);
                    out.append(AlgoMaskUtil.maskNumberStr(in.substring(15), confMaskTaxNumber.seed, flag));
                } else if (in.length() == 18) {
                    IdCardMaskUtil.maskBase(in, confMaskIdCard, flag, result);
                    out.append(result);
                } else {
                    IdCardMaskUtil.maskBase(in.substring(0, 18), confMaskIdCard, flag, result);
                    out.append(result).append(AlgoMaskUtil.maskNumberStr(in.substring(18), confMaskTaxNumber.seed, flag));
                }
            }
        } catch (Exception e) {
            errorCode = ErrorCode.TAXNUMBER_INPUT_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    public Conf.ConfMaskIdCard initIdCard(boolean provice, boolean city, boolean county, int seed) {
        Conf.ConfMaskIdCard confMaskIdCard = new Conf.ConfMaskIdCard();
        confMaskIdCard.province = provice;
        confMaskIdCard.city = city;
        confMaskIdCard.county = county;
        confMaskIdCard.birthday = false;
        confMaskIdCard.sex = false;
        confMaskIdCard.check = false;
        confMaskIdCard.seed = seed;
        return confMaskIdCard;
    }


    private List<String> buildTestInitData() {
        List<String> array = null;
        if (this.confMask == null) {
            Conf.ConfMaskTaxNumber conf = new Conf.ConfMaskTaxNumber();
            conf.region = true;
            this.confMask = conf;
        }


        return array;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>();
        array.add("420102199007113719");
        array.add("42010219900711371901");
        array.add("42010219900711371902");

        Conf.ConfMaskTaxNumber conf = new Conf.ConfMaskTaxNumber();

        conf.region = Util.getNumByRange(0, 1) == 0;

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(array.get(Util.getNumByRange(0, 2)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }
        String inRegin = in.substring(0, 6);
        String outRegin = out.substring(0, 6);
        Conf.ConfMaskTaxNumber cf = (Conf.ConfMaskTaxNumber) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //保留企业注册地行政区划码
                if (cf.isRegion()) {
                    if (!inRegin.equals(outRegin)) {
                        return new Object[]{false, "违反保留企业类型策略"};
                    }
                } else {
                    if (inRegin.equals(outRegin)) {
                        return new Object[]{false, "违反保留企业类型策略"};
                    }
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }
}
