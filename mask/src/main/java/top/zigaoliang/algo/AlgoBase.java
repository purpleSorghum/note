package top.zigaoliang.algo;


import lombok.Data;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;


/**
 * 所有算法的基类
 * Created by byc on 10/20/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({AlgoAddress.class,
            AlgoRelateIdToAge.class, AlgoIntegerRange.class, AlgoRelateIdToBirthday.class, AlgoRelateCompute.class,
            AlgoMixedColumn.class, AlgoChineseName.class,
            AlgoTaxNumber.class, AlgoCreditCode.class, AlgoBankCard.class, AlgoStockCode.class,
            AlgoStockName.class, AlgoCompatriotId.class, AlgoResidence.class, AlgoPassportHKAM.class,
            AlgoPassportChinese.class, AlgoPostalCode.class, AlgoMilitaryCard.class, AlgoFundCode.class,
            AlgoFundName.class, AlgoPlateNumber.class, AlgoFrameNumber.class, AlgoDate.class,
            AlgoFundName.class, AlgoPlateNumber.class, AlgoFrameNumber.class, AlgoMoney.class,
            AlgoIpAddress.class, AlgoMacAddress.class, AlgoInteger.class, AlgoRandomString.class,
            AlgoFixedValue.class, AlgoHashValue.class, /*AlgoRegex.class,*/ AlgoDictionaryRandom.class,
            AlgoDictionaryMap.class, AlgoCompany.class,AlgoEmail.class,AlgoIdCard.class,AlgoTelephone.class,AlgoCellphone.class,
            AlgoPhone.class, AlgoAccount.class, AlgoIntegerRange.class, AlgoCustomerName.class
})
public abstract class AlgoBase {
    protected AlgoId id;//算法类型ID

    public AlgoId getId() {
        return id;
    }

    /**
     * 算法本身的属性
     * find 1       发现
     * random 2     仿真脱敏
     * mask 4       正向脱敏
     * unmask 8     逆向脱敏
     * 如果全部算法都支持，attr等于15
     */
    protected int attr;

    @XmlTransient
    protected Conf.ConfFind confFind;//发现配置
    @XmlTransient
    protected Conf.ConfMask confMask;//脱敏配置

    public AlgoBase() {
    }

    public AlgoBase(AlgoId id) {
        this.id = id;
    }

    /**
     * 初始化算法
     *
     * @param confFind 发现配置
     * @return 成功返回0；失败返回错误码
     */
    public int init(Conf.ConfFind confFind) {
        if (confFind == null) {
            return ErrorCode.CONF_INIT_FIND.getCode();
        }
        this.confFind = confFind;
        return 0;
    }

    /**
     * 初始化算法
     *
     * @param confMask 脱敏配置
     * @return 成功返回0；失败返回错误码
     */
    public int init(Conf.ConfMask confMask) {
        if (confMask == null) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        this.confMask = confMask;
        return 0;
    }

    /**
     * 发现
     *
     * @param in 输入值
     * @return true: 符合算法规则；false: 不符合算法规则。
     */
    public abstract boolean find(String in);

    /**
     * 仿真脱敏
     *
     * @param in  输入值
     * @param out 输出值
     * @return 成功返回0；失败返回错误码
     */
    public abstract int random(String in, StringBuilder out);

    /**
     * 正向脱敏
     *
     * @param in  输入值
     * @param out 输出值
     * @return 成功返回0；失败返回错误码
     */
    public abstract int mask(String in, StringBuilder out);

    /**
     * 逆向脱敏
     *
     * @param in  输入值
     * @param out 输出值
     * @return 成功返回0；失败返回错误码
     */
    public abstract int unmask(String in, StringBuilder out);


    public abstract int cover(String in, StringBuilder out);


    /**
     * 构建仿真数据,用于白盒测试
     *
     * @param out 输出值
     * @return 成功返回0；失败返回错误码
     */
    public int random(StringBuilder out) {
        return 0;
    }


    /**
     * 校验脱敏前后数据正确性
     *
     * @param conf
     * @param in
     * @param out
     * @return Object[] index->0  true/false   index->1   errorInfo
     */
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        return new Object[2];
    }

    /**
     * 通用遮蔽算法结果校验
     *
     * @param cf
     * @param in
     * @param out
     * @return
     */
    public Object[] validateCover(Conf.CoverMask cf, String in, String out) {
        char sym = cf.symbol.charAt(0);
        //从左向右
        if (cf.direction) {
            for (int i = 1, j = 0; i <= out.length() && j < out.length(); i++, j++) {
                if (i >= cf.begin && i <= cf.end) {
                    if (out.charAt(j) != sym) {
                        return new Object[]{false, "违反遮蔽策略"};
                    }
                } else {
                    if (out.charAt(j) != in.charAt(j)) {
                        return new Object[]{false, "违反遮蔽策略"};
                    }
                }
            }
        }
        //从右向左
        else {
            for (int i = 1, j = out.length() - 1; i <= out.length() && j >= 0; i++, j--) {
                if (i >= cf.begin && i <= cf.end) {
                    if (out.charAt(j) != sym) {
                        return new Object[]{false, "违反遮蔽策略"};
                    }
                } else {
                    if (out.charAt(j) != in.charAt(j)) {
                        return new Object[]{false, "违反遮蔽策略"};
                    }
                }
            }
        }
        return new Object[]{true, null};
    }

}
