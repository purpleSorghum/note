package top.zigaoliang.algo;

import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CreditCode;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 统一社会信用代码算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoCreditCode extends AlgoBase {
    public AlgoCreditCode() {
        super(AlgoId.CREDITCODE);
    }

    @Override
    public boolean find(String in) {
        CreditCode creditCode = CreditCode.create(in.trim());
        return creditCode != null && creditCode.isValid();
    }

    @Override
    public int random(String in, StringBuilder out) {
        this.mask(in, out);
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        CreditCode creditCode = CreditCode.create(in);
        if (creditCode == null || !creditCode.isValid()) {
            out.append(in);
        } else {
            Conf.ConfMaskCreditCode conf = (Conf.ConfMaskCreditCode) confMask;
            if (conf.keep == 1) {
                creditCode.maskOrgType(conf.seed);
            }
            if (conf.keep == 2) {
                creditCode.maskOrgCode(conf.seed);
            }
            if (conf.keep == 3) {
                creditCode.maskDivisionCode(conf.seed);
            }
            out.append(creditCode.get());
        }
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        CreditCode creditCode = CreditCode.create(in);
        if (creditCode == null || !creditCode.isValid()) {
            out.append(in);
        } else {
            creditCode.setMask(false);
            Conf.ConfMaskCreditCode conf = (Conf.ConfMaskCreditCode) confMask;
            if (conf.keep == 1) {
                creditCode.maskOrgType(conf.seed);
            }
            if (conf.keep == 2) {
                creditCode.maskOrgCode(conf.seed);
            }
            if (conf.keep == 3) {
                creditCode.maskDivisionCode(conf.seed);
            }
            out.append(creditCode.get());
        }
        return 0;
    }

    public AlgoCover coverPramConf() {
        Conf.ConfMaskCreditCode conf = (Conf.ConfMaskCreditCode) confMask;
        Conf.ConfMaskCover confMaskCover = new Conf.ConfMaskCover();
        confMaskCover.symbol = conf.symbol;
        confMaskCover.begin = conf.begin;
        confMaskCover.end = conf.end;
        confMaskCover.direction = conf.direction;
        AlgoCover algoCover = new AlgoCover();
        algoCover.init(confMaskCover);
        return algoCover;
    }


    @Override
    public int cover(String in, StringBuilder out) {
        return coverPramConf().cover(in, out);
    }


    @Override
    public int random(StringBuilder out) {
        String in = "911101086876249193";
        CreditCode creditCode = CreditCode.create(in);
        if (creditCode == null || !creditCode.isValid()) {
            out.append(in);
        } else {
            int keep = Util.getNumByRange(1, 3);
            int seed = Util.getNumByRange(0, 65565);
            if (keep == 1) {
                creditCode.maskOrgType(seed);
            }
            if (keep == 2) {
                creditCode.maskOrgCode(seed);
            }
            if (keep == 3) {
                creditCode.maskDivisionCode(seed);
            }
            out.append(creditCode.get());
        }
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        CreditCode inCreditCode = CreditCode.create(in);
        CreditCode outCreditCode = CreditCode.create(out);
        if (inCreditCode == null || outCreditCode == null) {
            return new Object[]{false, "in or out data is invalid credit code"};
        }

        Conf.ConfMaskCreditCode cf = (Conf.ConfMaskCreditCode) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                switch (cf.getKeep()) {
                    //1: 保留企业类型
                    case 1:
                        if (!String.valueOf(inCreditCode.getOrgType()).equals(String.valueOf(outCreditCode.getOrgType()))
                                && String.valueOf(inCreditCode.getOrgCode()).equals(String.valueOf(outCreditCode.getOrgCode()))
                                && String.valueOf(inCreditCode.getDivisionCode()).equals(String.valueOf(outCreditCode.getDivisionCode()))
                        ) {

                            return new Object[]{false, "违反保留企业类型策略"};
                        }
                        break;
                    //2: 保留地区
                    case 2:
                        if (String.valueOf(inCreditCode.getOrgType()).equals(String.valueOf(outCreditCode.getOrgType()))
                                && String.valueOf(inCreditCode.getOrgCode()).equals(String.valueOf(outCreditCode.getOrgCode()))
                                && !String.valueOf(inCreditCode.getDivisionCode()).equals(String.valueOf(outCreditCode.getDivisionCode()))
                        ) {
                            return new Object[]{false, "违反保留地区策略"};
                        }
                        break;
                    //3: 保留机构类别代码
                    case 3:
                        if (String.valueOf(inCreditCode.getOrgType()).equals(String.valueOf(outCreditCode.getOrgType()))
                                && !String.valueOf(inCreditCode.getOrgCode()).equals(String.valueOf(outCreditCode.getOrgCode()))
                                && String.valueOf(inCreditCode.getDivisionCode()).equals(String.valueOf(outCreditCode.getDivisionCode()))
                        ) {
                            return new Object[]{false, "违反保留机构类别代码策略"};
                        }
                        break;
                }
                break;
            case COVER:
                return validateCover(cf, in, out);

        }
        return new Object[]{true, null};
    }
}
