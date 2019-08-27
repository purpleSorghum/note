package top.zigaoliang.conf;



import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import top.zigaoliang.algo.AlgoBase;
import top.zigaoliang.core.AlgoId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置类定义
 * 用于定义所有的配置
 *
 * @author byc
 * @date 10/22/18
 */
public class Conf {

    public enum MaskType {
        RANDOM, MASK, UNMASK, COVER;

        public static MaskType getMaskType(int id) {
            for (MaskType maskType : values()) {
                if (maskType.ordinal() == id) {
                    return maskType;
                }
            }
            return null;
        }
    }

    /**
     * 脱敏配置基类
     * 每个脱敏算法都继承这个基类
     * Created by byc on 10/22/18.
     */
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlSeeAlso({ConfMaskEmail.class, ConfMaskAddress.class, ConfMaskCompany.class, ConfMaskUnitName.class, ConfMaskChineseName.class, ConfMaskCellphone.class, ConfMaskTelephone.class
                , ConfMaskPhone.class, ConfMaskDate.class, ConfMaskTaxNumber.class, ConfMaskCreditCode.class, ConfMaskIdCard.class, ConfMaskMoney.class, ConfMaskBankCard.class
                , ConfMaskStockCode.class, ConfMaskStockName.class, ConfMaskCompatriotId.class, ConfMaskResidence.class, ConfMaskPassportHKAM.class, ConfMaskPassportChinese.class
                , ConfMaskPostalCode.class, ConfMaskMilitaryCard.class, ConfMaskFundCode.class, ConfMaskFundName.class, ConfMaskAccount.class, ConfMaskIpAddress.class
                , ConfMaskMacAddress.class, ConfMaskPlateNumber.class, ConfMaskFrameNumber.class, ConfMaskCustomerName.class, ConfMaskInteger.class, ConfMaskIntegerRange.class
                , ConfMaskCover.class, ConfMaskRandomString.class, ConfMaskDictionaryMap.class, ConfMaskDictionaryRandom.class,ConfMaskHashValue.class, ConfMaskCompany.class
                ,ConfMaskIdCard.class, ConfMaskEmail.class, ConfMaskCellphone.class, ConfMaskTelephone.class, ConfMaskPhone.class
                , ConfMaskAccount.class, ConfMaskIntegerRange.class, ConfMaskCustomerName.class,ConfMaskRelateVertical.class
                , ConfMaskRelateIdToAge.class, ConfMaskRelateIdToBirthday.class, ConfMaskRelateCompute.class, ConfMaskMixedColumn.class})

    public static class ConfMask extends CoverMask {
        public AlgoId id;//算法类型ID
        public MaskType process;//脱敏方式：仿真；可逆正向；可逆逆向。
        public int seed;//种子


        public ConfMask(AlgoId id) {
            this.id = id;
        }

        public ConfMask() {
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        /**
         * 获取脱敏配置或策略
         *
         * @return
         */
        public String getMaskRule() {
            return toString();
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "coverMask")
    @XmlType(propOrder = {"symbol", "direction", "begin", "end"})
    public static class CoverMask {
        @XmlElement
        public String symbol = "*";
        public boolean direction;//方向：true: 从左向右  false: 从右向左
        public int begin;//开始位置
        public int end;//结束位置
    }


    //1. 电子邮箱
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskEmail extends ConfMask {
        public ConfMaskEmail() {
            super(AlgoId.EMAIL);
        }

        public boolean reservePart;//保留部分 true保留前缀 false保留后缀
        public boolean coverType;   //true
//        public String symbol;
    }

    //2. 中文地址
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskAddress extends ConfMask {
        public ConfMaskAddress() {
            super(AlgoId.ADDRESS);
        }

        public boolean province;//保留省
        public boolean coverProvice;
        public boolean city;//保留地市（州、盟）
        public boolean coverCity;
        public boolean county;//保留区县（旗、林区）
        public boolean coverCounty;
        public boolean town;//保留乡镇（街道）
        public boolean coverTown;
        public boolean village;//保留村
        public boolean coverVillage;
        public boolean street;//门牌号（小区、门牌号）
        public boolean coverStreet;
        public boolean suffix;//只脱敏后半段的详细家庭住址
        public String symbol;

        @Override
        public String getMaskRule() {
            if (super.process != MaskType.COVER) {
                return new StringBuilder("==province:").append(province).
                            append("===city:").append(city).append("==county:").append(county).append("==town:").append(town)
                            .append("===village:").append(village).append("===street:").append(street).toString();
            }
            return new StringBuilder("==coverProvice:").append(coverProvice).
                        append("===coverCity:").append(coverCity).append("==coverCounty:").append(coverCounty).append("==coverTown:").append(coverTown)
                        .append("===coverVillage:").append(coverVillage).append("===coverStreet:").append(coverStreet).toString();
        }
    }

    //3. 公司名称
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCompany extends ConfMask {
        public ConfMaskCompany() {
            super(AlgoId.COMPANY);
        }

        public boolean address;//保留公司所在地信息（不改变有限公司字样）
//        public boolean coverType;  //true 遮蔽公司地址，false遮蔽公司名称
    }

    //4. 单位名称
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskUnitName extends ConfMask {
        public ConfMaskUnitName() {
            super(AlgoId.UNITNAME);
        }

        public boolean address;//保留公司所在地信息（不改变有限公司字样）
        public boolean coverType;  //true  遮蔽单位名称中的地址   false遮蔽单位名称中的名字
    }

    //5. 中文姓名
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskChineseName extends ConfMask {
        public ConfMaskChineseName() {
            super(AlgoId.CHINESENAME);
        }

        public boolean firstName;//true 保留姓  false 保留名
        public boolean length;//保持字数
        public int coverType = 2;  //1 遮蔽姓 2 遮蔽名 3 遮蔽中间字 4 保留中间字
        public String symbol="*";//默认使用*号遮蔽

        @Override
        public String getMaskRule() {
            if (super.process != MaskType.COVER) {
                return "===保留姓/名:" + firstName + "===保持length:" + length;
            }
            switch (coverType) {
                case 1:
                    return "===coverType:遮蔽姓";
                case 2:
                    return "===coverType:遮蔽名";
                case 3:
                    return "===coverType:遮蔽中间字";
                case 4:
                    return "===coverType:保留中间字";
            }
            return "";
        }
    }

    //6. 手机号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCellphone extends ConfMask {
        public ConfMaskCellphone() {
            super(AlgoId.CELLPHONE);
        }

        public int keep = 1;// 1: 保留前三位（不包括86+） 2: 保留中间四位 3: 保留最后四位
        public int coverType =2; //1: 前三位（不包括86+） 2: 中间四位 3: 最后四位
        //public String symbol;
    }

    //7. 座机号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskTelephone extends ConfMask {
        public ConfMaskTelephone() {
            super(AlgoId.TELEPHONE);
        }

        public boolean keep;// true: 保留区号  false: 保留前四位
        public int coverType;
        //public String symbol;
    }

    //8. 电话号码
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskPhone extends ConfMask {
        public ConfMaskPhone() {
            super(AlgoId.PHONE);
        }

        public boolean keepMiddle;  // 可逆的时候只有这个参数
        public boolean keepLast;   //保留最后四位
    }

    //9. 日期
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskDate extends ConfMask {
        public ConfMaskDate() {
            super(AlgoId.DATE);
        }

        public boolean year;//保留年
        public boolean month;//保留月
        public boolean day;//保留日
        public String rangeyear;//年变化范围
        public String rangeday; //日变化范围

        //梳理那里传给的日期范围(必须带上时分秒)，使用固定的标准格式的字符串串日期
        //如dateTime类型：0000-01-01 9999-12-31
        public String dateMin = "0000-01-01 00:00:00";  //默认值
        public String dateMax = "9999-12-31 23:59:59";
    }

    //10. 税号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskTaxNumber extends ConfMask {
        public ConfMaskTaxNumber() {
            super(AlgoId.TAXNUMBER);
        }

        public boolean region;//保留企业注册地行政区划码

        public boolean isRegion(){
            return region;
        }
    }

    //11. 统一社会信用代码
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCreditCode extends ConfMask {
        public ConfMaskCreditCode() {
            super(AlgoId.CREDITCODE);
        }

//        public boolean enterprise;//保留企业类型
//        public boolean domain;//保留地区
//        public boolean organization;//保留机构类别代码

        public int keep; // 1: 保留企业类型 2: 保留地区 3: 保留机构类别代码

        public int getKeep(){
            return keep;
        }
    }

    //12. 身份证
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskIdCard extends ConfMask {
        public ConfMaskIdCard() {
            super(AlgoId.IDCARD);
        }

        public boolean province;//保留省
        public boolean city;//保留市
        public boolean county;//保留区县
        public boolean birthday;//保留生日
        public boolean sex;//保留性别
        public boolean check;//保留校验位

        public boolean coverProvice;
        public boolean coverCity;
        public boolean coverCounty;
        public boolean coverYearAndMonth;
        public boolean coverCheck;
//        public String symbol;
    }

    //13. 金额数字
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskMoney extends ConfMask {
        public ConfMaskMoney() {
            super(AlgoId.MONEY);
        }

        public boolean number;  //true 保留整数数数  false 保留小数数位
        public boolean decimal; //保留小数位位数(保留精度)
        public int max = 1000;  //最大值   (可逆脱敏的时候不带最大值最小值参数)
        public int min = 0;     //最小值

        public boolean coverType;   //true 遮蔽整数位  false  遮蔽小数位
    }

    //14. 银行卡号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskBankCard extends ConfMask {
        public ConfMaskBankCard() {
            super(AlgoId.BANKCARD);
        }

        public boolean bank;// 保留发卡行代码
        public boolean coverType;  //true 遮蔽发卡行代码 false 遮蔽发卡行代码到校验位之间的部分
        public boolean check;  // 保留校验码
//        public String symbol;
        public boolean isBank(){
            return bank;
        }

        public boolean isCoverType(){
            return coverType;
        }
    }

    //15. 证券代码
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskStockCode extends ConfMask {
        public ConfMaskStockCode() {
            super(AlgoId.STOCKCODE);
        }

        public boolean prefix;//true 保留前三位 false 不保留前三位
    }

    //16. 证券名称
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskStockName extends ConfMask {
        public ConfMaskStockName() {
            super(AlgoId.STOCKNAME);
        }
    }

    //17. 台胞证
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCompatriotId extends ConfMask {
        public ConfMaskCompatriotId() {
            super(AlgoId.COMPATRIOTID);
        }
    }

    //18. 永久居住证
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskResidence extends ConfMask {
        public ConfMaskResidence() {
            super(AlgoId.RESIDENCE);
        }

        public boolean nation;//保留国籍
        public boolean birthday;//保留出生日期
        public boolean check;//保留校验位（暂时无校验位生成的相关资料）
    }

    //19. 港澳通行证
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskPassportHKAM extends ConfMask {
        public ConfMaskPassportHKAM() {
            super(AlgoId.PASSPORTHKAM);
        }

        //        public boolean domain;//保留持证人地区
//        public boolean number;//保留持证人终身号码
        public boolean handLetter; //保留通行证上的字母
        public boolean number;//保留通行证上前4个数字

    }

    //20. 中国护照
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskPassportChinese extends ConfMask {
        public ConfMaskPassportChinese() {
            super(AlgoId.PASSPORTCHINESE);
        }

        public boolean type;//保留护照类型
        public boolean number;//保留护照编号
    }

    //21. 邮政编码{"province": false, "domain": false, "city": false, "region": false}
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskPostalCode extends ConfMask {
        public ConfMaskPostalCode() {
            super(AlgoId.POSTALCODE);
        }

        public boolean province;//保留省
        public boolean domain;//保留邮区
        public boolean city;//保留市
        public boolean region;//保留投地区

        public int coverType;   //1：遮蔽省 2：遮蔽邮区 3：遮蔽市 4:遮蔽投递区
//        public String symbol;
    }


    //22. 军官证{"unit": false, "number": false}
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskMilitaryCard extends ConfMask {
        public ConfMaskMilitaryCard() {
            super(AlgoId.MILITARYCARD);
        }

        public boolean unit;//true: 保留单位简称
        public boolean number;//true: 保留编号

        public boolean coverType;   //true 遮蔽单位简称  false 遮蔽编号
//        public String symbol;
    }

    //23. 基金代码{}
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskFundCode extends ConfMask {
        public ConfMaskFundCode() {
            super(AlgoId.FUNDCODE);
        }
    }

    //24. 基金名称{}
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskFundName extends ConfMask {
        public ConfMaskFundName() {
            super(AlgoId.FUNDNAME);
        }
    }

    //25. 开户账号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskAccount extends ConfMask {
        public ConfMaskAccount() {
            super(AlgoId.ACCOUNT);
        }
    }

    //26. IP地址
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskIpAddress extends ConfMask {
        public ConfMaskIpAddress() {
            super(AlgoId.IPADDRESS);
        }

        public boolean prefix;//保留前3位数字
        public boolean suffix;//保留后3位数字
        public int coverType;  // 1 遮蔽前三位，2 遮蔽后三位  3，遮蔽前6位  4，遮蔽后6位
//        public String symbol;
    }

    //27. MAC地址
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskMacAddress extends ConfMask {
        public ConfMaskMacAddress() {
            super(AlgoId.MACADDRESS);
        }

        public boolean prefix;//保留前四位
        public boolean suffix;//保留后四位
        public int coverType;  //1 遮蔽前四位  2 遮蔽中间四位 3 遮蔽后四位
        //public String symbol;

    }

    //28. 车牌号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskPlateNumber extends ConfMask {
        public ConfMaskPlateNumber() {
            super(AlgoId.PLATENUMBER);
        }

        public boolean province;//保留省
        public boolean region;//保留地区
        public boolean number;//保留数字

        public int coverType;   //1 遮蔽省 2 遮蔽地区 3 遮蔽数字
//        public String symbol;
    }

    //29 车架号
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskFrameNumber extends ConfMask {
        public ConfMaskFrameNumber() {
            super(AlgoId.FRAMENUMBER);
        }

        public boolean country;//保留国别
        public boolean factory;//保留制造厂
        public boolean type;//保留汽车类型
        public int coverType; //1.遮蔽国别  2.遮蔽制造厂 3.遮蔽汽车类型
//        public String symbol;

    }

    //30. 客户名称{"address": false, "firstName": false, "secondName": false}
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCustomerName extends ConfMask {
        public ConfMaskCustomerName() {
            super(AlgoId.CUSTOMERNAME);
        }

        public boolean address;//公司名称（是否保留注册地区信息）
        //        public boolean brack;//公司名称（是否保留括号内信息）
        public boolean firstName;//中文姓名（保留姓） true 保留姓  false保留名
        //        public boolean secondName;//中文姓名（保留名）
        public boolean coverType; //true 遮蔽公司名称  false  遮蔽地区信息
        public boolean coverName; //true 遮蔽姓  false  遮蔽名
        //public String symbol;
    }

    //31. 整数
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskInteger extends ConfMask {
        public ConfMaskInteger() {
            super(AlgoId.INTEGER);
        }

        //        public boolean number;//保留整数位位数 (只有在用户选择否的时候 才让填写最大值最小值)
        private boolean keepLength = true;
        public long min = 0;//最小值
        public long max = 127;//最大值
        public int coverType;   //1 遮蔽前两位 2 遮蔽中间两位 3 遮蔽后两位
        public String symbol;
        public boolean isKeepLength(){
            return keepLength;
        }
    }

    //32. 整数区间
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskIntegerRange extends ConfMask {
        public ConfMaskIntegerRange() {
            super(AlgoId.INTEGERRANGE);
        }

        public int begin;  //起始值
        public int step;   //步长
        public int max;    //相对最大值
        public int index;  //列索引
    }

    //33. 遮蔽
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCover extends ConfMask {
        public ConfMaskCover() {
            super(AlgoId.COVER);
        }

        public int relateId;//关联算法ID，电子邮箱和日期单独处理
        public boolean place; //true  邮箱@符号，金额.之前的n位遮蔽 false 邮箱@符号，金额.之后的n位遮蔽
        public int dateCoverType = 1; //日期的遮蔽类型（1保留年 2保留年月 3保留年月日 4保留年月日时 5保留年月时分）

        public boolean direction;//方向：true: 从左向右  false: 从右向左
        public int begin;//开始位置
        public int end;//结束位置
        public String symbol;//符号：*，#，0
    }

    /**
     * 随机字符串算法处理类型
     */
    public enum RandomType {
        STRINGREPLACE(1),//字符串替换--全部替换成随机仿真文字、字母、数字，格式与脱敏前一致
        STRINGRANDOM(2),//字符串随机--全部随机成随机仿真文字、字母、数字，格式与脱敏前一致
        ALPHANUMREPLACE(3),//字母数字组合替换--映射替换字母数字，可选全部替换，只替换字母，只替换数字
        ALPHANUMTYPE(4),//字母数字组合按类型随机--所有字符进行随机替换，保持大写、小写及数字的位置不变
        ALPHANUMRANDOM(5),//字母数字组合随机--所有字符进行随机替换
        ALPHAREPLACE(6),//字母组合替换--映射替换字母
        ALPHARANDOM(7),//字母组合随机--所有字母进行随机替换
        STRNUMREPLACE(8),//数字字符串替换--映射替换数字
        STRNUMRANDOM(9),//数字字符串随机--所有数字进行随机替换
        CHINESEREPLACE(10),//中文替换--映射替换中文
        CHINESERANDOM(11)//中文随机--所有中文进行随机替换
        ;

        private final int type;

        private RandomType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static RandomType getRandomType(int type) {
            for (RandomType t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
    }

    //34. 随机字符串
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRandomString extends ConfMask {
        public ConfMaskRandomString() {
            super(AlgoId.RANDOMSTRING);
        }

        //        public RandomType type;//算法处理类型
//        public int begin;//开始位置
//        public int end;//结束位置
//        public boolean all;//全部脱敏.
        public boolean number;//脱敏数字
        public boolean alpha;//脱敏字母
        public boolean chinese;//脱敏中文
    }

    //35. 固定值算法
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskFixedValue extends ConfMask {
        public ConfMaskFixedValue() {
            super(AlgoId.FIXEDVALUE);
        }

        public String value;//固定值

        public String getValue(){
            return value;
        }
    }

    public enum HashType {
        HASH(0),//hash算法(hash(源数据))
        HASHSALT(1),//hash算法(hash(源数据+盐值))
        HASHSALT2(2)//hash算法(hash(hash(源数据)+hash(盐值)))
        ;
        private final int type;

        private HashType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static HashType getHashType(int type) {
            for (HashType t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
    }

    //36. HASH算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskHashValue extends ConfMask {
        public ConfMaskHashValue() {
            super(AlgoId.HASHVALUE);
        }

        public HashType type;//算法处理类型 (0:hash(源数据), 1:hash(源数据+盐值) 2:hash(hash(源数据)+hash(盐值)))
        public boolean crc32;//true: 32位; false: 16位
        public String saltValue;//盐值
//        public String encryptType;  //哪种加密  //目前 只使用MD5加密
    }

    //44. 正则表达式
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRegex extends ConfMask {
        public ConfMaskRegex() {
            super(AlgoId.REGEX);
        }

        public String regex;//正则表达式
        public boolean fuzzy;//true: 模糊匹配；false: 精确匹配
    }

    //45. 映射字典
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskDictionary extends ConfMask {
        public ConfMaskDictionary() {
            super(AlgoId.DICTIONARY);
        }

        public String path;//特征字典
    }

    //46. 映射字典
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskDictionaryMap extends ConfMask {
        public ConfMaskDictionaryMap() {
            super(AlgoId.DICTIONARYMAP);
        }

        public String path;//映射字典ID
    }

    //47. 随机字典
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskDictionaryRandom extends ConfMask {
        public ConfMaskDictionaryRandom() {
            super(AlgoId.DICTIONARYRANDOM);
        }

        public String path;//随机字典ID
    }

    public enum IntRangeType {
        RANGE(1),//数值区间
        ROUNDING(2)//数值取整
        ;
        private final int type;

        private IntRangeType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static IntRangeType getIntRangeType(int type) {
            for (IntRangeType t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
    }

    //37. 身份证取年龄
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRelateIdToAge extends ConfMask {
        public ConfMaskRelateIdToAge() {
            super(AlgoId.RELATEIDTOAGE);
        }

        public int index;//身份证所在列索引
    }

    //38. 身份证取生日
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRelateIdToBirthday extends ConfMask {
        public ConfMaskRelateIdToBirthday() {
            super(AlgoId.RELATEIDTOBIRTHDAY);
        }

        public int index;//身份证所在列索引
    }

    //39. 计算关联算法
    //C=A+B
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRelateCompute extends ConfMask {
        public ConfMaskRelateCompute() {
            super(AlgoId.RELATECOMPUTE);
        }

        public List<Integer> columns;//列索引
//        public String symbol;  //计算方式：0: 加; 1:减; 2:乘; 3:除

        public int decimal = 2;  //如果关联计算列结果列是整数，返回0。否则浮点数的精度
        public String min = "0";      //关联列的结果列字段类型范围
        public String max = "1000";

    }

    //40. 纵向列关联算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskRelateVertical extends ConfMask {
        public ConfMaskRelateVertical() {
            super(AlgoId.RELATEVERTICAL);
        }

        public List<Integer> columns = new ArrayList<Integer>();//和纵向列关联的列，需要把当前列写入这个数组
    }

    //43. 混合列算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskMixedColumn extends ConfMask {
        public ConfMaskMixedColumn() {
            super(AlgoId.MIXEDCOLUMN);
        }

        public MixedColumn cfg;
    }

    //48. 自定义算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskCustom extends ConfMask {
        public ConfMaskCustom() {
            super(AlgoId.CUSTOM);
        }

        public String function;//函数字符串；或JS脚本目录及入口函数名json格式例如:{fileLoacation:/opt/csbit/?,mainMethod:findHandler}）
    }

    //50.水印算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskWaterMark extends ConfMask {
        public ConfMaskWaterMark() {
            super(AlgoId.WATERMARK);
        }

        public String columns;      //（逗号分割 1.十分位2.百分位3.千分位4.万分位5.十万分位6.百万分位7.千万分位）
    }

    //51数值取整算法
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfMaskNumberRound extends ConfMask {
        public ConfMaskNumberRound() {
            super(AlgoId.NUMBERROUND);
        }

        public int type;   //1：直接舍弃，2四舍五入，3向上取整
    }


    /**************************************************************************
     * 分隔符
     **************************************************************************/
    /**
     * 数据库架构配置类
     * 表，列    脱敏表配置信息
     * Created by byc on 10/22/18.
     */
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "ConfTableMask")
    @XmlType(propOrder = {"name", "columns"})
    public static class ConfTableMask {
        @XmlElement(name = "name")
        public String name;//表名称
        @XmlElementWrapper(name = "columns")
        @XmlElement(name = "column")
        public List<ConfColumnMask> columns = new ArrayList<ConfColumnMask>();//列
    }

    //脱敏列配置信息
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfColumnMask {
        public String name;//列名称
        public boolean isMask;//true：该列进行脱敏；false：该列不进行脱敏
        public ConfMask confMask;//脱敏算法配置
        public AlgoBase algoBase;//算法对象
    }


    /**************************************************************************
     * 分隔符
     **************************************************************************/
    /**
     * 扫描配置
     * 扫描时每个算法的配置结构
     * Created by byc on 10/22/18.
     */
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfFind {
        public AlgoId id;//算法类型ID
        public long ruleId;
        public double rate = 0.6;//匹配率
        public int count;//匹配行数
        public String extend = "";//扩展：正则；混合算法；自定义（json格式例如:{fileLoacation:/opt/csbit/?,mainMethod:findHandler}）

        public ConfFind() {
        }

        public ConfFind(AlgoId id, Long ruleId, double rate, int count, String extend) {
            this.id = id;
            this.ruleId = ruleId;
            this.rate = rate;
            this.count = count;
            this.extend = extend;
        }
    }


    //发现表配置信息
    public static class ConfTableFind {
        public String name;//表名称
        public List<ConfFind> confFinds = new ArrayList<ConfFind>();//发现算法配置
        public List<ConfColumnFind> columns = new ArrayList<ConfColumnFind>();//列
    }

    //发现列配置信息
    public static class ConfColumnFind {
        public String name;//列名称
        public boolean isFind;//true：该列进行发现；false：该列不进行发现

        public ConfColumnFind() {
        }

        public ConfColumnFind(String name, boolean isFind) {
            this.name = name;
            this.isFind = isFind;
        }
    }

    public static class FindResultItem {
        public AlgoId algoId;//算法ID
        public long customId;//针对一些自定义类型，返回该类型的ID。（自定义算法，正则算法，混合列算法）
        public int matchRows;//匹配行数
        public List<String> samples = new ArrayList<String>();//样例
    }

    //记录每个列的发现结果
    public static class FindResult {
        public int index;//列index
        public String name;//列名
        public int tableRows;//表格行数
        public int scanRows; //扫描行数
        public List<FindResultItem> algoItems = new ArrayList<FindResultItem>();//结果匹配的算法集合
    }
}
