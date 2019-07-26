package top.zigaoliang.util;

import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.common.FileHelper;
import top.zigaoliang.conf.DivisionCodeVO;

import java.util.Arrays;
import java.util.List;


/**
 * 行政区划分码
 */
@Data
public class DivisionCode {
    private char[] code = new char[6];
    private int checkCode;
    public static Integer[] dict = null;

    public static DivisionCode create(String code) {
        return create(code.toCharArray());
    }

    public static DivisionCode create(char[] code) {
        if (code.length == 6) {
            return new DivisionCode(code);
        } else {
            return null;
        }
    }

    private DivisionCode(char[] code) {
        System.arraycopy(code, 0, this.code, 0, 6);
        this.checkCode = Integer.valueOf(String.valueOf(this.code));
        loadDict();
    }

    public boolean isValid() {
        return Arrays.binarySearch(dict, this.checkCode) >= 0;
    }

    public DivisionCode mask(long seed) {
        int index = Arrays.binarySearch(dict, this.checkCode);
        int maskIndex = (int) MaskUtil.maskIndex(seed, dict.length, index);
        this.checkCode = dict[maskIndex];
        System.arraycopy(String.valueOf(this.checkCode).toCharArray(), 0, this.code, 0, 6);
        return this;
    }

    public DivisionCode unMask(long seed) {
        int index = Arrays.binarySearch(dict, this.checkCode);
        int maskIndex = (int) MaskUtil.unMaskIndex(seed, dict.length, index);
        this.checkCode = dict[maskIndex];
        System.arraycopy(String.valueOf(this.checkCode).toCharArray(), 0, this.code, 0, 6);
        return this;
    }

    public char[] get() {
        return this.code;
    }


    public  void loadDict() {
        if (dict == null) {
            synchronized (this) {
                try {
                    List<DivisionCodeVO> codes = FileHelper.readSource("/divisioncode.txt",DivisionCodeVO.class);
                    dict = codes.stream().map(DivisionCodeVO::getCode).toArray(Integer[]::new);
                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getSimpleName()).error(e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }
    }

}
