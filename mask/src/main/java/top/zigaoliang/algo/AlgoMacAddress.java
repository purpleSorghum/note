package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.Util;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * MAC地址算法
 *
 * @author byc
 * @date 10/24/18
 */
public class AlgoMacAddress extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoMacAddress.class);

    public AlgoMacAddress() {
        super(AlgoId.MACADDRESS);
    }

    private Conf.ConfMaskMacAddress confMaskMacAddress = new Conf.ConfMaskMacAddress();

    @Override
    public int init(Conf.ConfMask confMask) {
        if (confMask == null) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        this.confMask = confMask;
        if (confMask instanceof Conf.ConfMaskMacAddress) {
            confMaskMacAddress = (Conf.ConfMaskMacAddress) confMask;
        }
        return 0;
    }

    @Override
    public boolean find(String in) {
        if(in.length() != 17){
            return false;
        }
        ErrorCode errorCode = null;
        //MAC正则
        String regEx = "^[A-Fa-z0-9]{2}([-:][A-Fa-z0-9]{2}){5}$";
        if (!Pattern.compile(regEx).matcher(in).matches()) {
            //MAC格式不正确
            errorCode = ErrorCode.MAC_ADDRESS_INPUT;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        ErrorCode errorCode = null;
        String splitChar = getLetterSplit(in);
        String[] array = in.split(splitChar);
        boolean letterState = getLetterState(in);
        StringBuilder result  = new StringBuilder();
        try {
            //保留前四位
            if (confMaskMacAddress.prefix) {
                result.append(array[0]).append(splitChar).append(array[1]);
            } else {
                result.append(getMacNode(2, letterState, splitChar));
            }
            result.append(splitChar).append(getMacNode(2, letterState, splitChar)).append(splitChar);
            //保留后四位
            if (confMaskMacAddress.suffix) {
                result.append(array[array.length - 2]).append(splitChar).append(array[array.length - 1]);
            } else {
                result.append(getMacNode(2, letterState, splitChar));
            }
            out.append(result.toString().length() <= in.length()?result.toString():in);
        } catch (Exception e) {
            errorCode = ErrorCode.MAC_RANDOM_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }


    @Override
    public int mask(String in, StringBuilder out) {
        return maskBase(in, out, true);
    }
    @Override
    public int unmask(String in, StringBuilder out) {
        return maskBase(in, out, false);
    }

    @Override
    public int cover(String in, StringBuilder out) {
        String splitChar = getLetterSplit(in);
        String[] array = in.split(splitChar);
        switch (confMaskMacAddress.coverType) {
            case 1:
                out.append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2)).append(splitChar)
                        .append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2)).append(splitChar)
                        .append(array[2]).append(splitChar).append(array[3]).append(splitChar)
                        .append(array[4]).append(splitChar).append(array[5]);
                break;
            case 2:
                out.append(array[0]).append(splitChar).append(array[1]).append(splitChar)
                        .append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2))
                        .append(splitChar).append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2))
                        .append(splitChar).append(array[4]).append(splitChar).append(array[5]);
                break;
            case 3:
                out.append(array[0]).append(splitChar).append(array[1]).append(splitChar).append(array[2]).append(splitChar)
                        .append(array[3]).append(splitChar).append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2))
                        .append(splitChar).append(CommonUtil.coverBySymbol(confMaskMacAddress.symbol, 2));
                break;
            default:
                out.append(in);
        }
        return 0;
    }

    /**
     * @param flag 标志位  true   正向脱敏   false   逆向脱敏
     * @return
     */
    public int maskBase(String in,StringBuilder out,boolean flag) {
        ErrorCode errorCode = null;
        String splitChar = getLetterSplit(in);
        String[] nodeArray = in.split(splitChar);
        String[] newNodeArray = new String[6];
        //MAC地址字母的大小写状态
        try {
            //保留前四位
            if (confMaskMacAddress.prefix) {
                newNodeArray[0] = nodeArray[0];
                newNodeArray[1] = nodeArray[1];
            } else {
                newNodeArray[0] = maskMacAdressNodeLetter(nodeArray[0], confMaskMacAddress.seed, flag);
                newNodeArray[1] = maskMacAdressNodeLetter(nodeArray[1], confMaskMacAddress.seed, flag);
            }
            newNodeArray[2] = maskMacAdressNodeLetter(nodeArray[2], confMaskMacAddress.seed, flag);
            newNodeArray[3] = maskMacAdressNodeLetter(nodeArray[3], confMaskMacAddress.seed, flag);
            //保留后四位
            if (confMaskMacAddress.suffix) {
                newNodeArray[4] = nodeArray[4];
                newNodeArray[5] = nodeArray[5];
            } else {
                newNodeArray[4] = maskMacAdressNodeLetter(nodeArray[4], confMaskMacAddress.seed, flag);
                newNodeArray[5] = maskMacAdressNodeLetter(nodeArray[5], confMaskMacAddress.seed, flag);
            }
            //将数组元素和分割字符拼接成一个mac地址out.append(rangeMacFromArray(newNodeArray,splitChar));
            out.append(rangeMacFromArray(newNodeArray,splitChar));
        } catch (Exception e) {
            errorCode = ErrorCode.MAC_MASK_UNKNOWN;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return errorCode.getCode();
        }
        return 0;
    }

    /**
     * 判断MAC地址中的字符的大小写
     *
     * @param in mac 地址中字符小写 或 全是数字  返回 false   字符大写返回true
     * @return
     */
    public boolean getLetterState(String in) {
        return in.matches(".*[ABCDED].*");
    }

    //将数组元素和分割字符拼接成一个mac地址
    public String rangeMacFromArray(String[] array,String splitChar){
        StringBuilder out  = new StringBuilder();
        for(int i = 0; i < array.length; i++){
            out.append(array[i]).append(splitChar);
        }
        out.deleteCharAt(out.length() - 1);
        return out.toString();
    }

    /**
     *
     * @param in macAddress
     * @return 返回mac地址的分隔符
     */
    public String getLetterSplit(String in){
        if (in.contains("-")) {
            return "-";
        }
        if (in.contains(":")) {
            return ":";
        }
        return ":";
    }

    /**
     *
     * @return    脱敏后的字符
     */
    public String maskMacAdressNodeLetter(String node, int seed, boolean flag) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < node.length(); i++) {
            char ch = node.charAt(i);
            if (Character.isLetter(ch)) {
                if (Character.isUpperCase(ch)) {
                    //大写
                    out.append((char) Util.maskBaseForInteger(Util.upperChar, ch, seed, flag));
                } else {
                    //小写
                    out.append((char) Util.maskBaseForInteger(Util.downChar, ch, seed, flag));
                }
            } else if (Character.isDigit(ch)) {
                out.append(Util.maskBaseForInteger(Util.numberArray, ch - 48, seed, flag));
            }
        }
        return out.toString();
    }


    /**
     * 生成随机数字和字母
     *
     * @param length
     * @return
     */
    public String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出数字还是字母
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(6) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }

        }
        return val;
    }

    /**
     * 生成两位数字或者 字母组合
     *
     * @param length 节点的长度
     * @return
     */
    public String getMacNode(int length, boolean letterState, String splitChar) {
        StringBuilder macNode = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            macNode.append(getStringRandom(2));
            if (i != length) {
                macNode.append(splitChar);
            }
        }
        if (letterState) {
            return macNode.toString().toUpperCase();
        } else {
            return macNode.toString().toLowerCase();
        }
    }


}
