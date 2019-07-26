package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 中国护照算法
 * Created by byc on 10/24/18.
 * Update zaj
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoPassportChinese extends AlgoBase {
    private static  String[] passportSuffix = {"G","D","S","E","P"};
    public AlgoPassportChinese() {
        super(AlgoId.PASSPORTCHINESE);
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskPassportChinese)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        Pattern pattern = Pattern.compile("([G|D|S|E|P]{1}\\d{8})");
        if(in.length() != 9){
            return false;
        }
        return pattern.matcher(in).matches();
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskPassportChinese tmpConf = (Conf.ConfMaskPassportChinese) confMask;
        if (tmpConf.type) {
            out.append(in.substring(0, 1));
        } else {
            out.append(passportSuffix[Util.getNumByRange(0, passportSuffix.length - 1)]);
        }
        if (tmpConf.number) {
            out.append(in.substring(1));
        } else {
            out.append(Util.getRandowNumber(8));
        }
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in,out,true);
    }

    public int maskBase(String in, StringBuilder out, boolean flag){
        Conf.ConfMaskPassportChinese tmpConf = (Conf.ConfMaskPassportChinese) confMask;
        out.append(in.substring(0,1));
        out.append(AlgoMaskUtil.maskNumberStr(in.substring(1),tmpConf.seed,flag));
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in,out,false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskPassportChinese confMaskPassportChinese = (Conf.ConfMaskPassportChinese) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = confMaskPassportChinese.symbol;
        confMaskCover.begin = confMaskPassportChinese.begin;
        confMaskCover.end = confMaskPassportChinese.end;
        confMaskCover.direction = confMaskPassportChinese.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }

    @Override
    public int random(StringBuilder out) {
        List<String> array = new ArrayList<>(8);
        array.add("141687340");
        array.add("G07395462");
        array.add("P4625739");
        array.add("D04652187");
        array.add("S1752875");
        array.add("S07460997");
        array.add("154691274");
        array.add("G04717722");
        Conf.ConfMaskPassportChinese conf = new Conf.ConfMaskPassportChinese();

        conf.seed = Util.getNumByRange(0, 65565);
        conf.type = Util.getNumByRange(0, 1) == 1;
        conf.number = Util.getNumByRange(0, 1) == 1;
        conf.process = Conf.MaskType.MASK;
        this.confMask = conf;

        this.mask(array.get(Util.getNumByRange(0, array.size() - 1)), out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }

        Conf.ConfMaskPassportChinese cf = (Conf.ConfMaskPassportChinese) conf;

        //护照类型
        String inType = in.substring(0,1);
        String outType = out.substring(0,1);
        //护照编号
        String inNumber = in.substring(inType.length());
        String outNumber = out.substring(outType.length());

        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                if (cf.type) {
                    if (!inType.equals(outType)) {
                        return new Object[]{false, "违反保留护照类型策略"};
                    }
                } else {
                    if (inType.equals(outType)) {
                        return new Object[]{false, "违反保留护照类型策略"};
                    }
                }

                if (cf.number) {
                    if (!inNumber.equals(outNumber)) {
                        return new Object[]{false, "违反保留护照编号策略"};
                    }
                } else {
                    if (inNumber.equals(outNumber)) {
                        return new Object[]{false, "违反保留护照编号策略"};
                    }
                }
                break;
            case COVER:
                return validateCover(cf, in, out);
        }
        return new Object[]{true, null};
    }

//    //获取护照类型
//    private String getType(String in) {
//        String unit = in.substring(0, 2);
//        int inPos = prefix2.indexOf(unit);
//        if (inPos == -1) {
//            unit = in.substring(0, 1);
//            inPos = prefix1.indexOf(unit);
//        }
//        if (unit.length() == 1) {
//            int outPos = (inPos + confMask.seed) % prefix1.size();
//            if (outPos == inPos) outPos++;
//            if (outPos == prefix1.size()) outPos = 0;
//            unit = prefix1.get(outPos);
//        } else {
//            int outPos = (inPos + confMask.seed) % prefix2.size();
//            if (outPos == inPos) outPos++;
//            if (outPos == prefix2.size()) outPos = 0;
//            unit = prefix2.get(outPos);
//        }
//        return unit;
//    }
}
