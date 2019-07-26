package top.zigaoliang.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一社会信用代码相关操作
 */
@Data
public class CreditCode {
    private static int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28};//加权因子
    private static int MOD = 31;//模数

    private boolean isMask = true;//正向脱敏还是逆向脱敏

    private final static char MANAGE_DEPARTMENT_CODE_1 = '1';// 机构编制
    private final static char MANAGE_DEPARTMENT_CODE_5 = '5';// 民政
    private final static char MANAGE_DEPARTMENT_CODE_9 = '9';// 工商
    private final static char MANAGE_DEPARTMENT_CODE_Y = 'Y';// 其他

    private final static char ORG_TYPE_CODE_1 = '1';
    private final static char ORG_TYPE_CODE_2 = '2';
    private final static char ORG_TYPE_CODE_3 = '3';
    private final static char ORG_TYPE_CODE_9 = '9';

    private final static char[][] ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE = {
            // 机构编制
            {MANAGE_DEPARTMENT_CODE_1, ORG_TYPE_CODE_1},// 机关
            {MANAGE_DEPARTMENT_CODE_1, ORG_TYPE_CODE_2},// 事业单位
            {MANAGE_DEPARTMENT_CODE_1, ORG_TYPE_CODE_3},// 中央编办直接管理机构编制的群众团体
            {MANAGE_DEPARTMENT_CODE_1, ORG_TYPE_CODE_9},// 其他
            // 民政
            {MANAGE_DEPARTMENT_CODE_5, ORG_TYPE_CODE_1},// 社会团体
            {MANAGE_DEPARTMENT_CODE_5, ORG_TYPE_CODE_2},// 民办非企业单位
            {MANAGE_DEPARTMENT_CODE_5, ORG_TYPE_CODE_3},// 基金会
            {MANAGE_DEPARTMENT_CODE_5, ORG_TYPE_CODE_9},// 其他
            // 工商
            {MANAGE_DEPARTMENT_CODE_9, ORG_TYPE_CODE_1},// 企业
            {MANAGE_DEPARTMENT_CODE_9, ORG_TYPE_CODE_2},// 个体工商户
            {MANAGE_DEPARTMENT_CODE_9, ORG_TYPE_CODE_3},// 明明专业合作社
            // 其他
            {MANAGE_DEPARTMENT_CODE_Y, ORG_TYPE_CODE_1},
    };

    private static final char[] checkCodeMap = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
            'Q', 'R', 'T', 'U', 'W', 'X', 'Y'};

    private static final Map<Character, Integer> checkCodeHashMap = new HashMap<Character, Integer>() {{
        put('A', 0);put('B', 1);put('C', 2);put('D', 3);put('E', 4);put('F', 5);put('G', 6);put('H', 7);put('J', 8);put('K', 9);
        put('L', 10);put('M', 11);put('N', 12);put('P', 13);put('Q', 14);put('R', 15);put('T', 16);put('U', 17);put('W', 18);put('X', 19);put('Y', 20);
    }};

    private int orgTypeIndex = -1;//组织类型索引
    private char[] code = new char[17];//
    private static final int MANAGE_DEPARTMENT_CODE = 0;//等级管理部门代码1位
    private static final int ORG_TYPE_CODE = 1;//机构类别代码1位
    private static final int DIVISION_CODE = 2;//登记管理机关行政区划码6位
    private static final int DIVISION_CODE_LEN = 6;
    private static final int ORG_CODE = 8;//组织机构代码9位
    private static final int ORG_CODE_LEN = 9;
    private char checkCode;//校验码1位

    public static CreditCode create(String code) {
        return create(code.toCharArray());
    }

    public static CreditCode create(char[] code) {
        if (code.length == 18) {
            return new CreditCode(code);
        } else {
            return null;
        }
    }

    private CreditCode() {
    }

    private CreditCode(char[] code) {
        int i = 0;
        for (; i < 17; i++) {
            this.code[i] = code[i];

        }
        this.checkCode = code[i];
    }

    /**
     * 是否合法
     */
    public boolean isValid() {
        // 1. 等级管理部门代码是否合法
        if (!(this.code[MANAGE_DEPARTMENT_CODE] == MANAGE_DEPARTMENT_CODE_1 || this.code[MANAGE_DEPARTMENT_CODE] == MANAGE_DEPARTMENT_CODE_5
                || this.code[MANAGE_DEPARTMENT_CODE] == MANAGE_DEPARTMENT_CODE_9 || this.code[MANAGE_DEPARTMENT_CODE] == MANAGE_DEPARTMENT_CODE_Y)) {
            return false;
        }
        // 2. 机构类别代码是否合法
        if (!(this.code[ORG_TYPE_CODE] == ORG_TYPE_CODE_1 || this.code[ORG_TYPE_CODE] == ORG_TYPE_CODE_2
                || this.code[ORG_TYPE_CODE] == ORG_TYPE_CODE_3 || this.code[ORG_TYPE_CODE] == ORG_TYPE_CODE_9)) {
            return false;
        }
        // 3. 前两位组合是否合法
        boolean valid = false;
        this.orgTypeIndex = 0;
        for (char[] item : ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE) {
            if (this.code[MANAGE_DEPARTMENT_CODE] == item[0]) {
                if (this.code[ORG_TYPE_CODE] == item[1]) {
                    valid = true;
                    break;
                }
            }
            this.orgTypeIndex++;
        }

        if (!valid) {
            this.orgTypeIndex = -1;
            return valid;
        }

        //4. 登记管理机关行政区划码 是否合法
        DivisionCode divisionCode = DivisionCode.create(getDivisionCode());
        if (divisionCode == null || !divisionCode.isValid()) {
            return false;
        }

        //5. 校验组织机构代码是否合法
        OrginazationCode orginazationCode = OrginazationCode.create(getOrgCode());
        if (orginazationCode == null || !orginazationCode.isValid()) {
            return false;
        }
        //6. 校验码是否合法
        return this.checkCode == getCheckCode();
    }

    /**
     * 生成校验码
     */
    public char getCheckCode() {
        // 1. 加权因子对应各位相乘 并求和
        int w = 0;
        for (int i = 0; i < this.code.length; i++) {
            if (Character.isDigit(this.code[i])) {
                w += (this.code[i] - 48) * WEIGHT[i];
            } else {
                w += (checkCodeHashMap.get(this.code[i]) + 10) * WEIGHT[i];// 大写字母 A表示10
            }
        }
        // 2. 取模
        int r = MOD - w % MOD;
        char c;
        if (r == 31) {
            c = '0';
        } else if (r >= 10) {
            c = checkCodeMap[r - 10];
        } else {
            c = (char) ('0' + r);
        }
        return c;
    }

    /**
     * 获取邮政编码
     */
    public char[] getDivisionCode() {
        char[] divesionCode = new char[DIVISION_CODE_LEN];
        System.arraycopy(this.code, DIVISION_CODE, divesionCode, 0, DIVISION_CODE_LEN);
        return divesionCode;
    }

    /**
     * 获取组织机构代码
     */
    public char[] getOrgCode() {
        char[] orgCode = new char[ORG_CODE_LEN];
        System.arraycopy(this.code, ORG_CODE, orgCode, 0, ORG_CODE_LEN);
        return orgCode;
    }

    /**
     * 脱敏前两位
     */
    public CreditCode maskOrgType(long seed) {
        int index;
        if (this.isMask()) {
            index = (int) MaskUtil.maskIndex(seed, ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE.length, this.orgTypeIndex);
        } else {
            index = (int) MaskUtil.unMaskIndex(seed, ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE.length, this.orgTypeIndex);
        }

        this.code[MANAGE_DEPARTMENT_CODE] = ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE[index][0];
        this.code[ORG_TYPE_CODE] = ALL_MANAGE_DEPARTMENT_AND_ORG_TYPE[index][1];

        // 2.校验位生成
        this.checkCode = this.getCheckCode();
        return this;
    }

    /**
     * 获取前两位
     *
     * @return
     */
    public char[] getOrgType() {
        char[] orgType = new char[2];
        System.arraycopy(this.code, 0, orgType, 0, 2);
        return orgType;
    }

    /**
     * 脱敏邮政编码
     */
    public CreditCode maskDivisionCode(long seed) {
        DivisionCode divisionCode = DivisionCode.create(this.getDivisionCode());
        char[] di;
        if (isMask()) {
            di = divisionCode.mask(seed).get();
        } else {
            di = divisionCode.unMask(seed).get();
        }
        System.arraycopy(di, 0, this.code, DIVISION_CODE, DIVISION_CODE_LEN);
        // 2.校验位生成
        this.checkCode = this.getCheckCode();
        return this;
    }

    /**
     * 脱敏组织机构代码
     */
    public CreditCode maskOrgCode(long seed) {
        OrginazationCode orginazationCode = OrginazationCode.create(this.getOrgCode());
        char[] di;
        if (isMask()) {
            di = orginazationCode.mask(seed).get();
        } else {
            di = orginazationCode.unMask(seed).get();
        }
        System.arraycopy(di, 0, this.code, ORG_CODE, ORG_CODE_LEN);
        // 2.校验位生成
        this.checkCode = this.getCheckCode();
        return this;
    }

    public char[] get() {
        char[] code = new char[18];
        System.arraycopy(this.code, 0, code, 0, 17);
        code[17] = this.checkCode;
        return code;
    }

    public boolean isMask() {
        return isMask;
    }

    public void setMask(boolean mask) {
        isMask = mask;
    }
}
