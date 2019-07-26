package top.zigaoliang.core;

/**
 * 算法类型ID
 * Created by byc on 10/22/18.
 */
public enum AlgoId {
    //具体某类型数据的算法
    UNKOWN(0),//未知算法
    EMAIL(1),//电子邮箱算法
    ADDRESS(2),//中文地址算法
    COMPANY(3),//公司名称算法
    UNITNAME(4), //单位名称算法
    CHINESENAME(5),//中文姓名算法
    CELLPHONE(6),//手机号码算法
    TELEPHONE(7),//座机号码算法
    PHONE(8),//电话号码算法
    DATE(9),//日期算法
    TAXNUMBER(10),//税号算法
    CREDITCODE(11),//统一社会信用代码算法
    IDCARD(12),//身份证算法
    MONEY(13),//金额数字算法
    BANKCARD(14),//银行卡号算法
    STOCKCODE(15),//股票代码算法
    STOCKNAME(16),//股票名称算法
    COMPATRIOTID(17),//台胞证算法
    RESIDENCE(18),//永久居住证算法
    PASSPORTHKAM(19),//Hong Kong and Macao passport港澳通行证算法
    PASSPORTCHINESE(20),//中国护照算法
    POSTALCODE(21),//邮政编码算法
    MILITARYCARD(22),//军官证算法
    FUNDCODE(23),//基金代码算法
    FUNDNAME(24),//基金名称算法
    ACCOUNT(25),//开户账号算法
    IPADDRESS(26),//IP地址算法
    MACADDRESS(27),//MAC地址算法
    PLATENUMBER(28),//车牌号算法
    FRAMENUMBER(29),//车架号算法
    CUSTOMERNAME(30),//客户名称算法
    INTEGER(31),//整数算法
    INTEGERRANGE(32),//整数区间算法


    //通用算法
    COVER(33),//遮蔽算法
    RANDOMSTRING(34),//随机字符串算法
    FIXEDVALUE(35),//固定值算法
    HASHVALUE(36),//HASH算法

    //列关联算法
    RELATEIDTOAGE(37),//关联算法--身份证取年龄
    RELATEIDTOBIRTHDAY(38),//关联算法--身份证取生日
    RELATECOMPUTE(39),//关联算法--计算关联算法
    RELATEVERTICALMAIN(40),//主关联算法--和副关联算法为同一个算法，主关联列是需要打乱的列，而副关联列跟随主关联列变化
    RELATEVERTICAL(41),//副关联算法--纵向列关联算法
    RELATEVERTICALGROUP(42),//分组关联算法
    MIXEDCOLUMN(43),//混合列算法

    //自定义扫描/脱敏算法,这几个算法只在后台自定义算法中使用，不在前台配置
    REGEX(44),//正则表达式
    DICTIONARY(45),//特征字典
    DICTIONARYMAP(46),//映射字典
    DICTIONARYRANDOM(47),//随机字典
    CUSTOM(48),//自定义算法

    WATERMARK(50), //数据水印 50
    NUMBERROUND(51)  //数值取整

    ;
    private final int id;
    AlgoId(int id) {
        this.id = id;
    }

    /**
     * 获取算法ID
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * 获取算法实例
     * @param id
     * @return
     */
    public static AlgoId getAlgoId(int id) {
        for (AlgoId algoId: values()) {
            if (algoId.getId() == id) {
                return algoId;
            }
        }
        return null;
    }

    public static int size() {
        return values().length + 1;
    }
}
