package top.zigaoliang.conf;

/**
 * 错误码
 * 定义错误码和错误字符串
 *
 * @author byc
 * @date 10/24/18
 */
public enum ErrorCode {
    /**
     * 框架错误
     */
    CONF_INIT_FIND(1, "初始化发现算法配置错误"),
    CONF_INIT_MASK(2, "初始化脱敏算法配置错误"),
    CONF_COLUMN_SIZE(3, "配置列数量和数据的列数量不相等"),
    CONF_ALGO_SIZE(4, "发现配置算法数为零"),
    NEW_ALGO_ERROR(5, "创建算法对象错误"),
    FIND_FUNC_ERROR(6, "发现函数处理错误"),
    FIND_FUNC_EMPTY(7, "发现函数输入数据为空"),
    MASK_FUNC_ERROR(8, "脱敏函数处理错误"),
    MASK_FUNC_EMPTY(9, "脱敏函数输入数据为空"),
    MASK_CANNOT_UNMASK(10, "算法不支持逆向"),
    MASK_FUNC_INVALIDDATA(11, "脱敏输入数据不是指定类型"),
    MASK_SORT_ERROR(12, "算法排序出现未知异常"),

    /**
     * 工具方法错误
     */
    READ_FILE_ERROR(10001,"读取静态资源文件出现未知错误"),
    MIXEDCOLUMN_ERROR(10002,"混合列算法出现未知异常"),

    /**电子邮箱*/
    EMAIL_INPUT(1010,"电子邮箱的格式不正确"),
    EMAIL_RANDOM_UNKNOWN(1011,"邮箱仿真出现未知错误"),
    EMAIL_MASK_UNKNOWN(1012,"邮箱脱敏出现未知错误"),

    /**中文地址*/
    ADDRESS_INPUT_LENGTH(1020,"地域的地址成都必须大于4位"),
    ADDRESS_END_LENGTH(1021,"地址不是以地址关键字结尾"),
    ADDRESS_CASCADE_ERROR(1022,"省市县的级联关系不正确"),
    ADDRESS_KEYWORD_NUMBER(1023,"字符串的关键字个数少于3个 不认为是地址"),
    ADDRESS_RANDOM_UNKNOW(1024,"中文地址仿真出现未知错误"),
    ADDRESS_FIND_ERROR(1025,"地址发现出现未知异常"),

    /**公司名称*/
    COMPANY_INPUT(1030,"公司名称的长度应在4到20位之间"),
    COMPANY_WITHOUT_REGION(1031,"公司名称中没有地域信息"),
    COMPANY_INPUT_ERROR(1032,"公司名称格式不符合要求"),
    COMPANY_RANDOM_ERROR(1033,"公司仿真出现未知异常"),
    COMPANY_MASK_ERROR(1034,"公司脱敏出现未知异常"),
    COMPANY_CONTAIN_ERROR(1035,"公司名称不能包含数字或字母"),

    /** 单位名称 **/
    UNIT_INPUT(1040,"公司名称的长度应在4到20位之间"),
    UNIT_WITHOUT_REGION(1041,"公司名称中没有地域信息"),
    UNIT_INPUT_ERROR(1042,"公司名称格式不符合要求"),
    UNIT_RANDOM_ERROR(1043,"公司仿真出现未知异常"),
    UNIT_MASK_ERROR(1044,"公司脱敏出现未知异常"),
    UNIT_CONTAIN_ERROR(1045,"公司名称不能包含数字或字母"),

    /**中文姓名算法错误码*/
    CHINESENAME_IN_NULL(1050, "输入数据为空"),
    CHINESENAME_IN_LENGTH_NULL(1051, "输入数据长度错误"),
    CHINESENAME_MSAK_SURNAME_ERROE(1052, "姓氏不存在"),
    CHINESENAME_UNMSAK_SURNAME_ERROE(1053, "姓氏不存在"),
    CHINESENAME_RANDOM_SURNAME_ERROE(1054, "姓氏不存在"),
    CHINESENAME_RANDOM_ERROR(1055,"中文姓名仿真出现未知异常"),
    CHINESENAME_MASK_ERROR(1056,"中文姓名脱敏出现未知异常"),

    /**手机号码*/
    CELLPHONE_INPUT(1060,"手机号码的格式不正确"),
    CELLPHONE_RANDOM_UNKNOWN(1061,"手机号码仿真出现未知错误"),
    CELLPHONE_MASK_UNKNOWN(1062,"手机号码脱敏出现未知错误"),
    CELLPHONE_LENGTH(1063,"手机号长度不是11位"),
    CELLPHONE_CODE_ERROR(1064,"手机号的前三位不合法"),
    CELLPHONE_PARAM_ERROR(1065,"参数不合法"),

    /**座机号码*/
    TELLPHONE_INPUT(1070,"座机号码的格式不正确"),
    TELLPHONE_RANDOM_UNKNOWN(1071,"座机号码仿真出现未知错误"),
    TELLPHONE_MASK_UNKNOWN(1072,"座机号码脱敏出现未知错误"),
    TELLPHONE_AREACODE_ERROR(1073,"该座机号中的区号不存在"),
    TELLPHONE_RANDOM_ERROR(1074,"座机号脱敏出现未知异常"),

    /** 电话号码**/
    PHONE_RANDOM_ERROR(1080,"电话号码脱敏出现未知异常"),
    PHONE_MASK_ERROR(1081,"电话号码脱敏出现未知异常"),

    /**日期*/
    DATE_INPUT(1090,"日期格式不正确"),
    DATE_SPLIT(1091,"获得日期的年月日出现未知异常"),
    DATE_MOUTH_ERROR(1092,"月份的范围不在1到12之间"),
    DATE_DAY_ERROR(1093,"日期的范围不在1到31之间"),
    DATE_INPUT_UNKNOWN(1094,"日期验证出现未知错误"),
    DATE_RANDOM_UNKNOWN(1095,"日期仿真出现未知错误"),
    DATE_MASK_UNKNOWN(1096,"日期仿真出现未知错误"),
    DATE_INPUT_LENGTH(1097,"日期不能为空"),
    DATE_YEAR_ERROR(1098,"日期的年份不符合要求"),
    DATE_DAY_ERROR_BYYEARANDMONTH(1099,"根据年份和月份计算的 日 不合法"),
    DATE_FIND_NUKNOWN(10990,"日期验证发现未知异常"),
    DATE_REBACKFORMAT_UNKNOWN(10991,"日期拼接成原有日期格式出现未知异常"),
    DATE_RANDOM_ERROR(10992,"日期仿真参数不合法"),

    /**税号*/
    TAXNUMBER_INPUT(10100,"税号的格式不正确"),
    TAXNUMBER_INPUT_RANDOM_UNKNOWN(10101,"税号仿真出现未知错误"),
    TAXNUMBER_INPUT_MASK_UNKNOWN(10102,"税号仿真出现未知错误"),
    TAXNUMBER_INPUT_LENGTH(10103,"税号的长度应该是15 17 18 20"),
    TAXNUMBER_INPUT_ORIGN(10104,"税号中的组织机构不正确"),

    /**身份证*/
    ID_LACKDINGITS(1110,"身份证号码长度应该为15位或18位 "),
    ID_LASTOFNUMBER(1111,"身份证15位都应位数字，18位号码除最后一位外，都应该位数字"),
    ID_INVALIDBIRTH(1112, "身份证出生日期无效"),
    ID_INVALIDSCOPE(1113,"身份证生日不再有效范围内"),
    ID_INVALIDMONTH(1114,"身份证月份无效"),
    ID_CODINGERROR(1115,"身份证地区编码错误"),
    ID_INVALIDCALIBRATION(1116,"身份证校验码无效，不是合法的身份证号码"),
    ID_RANDOM_UNKNOWN(1117,"身份证仿真出现未知异常"),
    ID_MASK_UNKNOWN(1118,"身份证脱敏出现未知异常"),
    ID_BIRTH_UNKNOWN(1119,"身份证出生日期验证出现未知异常"),

    /**金额数字*/
    MONEY_INPUT(1120,"金额数字输入数据错误"),
    MONEY_OVERFMAX(1121,"金额数字超过了最大值"),
    MONEY_UNDERMIN(1122,"金额数字低于最小值"),
    MONEY_UNKNOWN(1123,"生成仿真数据出现未知错误"),
    MONEY_FIND_UNKNOWN(1124,"数字金额发现出现未知错误"),
    MONEY_SHIFT_UNKNOWN(1125,"字符串转成Double越界"),
    MONEY_RANDOM_UNKNOWN(1126,"金额仿真出现未知异常"),

    STOCKCODE_INDEX(1140, "索引值越界"),

    /**港澳通行证*/
    PASSPORTHKAM_INPUT(1180,"港澳通行证格式不正确"),
    PASSPORTHKAM_LENGTH_ERROR(1181,"港澳通行证的长度必须是9位"),
    PASSPORTHKAM_LETTER_ERROR(1182,"港澳通行证的号码的开头必须是W或者C"),
    PASSPORTHKAM_RANDOM_ERROR(1183,"港澳通行证仿真出项未知异常"),
    PASSPORTHKAM_MASK_ERROR(1184,"港澳通行证脱敏出现未知异常"),

    POSTALCODE_MASK_ERROR(1210,"邮编算法脱敏出现未知异常"),


    /**ip*/
    IP_ADDRESS_INPUT(1250,"ip输入格式错误"),
    IP_RANDOM_UNKNOWN(1251,"ip仿真出现未知错误"),
    IP_MASK_UNKNOWN(1252,"ip脱敏出现未知错误"),

    /**MAC*/
    MAC_ADDRESS_INPUT(1260,"MAC输入格式错误"),
    MAC_RANDOM_UNKNOWN(1261,"MAC仿真出现未知错误"),
    MAC_MASK_UNKNOWN(1262,"MAC脱敏出现未知错误"),

    /**车牌号算法错误码*/
    PLATENUMBER_IN_NULL(1270, "发现算法输入数据为空"),
    PLATENUMBER_IN_LENGTH_ERROR(1271, "算法输入数据长度错误"),
    PLATENUMBER_RANDOM_IN_ERROR(1272, "随机算法输入数据错误"),
    PLATENUMBER_MASK_IN_ERROR(1273, "脱敏算法输入数据错误"),
    PLATENUMBER_UNMASK_IN_ERROR(1274, "逆向脱敏算法输入数据错误"),
    PLATENUMBER_IN_ERROR(1275, "算法输入数据错误"),
    PLATENUMBER_RANDOM_ERROR(1276, "车牌号仿真出现未知错误"),
    PLATENUMBER_MASK_ERROR(1278,"车牌号可逆脱敏出现未知异常"),

    /**车架号算法错误码*/
    FRAMENUMBER_IN_NULL(1280, "输入数据为空"),
    FRAMENUMBER_IN_LENGTH_ERROR(1281, "发现算法输入数据长度错误"),
    FRAMENUMBER_CHECKBIT_ERROR(1282, "校验位计算错误"),
    FRAMENUMBER_RANDOM_IN_ERROR(1283, "随机算法输入数据错误"),
    FRAMENUMBER_MASK_IN_ERROR(1284, "脱敏算法输入数据错误"),
    FRAMENUMBER_UNMASK_IN_ERROR(1285, "逆向脱敏算法输入数据错误"),
    FRAMENUMBER_FORMAT_ERROR(1286,"车架编号格式不正确"),
    FRAMENUMBER_RANDOM_ERROR(1287,"车架号仿真出现未知异常"),
    FRAMENUMBER_MASK_ERROR(1288,"车架号脱敏出现未知异常"),

    /**整数*/
    INTEGER_INPUT_SHORT(1300,"整数的输入值之不能为空"),
    INTEGER_INPUT_LONG(1301,"整数的输入值太长"),
    INTEGER_RANDOM_UNKNOWN(1302,"整数仿真出现未知错误"),
    INTEGER_MASK_UNKNOWN(1303,"整数脱敏出现未知错误"),
    INTEGER_INPUT(1305,"整数的输入值格式错误"),
    INTEGER_INPUT_OUT(1306,"该值超出了整形的取值范围"),
    INTEGER_START_ZERO(1307,"整数不能以0开头"),

    /**遮蔽*/
    COVER_INPUT(1310,"参数的长度不能为空"),
    COVER_INDEX_LONG(1311,"要遮蔽的起始坐标太大"),
    COVER_INDEX_UNDER(1312,"要遮蔽的结束坐标不能太小"),
    COVER_INDE_ERROR(1313,"要遮蔽的字符串的起始 结束位置不合法"),
    COVER_EMAILANDMONEY(1314,"对数字金额和邮箱遮蔽出现未知异常"),

    /**hash*/
    HASH_INPUT(1340,"没有实现该算法"),
    HASH_SHA(1341,"SHA加密出现未知异常"),
    HASH_MD5(1342,"MD5加密出现未知异常"),
    HASH_ENCRYPT(1343,"加密出现未知异常"),

    /**
     * 算法错误码
     * 每个算法10个错误码，错误码【1420】中42为算法ID
     */
    RELATE_COMPUTE_INPUT(1410, "计算关联算法输入数据错误"),
    RELATE_COMPUTE_UNKNOWN(1411, "计算关联算法未知错误"),
    RELATE_VERTICAL_DATA(1420, "纵向列关联算法初始化数据为空"),
    RELATE_VERTICAL_INDEX(1421, "纵向列关联算法索引值越界"),

    /**正则表达式算法**/
    REGEX_INPUT_ERROR(1440,"数据于正则格式不匹配"),

    WATERMARK_MASK_INT_ERROR(1480,"水印整数算法脱敏出现未知异常"),
    WATERMARK_MASK_FLOAT_ERROR(1480,"水印浮点数算法脱敏出现未知异常"),
    ;
    private final int code;//错误码
    private final String msg;//错误字符串（错误的文字解释）

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return "CODE:" + code + "; MSG:" + msg;
    }

    public static ErrorCode getErrorCode(int code) {
        for (ErrorCode errorCode: values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
