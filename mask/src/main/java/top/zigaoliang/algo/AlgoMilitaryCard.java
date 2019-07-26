package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 军官证算法
 *
 * @author byc
 * @date 10/24/18
 * Update zaj
 * 南|北|沈|兰|成|济|广|海|空|参|政|后|装|武字第(\d{8})号
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoMilitaryCard extends AlgoBase {

    //    private static final Pattern pattern = Pattern.compile("[南|北|沈|兰|成|济|广|海|e空|参|政|后|装|武]{1}[字|字号]{1}[第|-|_| ]{1}[\\(|]{1}\\d{8}[\\)|]{1}[号|]");
    private static final List<String> unitList = new ArrayList<String>() {{
        add("南");
        add("北");
        add("沈");
        add("兰");
        add("成");
        add("济");
        add("广");
        add("海");
        add("空");
        add("参");
        add("政");
        add("后");
        add("装");
        add("武");
    }};
    private Random random = null;

    public AlgoMilitaryCard() {
        super(AlgoId.MILITARYCARD);
        attr = 22;
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskMilitaryCard)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        this.random = new Random(confMask.seed);
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        if(in.length() >14 || in.length() < 9){
            return false;
        }
        String[] special = {"字", "号", "第", "-", "_", " ", "(", ")", "（", "）", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        //第一个字符是关键字，后面有8个数字就算军官号
        if (Strings.isNullOrEmpty(in)) {
            return false;
        }
        String[] charArray = in.split("");
        if (!unitList.contains(charArray[0])) {
            return false;
        }
        //军官证中可能含有的字符
        if (!CommonUtil.containSpecialChar(in, special)) {
            return false;
        }
        //必须有6，7，8位连续的数字
        if (!hasContinNumber(in)) {
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskMilitaryCard tmpConf = (Conf.ConfMaskMilitaryCard) confMask;
        String unit = in.substring(0, 1);
        if (tmpConf.unit) {
            out.append(unit);
        } else {
            int newIndex = Util.getNumByRange(0, unitList.size() - 1);
            out.append(unitList.get(newIndex));
        }
        char[] charArray = in.toCharArray();
        for (int i = 1; i < charArray.length; i++) {
            if (!Character.isDigit(charArray[i])) {
                out.append(charArray[i]);
            } else {
                //是数字，对数字经行仿真
                if (tmpConf.number) {
                    out.append(charArray[i]);
                } else {
                    out.append(Util.getNumByRange(0, 9));
                }
            }
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
        /**
         * 遮蔽单位简称，就是遮蔽第一个字符，遮蔽编号，就遮蔽军官证中的数字
         */
        Conf.ConfMaskMilitaryCard tmpConf = (Conf.ConfMaskMilitaryCard) confMask;
        if (tmpConf.coverType) {
            out.append(tmpConf.symbol).append(in.substring(1));
        } else {
            char[] arr = in.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (!Character.isDigit(arr[i])) {
                    out.append(arr[i]);
                } else {
                    out.append(tmpConf.symbol);
                }
            }
        }
        return 0;
    }

    public int maskBase(String in, StringBuilder out, boolean flag) {
        Conf.ConfMaskMilitaryCard tmpConf = (Conf.ConfMaskMilitaryCard) confMask;
        String unit = in.substring(0, 1);
        if (tmpConf.unit) {
            out.append(unit);
        } else {
            int oldIndex = unitList.indexOf(unit);
            int[] indexRange = {0, unitList.size() - 1};
            int newIndex = Util.maskBaseForInteger(indexRange, oldIndex, tmpConf.seed, flag);
            out.append(unitList.get(newIndex));
        }
        char[] charArray = in.toCharArray();
        for (int i = 1; i < charArray.length; i++) {
            if (!Character.isDigit(charArray[i])) {
                out.append(charArray[i]);
            } else {
                //是数字，对数字经行仿真
                if (tmpConf.number) {
                    out.append(Util.maskBaseForInteger(Util.numberArray, charArray[i] - 48, tmpConf.seed, flag));
                } else {
                    out.append(charArray[i]);
                }
            }
        }
        return 0;
    }

    //判断字符串中是否有连续的6，7，8位数字
    public static boolean hasContinNumber(String in) {
        char[] charArray = in.toCharArray();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < charArray.length; i++) {
            if (Character.isDigit(charArray[i]) && (i + 5) < (charArray.length - 1)) {
                temp.append(charArray[i]).append(charArray[i + 1]).append(charArray[i + 2])
                        .append(charArray[i + 3]).append(charArray[i + 4]).append(charArray[i + 5]);
                break;
            }
        }
        try {
            Integer.parseInt(temp.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(1);
        array.add("南字号第(00345678)号");
        Conf.ConfMaskMilitaryCard conf = new Conf.ConfMaskMilitaryCard();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.unit = Util.getNumByRange(0, 1) == 1;
        conf.number = Util.getNumByRange(0, 1) == 1;
        this.confMask = conf;

        this.mask(array.get(0), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskMilitaryCard cf = (Conf.ConfMaskMilitaryCard) conf;

        String inUnit = in.substring(0, 1);
        String inNumber = getMilitaryNumber(in);

        String outUnit = out.substring(0, 1);
        String outNumber = getMilitaryNumber(out);

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //根据当前的算法，在保留的情况下，脱敏前后一定相等，在不保留的情况向，前后可能相等也可能不等
                if (cf.unit && !inUnit.equals(outUnit)) {
                    return new Object[]{false, "违反保留单位简称策略"};
                }

                if (cf.number && !inNumber.equals(outNumber)) {
                    return new Object[]{false, "违反保留编号策略"};
                }

                break;
            case COVER:
                //遮蔽单位简称
                if (cf.coverType) {
                    StringBuilder coverPart = new StringBuilder();
                    for (int i = 0; i < inUnit.length(); i++) {
                        coverPart.append(cf.symbol);
                    }
                    if (!(outUnit.equals(coverPart.toString())
                            && inNumber.equals(outNumber))) {
                        return new Object[]{false, "违反遮蔽单位简称策略"};
                    }
                }
                //遮蔽编号
                else {
                    StringBuilder coverPart = new StringBuilder();
                    for (int i = 0; i < inNumber.length(); i++) {
                        coverPart.append(cf.symbol);
                    }
                    if (!(outNumber.equals(coverPart.toString())
                            && inUnit.equals(outUnit))) {
                        return new Object[]{false, "违反遮蔽编号策略"};
                    }
                }
        }
        return new Object[]{true, null};
    }

    //获取军官证编号
    private String getMilitaryNumber(String in) {
        StringBuilder out = new StringBuilder();
        char[] charArray = in.toCharArray();
        for (int i = 1; i < charArray.length; i++) {
            if (Character.isDigit(charArray[i])) {
                out.append(charArray[i]);
            }
        }
        return out.toString();
    }

}
