package top.zigaoliang.util;

/**
 * @author HeZhong
 * @Date 18/11/22
 */
public class MaskUtil {

    public static char maskUpperCaseLetter(long seed, char c) {
        return (char) (MaskUtil.maskIndex(seed, 26, c - 'A') + 'A');
    }

    public static char unMaskUpperCaseLetter(long seed, char c) {
        return (char) (MaskUtil.unMaskIndex(seed, 26, c - 'A') + 'A');
    }

    public static char maskDigit(long seed, char c) {
        return (char) (MaskUtil.maskIndex(seed, 10, c - '0') + '0');
    }

    public static char unMaskDigit(long seed, char c) {
        return (char) (MaskUtil.unMaskIndex(seed, 10, c - '0') + '0');
    }

    public static long maskIndex(long seed, int size, int index) {
        int[] indexRange = {0,size -1};
        return Util.maskBaseForInteger(indexRange,index,(int)seed,true);
    }

    public static long unMaskIndex(long seed, int size, int index) {
        int[] indexRange = {0,size -1};
        return Util.maskBaseForInteger(indexRange,index,(int)seed,false);
    }

}
