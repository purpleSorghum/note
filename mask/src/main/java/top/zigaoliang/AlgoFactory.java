package top.zigaoliang;


import top.zigaoliang.algo.*;

import com.alibaba.fastjson.JSON;

import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;

/**
 * 算法工厂
 * 生成算法类对象
 *
 * @author byc
 * @date 10/20/18
 */
public class AlgoFactory {
    public static AlgoBase getAlgo(AlgoId algoId) {
        AlgoBase algoBase = null;
        switch (algoId) {
            case EMAIL:
                algoBase = new AlgoEmail();
                break;
            case ADDRESS:
                algoBase = new AlgoAddress();
                break;
            case COMPANY:
                algoBase = new AlgoCompany();
                break;
            case UNITNAME:
                algoBase = new AlgoUnitName();
                break;
            case CHINESENAME:
                algoBase = new AlgoChineseName();
                break;
            case CELLPHONE:
                algoBase = new AlgoCellphone();
                break;
            case TELEPHONE:
                algoBase = new AlgoTelephone();
                break;
            case PHONE:
                algoBase = new AlgoPhone();
                break;
            case DATE:
                algoBase = new AlgoDate();
                break;
            case TAXNUMBER:
                algoBase = new AlgoTaxNumber();
                break;
            case CREDITCODE:
                algoBase = new AlgoCreditCode();
                break;
            case IDCARD:
                algoBase = new AlgoIdCard();
                break;
            case MONEY:
                algoBase = new AlgoMoney();
                break;
            case BANKCARD:
                algoBase = new AlgoBankCard();
                break;
            case STOCKCODE:
                algoBase = new AlgoStockCode();
                break;
            case STOCKNAME:
                algoBase = new AlgoStockName();
                break;
            case COMPATRIOTID:
                algoBase = new AlgoCompatriotId();
                break;
            case RESIDENCE:
                algoBase = new AlgoResidence();
                break;
            case PASSPORTHKAM:
                algoBase = new AlgoPassportHKAM();
                break;
            case PASSPORTCHINESE:
                algoBase = new AlgoPassportChinese();
                break;
            case POSTALCODE:
                algoBase = new AlgoPostalCode();
                break;
            case MILITARYCARD:
                algoBase = new AlgoMilitaryCard();
                break;
            case FUNDCODE:
                algoBase = new AlgoFundCode();
                break;
            case FUNDNAME:
                algoBase = new AlgoFundName();
                break;
            case ACCOUNT:
                algoBase = new AlgoAccount();
                break;
            case IPADDRESS:
                algoBase = new AlgoIpAddress();
                break;
            case MACADDRESS:
                algoBase = new AlgoMacAddress();
                break;
            case PLATENUMBER:
                algoBase = new AlgoPlateNumber();
                break;
            case FRAMENUMBER:
                algoBase = new AlgoFrameNumber();
                break;
            case CUSTOMERNAME:
                algoBase = new AlgoCustomerName();
                break;
            case INTEGER:
                algoBase = new AlgoInteger();
                break;
//            case WATERMARK:
//                algoBase = new AlgoWaterMark();
//                break;
            case COVER:
                algoBase = new AlgoCover();
                break;
            case RANDOMSTRING:
                algoBase = new AlgoRandomString();
                break;
            case FIXEDVALUE:
                algoBase = new AlgoFixedValue();
                break;
            case HASHVALUE:
                algoBase = new AlgoHashValue();
                break;
            case REGEX:
                algoBase = new AlgoRegex();
                break;
            case DICTIONARY:
                algoBase = new AlgoDictionary();
                break;
            case DICTIONARYMAP:
                algoBase = new AlgoDictionaryMap();
                break;
            case DICTIONARYRANDOM:
                algoBase = new AlgoDictionaryRandom();
                break;
            case INTEGERRANGE:
                algoBase = new AlgoIntegerRange();
                break;
            case RELATEIDTOAGE:
                algoBase = new AlgoRelateIdToAge();
                break;
            case RELATEIDTOBIRTHDAY:
                algoBase = new AlgoRelateIdToBirthday();
                break;
            case RELATECOMPUTE:
                algoBase = new AlgoRelateCompute();
                break;
            case RELATEVERTICAL:
            case RELATEVERTICALMAIN:
            case RELATEVERTICALGROUP:
                algoBase = new AlgoRelateVertical();
                break;
            case MIXEDCOLUMN:
                algoBase = new AlgoMixedColumn();
                break;
            case CUSTOM:
                algoBase = new AlgoCustom();
                break;
            case NUMBERROUND:
                algoBase = new AlgoNumberRound();
            default:
                break;
        }
        return algoBase;
    }

    public static Conf.ConfMask getConfMask(AlgoId algoId, String conf) {
        switch (algoId) {
            case EMAIL:
                return JSON.parseObject(conf, Conf.ConfMaskEmail.class);
            case ADDRESS:
                return JSON.parseObject(conf, Conf.ConfMaskAddress.class);
            case COMPANY:
                return JSON.parseObject(conf, Conf.ConfMaskCompany.class);
            case UNITNAME:
                return JSON.parseObject(conf, Conf.ConfMaskUnitName.class);
            case CHINESENAME:
                return JSON.parseObject(conf, Conf.ConfMaskChineseName.class);
            case CELLPHONE:
                return JSON.parseObject(conf, Conf.ConfMaskCellphone.class);
            case TELEPHONE:
                return JSON.parseObject(conf, Conf.ConfMaskTelephone.class);
            case PHONE:
                return JSON.parseObject(conf, Conf.ConfMaskPhone.class);
            case DATE:
                return JSON.parseObject(conf, Conf.ConfMaskDate.class);
            case TAXNUMBER:
                return JSON.parseObject(conf, Conf.ConfMaskTaxNumber.class);
            case CREDITCODE:
                return JSON.parseObject(conf, Conf.ConfMaskCreditCode.class);
            case IDCARD:
                return JSON.parseObject(conf, Conf.ConfMaskIdCard.class);
            case MONEY:
                return JSON.parseObject(conf, Conf.ConfMaskMoney.class);
            case BANKCARD:
                return JSON.parseObject(conf, Conf.ConfMaskBankCard.class);
            case STOCKCODE:
                return JSON.parseObject(conf, Conf.ConfMaskStockCode.class);
            case STOCKNAME:
                return JSON.parseObject(conf, Conf.ConfMaskStockName.class);
            case COMPATRIOTID:
                return JSON.parseObject(conf, Conf.ConfMaskCompatriotId.class);
            case RESIDENCE:
                return JSON.parseObject(conf, Conf.ConfMaskResidence.class);
            case PASSPORTHKAM:
                return JSON.parseObject(conf, Conf.ConfMaskPassportHKAM.class);
            case PASSPORTCHINESE:
                return JSON.parseObject(conf, Conf.ConfMaskPassportChinese.class);
            case POSTALCODE:
                return JSON.parseObject(conf, Conf.ConfMaskPostalCode.class);
            case MILITARYCARD:
                return JSON.parseObject(conf, Conf.ConfMaskMilitaryCard.class);
            case FUNDCODE:
                return JSON.parseObject(conf, Conf.ConfMaskFundCode.class);
            case FUNDNAME:
                return JSON.parseObject(conf, Conf.ConfMaskFundName.class);
            case ACCOUNT:
                return JSON.parseObject(conf, Conf.ConfMaskAccount.class);
            case IPADDRESS:
                return JSON.parseObject(conf, Conf.ConfMaskIpAddress.class);
            case MACADDRESS:
                return JSON.parseObject(conf, Conf.ConfMaskMacAddress.class);
            case PLATENUMBER:
                return JSON.parseObject(conf, Conf.ConfMaskPlateNumber.class);
            case FRAMENUMBER:
                return JSON.parseObject(conf, Conf.ConfMaskFrameNumber.class);
            case CUSTOMERNAME:
                return JSON.parseObject(conf, Conf.ConfMaskCustomerName.class);
            case INTEGER:
                return JSON.parseObject(conf, Conf.ConfMaskInteger.class);
            case WATERMARK:
                return JSON.parseObject(conf, Conf.ConfMaskWaterMark.class);
            case COVER:
                return JSON.parseObject(conf, Conf.ConfMaskCover.class);
            case RANDOMSTRING:
                return JSON.parseObject(conf, Conf.ConfMaskRandomString.class);
            case FIXEDVALUE:
                return JSON.parseObject(conf, Conf.ConfMaskFixedValue.class);
            case HASHVALUE:
                return JSON.parseObject(conf, Conf.ConfMaskHashValue.class);
            case REGEX:
                return JSON.parseObject(conf, Conf.ConfMaskRegex.class);
            case DICTIONARY:
                return JSON.parseObject(conf, Conf.ConfMaskDictionary.class);
            case DICTIONARYMAP:
                return JSON.parseObject(conf, Conf.ConfMaskDictionaryMap.class);
            case DICTIONARYRANDOM:
                return JSON.parseObject(conf, Conf.ConfMaskDictionaryRandom.class);
            case INTEGERRANGE:
                return JSON.parseObject(conf, Conf.ConfMaskIntegerRange.class);
            case NUMBERROUND:
                return JSON.parseObject(conf, Conf.ConfMaskNumberRound.class);
            case RELATEIDTOAGE:
                return JSON.parseObject(conf, Conf.ConfMaskRelateIdToAge.class);
            case RELATEIDTOBIRTHDAY:
                return JSON.parseObject(conf, Conf.ConfMaskRelateIdToBirthday.class);
            case RELATECOMPUTE:
                return JSON.parseObject(conf, Conf.ConfMaskRelateCompute.class);
            case RELATEVERTICALGROUP:
            case RELATEVERTICAL:
            case RELATEVERTICALMAIN:
                return JSON.parseObject(conf, Conf.ConfMaskRelateVertical.class);
            case MIXEDCOLUMN:
                return JSON.parseObject(conf, Conf.ConfMaskMixedColumn.class);
            case CUSTOM:
                return JSON.parseObject(conf, Conf.ConfMaskCustom.class);
            case UNKOWN:
                return JSON.parseObject(conf, Conf.ConfMask.class);
            default:
                throw new RuntimeException("暂时不支持的脱敏配置");
        }
    }

    public static Conf.ConfMask getDefaultConfMask(AlgoId algoId) {
        switch (algoId) {
            case EMAIL:
                return new Conf.ConfMaskEmail();
            case ADDRESS:
                return new Conf.ConfMaskAddress();
            case COMPANY:
                return new Conf.ConfMaskCompany();
            case UNITNAME:
                return new Conf.ConfMaskUnitName();
            case CHINESENAME:
                return new Conf.ConfMaskChineseName();
            case CELLPHONE:
                return new Conf.ConfMaskCellphone();
            case TELEPHONE:
                return new Conf.ConfMaskTelephone();
            case PHONE:
                return new Conf.ConfMaskPhone();
            case DATE:
                return new Conf.ConfMaskDate();
            case TAXNUMBER:
                return new Conf.ConfMaskTaxNumber();
            case CREDITCODE:
                return new Conf.ConfMaskCreditCode();
            case IDCARD:
                return new Conf.ConfMaskIdCard();
            case MONEY:
                return new Conf.ConfMaskMoney();
            case BANKCARD:
                return new Conf.ConfMaskBankCard();
            case STOCKCODE:
                return new Conf.ConfMaskStockCode();
            case STOCKNAME:
                return new Conf.ConfMaskStockName();
            case COMPATRIOTID:
                return new Conf.ConfMaskCompatriotId();
            case RESIDENCE:
                return new Conf.ConfMaskResidence();
            case PASSPORTHKAM:
                return new Conf.ConfMaskPassportHKAM();
            case PASSPORTCHINESE:
                return new Conf.ConfMaskPassportChinese();
            case POSTALCODE:
                return new Conf.ConfMaskPostalCode();
            case MILITARYCARD:
                return new Conf.ConfMaskMilitaryCard();
            case FUNDCODE:
                return new Conf.ConfMaskFundCode();
            case FUNDNAME:
                return new Conf.ConfMaskFundName();
            case ACCOUNT:
                return new Conf.ConfMaskAccount();
            case IPADDRESS:
                return new Conf.ConfMaskIpAddress();
            case MACADDRESS:
                return new Conf.ConfMaskMacAddress();
            case PLATENUMBER:
                return new Conf.ConfMaskPlateNumber();
            case FRAMENUMBER:
                return new Conf.ConfMaskFrameNumber();
            case CUSTOMERNAME:
                return new Conf.ConfMaskCustomerName();
            case INTEGER:
                return new Conf.ConfMaskInteger();
//            case WATERMARK:
//                return new Conf.ConfMaskWaterMark();
            case COVER:
                return new Conf.ConfMaskCover();
            case RANDOMSTRING:
                return new Conf.ConfMaskRandomString();
            case FIXEDVALUE:
                return new Conf.ConfMaskFixedValue();
            case HASHVALUE:
                return new Conf.ConfMaskHashValue();
            case REGEX:
                return new Conf.ConfMaskRegex();
            case DICTIONARYMAP:
                return new Conf.ConfMaskDictionaryMap();
            case DICTIONARYRANDOM:
                return new Conf.ConfMaskDictionaryRandom();
            case INTEGERRANGE:
                return new Conf.ConfMaskIntegerRange();
            case RELATEIDTOAGE:
                return new Conf.ConfMaskRelateIdToAge();
            case RELATEIDTOBIRTHDAY:
                return new Conf.ConfMaskRelateIdToBirthday();
            case RELATECOMPUTE:
                return new Conf.ConfMaskRelateCompute();
            case RELATEVERTICAL:
            case RELATEVERTICALMAIN:
                return new Conf.ConfMaskRelateVertical();
            case MIXEDCOLUMN:
                return new Conf.ConfMaskMixedColumn();
            case CUSTOM:
                return new Conf.ConfMaskCustom();
            default:
                return null;
        }
    }

}
