package top.zigaoliang.util;

import lombok.Data;

/**
 * 组织机构代码
 */
@Data
public class OrginazationCode {
    private static int[] WEIGHT = {3, 7, 9, 10, 5, 8, 4, 2};//加权因子
    private static int MOD = 11;//模数
    private char[] code = new char[8];//本体代码 由8位数字或者大写拉丁字母组成
    private char checkCode;//校验码 1位数字或者大写拉丁字母

    public static OrginazationCode create(String code) {
        return create(code.toCharArray());
    }

    public static OrginazationCode create(char[] code) {
        if (code.length == 9) {
            return new OrginazationCode(code);
        } else {
            return null;
        }
    }

    private OrginazationCode() {
    }

    private OrginazationCode(char[] code) {
        int i = 0;
        for (int j = 0; j < 8; j++) {
            this.code[j] = code[i++];
        }

        this.checkCode = code[i];
    }

    /**
     * 是否合法
     */
    public boolean isValid() {
        // 验证本体代码是否合法
        for (char aCode : this.code) {
            // 只包含数字和大写字母
            if (!Character.isDefined(aCode) && !Character.isUpperCase(aCode)) {
                return false;
            }
        }
        // 验证校验码是否合法
        return this.getCheckCode() == this.checkCode;
    }

    /**
     * 生成校验码
     */
    public char getCheckCode() {
        // 1. 本体代码与加权银子对应各位相乘 并求和
        int w = 0;
        for (int i = 0; i < this.code.length; i++) {
            if (Character.isDigit(this.code[i])) {
                w += (this.code[i] - 48) * WEIGHT[i];
            } else {
                w += (this.code[i] - 55) * WEIGHT[i];// 大写字母 A表示10
            }
        }
        // 2. 取模
        int r = MOD - w % MOD;
        char c;
        if (r == 11) {
            c = '0';
        } else if (r == 10) {
            c = 'X';
        } else {
            c = (char) ('0' + r);
        }
        return c;
    }

    /**
     * 脱敏
     */
    public OrginazationCode mask(long seed) {
        // 1. 本位代码脱敏
        for (int i = 0; i < this.code.length; i++) {
            if (Character.isDigit(this.code[i])) {
                this.code[i] = MaskUtil.maskDigit(seed, this.code[i]);
            } else {
                this.code[i] = MaskUtil.maskUpperCaseLetter(seed, this.code[i]);
            }
        }
        // 2.校验位生成
        this.checkCode = this.getCheckCode();
        return this;
    }

    /**
     * 逆向脱敏
     */
    public OrginazationCode unMask(long seed) {
        // 1. 本位代码脱敏
        for (int i = 0; i < this.code.length; i++) {
            if (Character.isDigit(this.code[i])) {
                this.code[i] = MaskUtil.unMaskDigit(seed, this.code[i]);
            } else {
                this.code[i] = MaskUtil.unMaskUpperCaseLetter(seed, this.code[i]);
            }
        }

        // 2.校验位生成
        this.checkCode = this.getCheckCode();
        return this;
    }

    public char[] get() {
        char[] code = new char[9];
        System.arraycopy(this.code, 0, code, 0, 8);
        code[8] = this.checkCode;
        return code;
    }
}
