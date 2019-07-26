package top.zigaoliang.algo;

import com.google.common.base.Strings;
import lombok.Data;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.CommonUtil;
import top.zigaoliang.util.HashMapUtil;
import top.zigaoliang.util.IndexMapList;
import top.zigaoliang.util.MaskUtil;
import top.zigaoliang.util.Util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 银行卡号算法
 *
 * @author byc
 * @date 10/24/18
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoBankCard extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoBankCard.class);
    static String regex = "[-_，, |]";

    private static IndexMapList indexMapList;

    /**
     * 初始化发卡行代码字典
     */
    static {
        indexMapList = HashMapUtil.convertToIndexMap("/bankcard.txt");
    }

    public AlgoBankCard() {
        super(AlgoId.BANKCARD);
    }

    @Override
    public boolean find(String in) {
        if (!containChar(in)) {
            return false;
        }
        //去掉银行卡中的特殊字符后验证 银行卡的长度
        in = CommonUtil.removeSpecial(in, regex);
        if (!this.checkLength(in)) {
            return false;
        }
        String six = in.substring(0, 6);
        if (HashMapUtil.containsKey(indexMapList.getMap(), six)) {
            return this.checkLuhn(in);
        } else {
            return false;
        }
    }


    private boolean checkLuhn(String in) {
        int sum = getLunhSum(in);
        if (sum % 10 == 0) {
            return true;
        }
        return false;
    }

    private int getLunhSum(String in) {
        int sum = 0;
        int sumRest = 0;
        int tmp = 0;

        String[] ins = in.split("");
        int length = ins.length;
        for (int i = length - 1; i >= 0; i--) {
            if ((length - i) % 2 == 0) {
                tmp = Integer.valueOf(ins[i]) * 2;
                sum += tmp > 9 ? tmp - 9 : tmp;
            } else {
                sumRest += Integer.valueOf(ins[i]);
            }
        }
        return sum + sumRest;
    }

    private boolean checkLength(String in) {
        if (in.length() < 12 || in.length() > 19) {
            return false;
        }
        return true;
    }

    @Override
    public int random(String in, StringBuilder out) {
        StringBuilder result = new StringBuilder();
        //记录源数据的格式
        Map<Integer, String> specialMap = getSpecialCharIndex(in);
        Conf.ConfMaskBankCard conf = (Conf.ConfMaskBankCard) confMask;
        String newIn = CommonUtil.removeSpecial(in, regex);
        if (conf.bank) {
            //保留发卡代码
            newIn = this.randomBank(newIn);
            result.append(newIn.substring(0, newIn.length() - 1)).append(this.getCheckLuhn(newIn));
        } else {
            int bankcarPrefixIndex = Util.getNumByRange(0, indexMapList.getList().size() - 1);
            String bankCard = indexMapList.getList().get(bankcarPrefixIndex).concat(this.randomStr(newIn.substring(6)));
            result.append(bankCard.substring(0, bankCard.length() - 1)).append(this.getCheckLuhn(bankCard));
        }
        //还原数据的格式
        rebackFormat(getSpecialCharIndex(in), in, result.toString(), out);
        return 0;
    }

    private int getCheckLuhn(String in) {
        int sum = getLunhSum(in) - Integer.valueOf(in.charAt(in.length() - 1) + "");
        int last = Integer.valueOf((sum + "").charAt((sum + "").length() - 1) + "");
        return 10 - last;
    }

    private String randomBank(String in) {
        String six = in.substring(0, 6);
        in = in.substring(6);
        return six.concat(this.randomStr(in));
    }

    private String randomStr(String in) {
        StringBuilder sb = new StringBuilder();
        String[] ins = in.split("");
        for (String s : ins) {
            int i = Integer.valueOf(s);
            i = i == 0 ? confMask.seed - 1 : confMask.seed / i;
            Random random = new Random(i);
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public int mask(String in, StringBuilder out) {
        Conf.ConfMaskBankCard conf = (Conf.ConfMaskBankCard) confMask;
        StringBuilder result = new StringBuilder();
        //记录源数据的格式
        String newIn = CommonUtil.removeSpecial(in, regex);
        if (conf.bank) {
            newIn = this.maskBank(newIn);
            result.append(newIn.substring(0, newIn.length() - 1)).append(this.getCheckLuhn(newIn));
        } else {
            int bankCardPrefixOldIndex = HashMapUtil.getMapValue(indexMapList.getMap(), newIn.substring(0, 6));
            int[] indexRange = {0, indexMapList.getList().size() - 1};
            int bankCardPrefixNewIndex = Util.maskBaseForInteger(indexRange, bankCardPrefixOldIndex, conf.seed, true);
            String bankCard = indexMapList.getList().get(bankCardPrefixNewIndex).concat(this.maskStr(newIn.substring(6)));
            result.append(bankCard.substring(0, bankCard.length() - 1)).append(this.getCheckLuhn(bankCard));
        }
        rebackFormat(getSpecialCharIndex(in), in, result.toString(), out);
        return 0;
    }

    private String maskBank(String in) {
        String six = in.substring(0, 6);
        in = in.substring(6);
        return six.concat(this.maskStr(in));
    }

    private String maskStr(String in) {
        StringBuilder sb = new StringBuilder();
        char[] ins = in.toCharArray();
        for (char s : ins) {
            char i = MaskUtil.maskDigit(confMask.seed, s);
            sb.append(i);
        }
        return sb.toString();
    }

    private String unmaskStr(String in) {
        StringBuilder sb = new StringBuilder();
        char[] ins = in.toCharArray();
        for (char s : ins) {
            char i = MaskUtil.unMaskDigit(confMask.seed, s);
            sb.append(i);
        }
        return sb.toString();
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        Conf.ConfMaskBankCard conf = (Conf.ConfMaskBankCard) confMask;
        StringBuilder result = new StringBuilder();
        //记录源数据的格式
        String newIn = CommonUtil.removeSpecial(in, regex);

        if (conf.bank) {
            newIn = this.maskBank(newIn);
            result.append(newIn.substring(0, newIn.length() - 1)).append(this.getCheckLuhn(newIn));
        } else {
            int bankCardPrefixOldIndex = HashMapUtil.getMapValue(indexMapList.getMap(), newIn.substring(0, 6));
            int[] indexRange = {0,indexMapList.getList().size() - 1};
            int bankCardPrefixNewIndex = Util.maskBaseForInteger(indexRange, bankCardPrefixOldIndex, conf.seed, false);
            String bankCard = indexMapList.getList().get(bankCardPrefixNewIndex).concat(this.unmaskStr(newIn.substring(6)));
            result.append(bankCard.substring(0, bankCard.length() - 1)).append(this.getCheckLuhn(bankCard));
        }
        rebackFormat(getSpecialCharIndex(in), in, result.toString(), out);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        Conf.ConfMaskBankCard conf = (Conf.ConfMaskBankCard) confMask;
        //记录源数据的格式
        String newIn = CommonUtil.removeSpecial(in, regex);
        StringBuilder result = new StringBuilder();
        if (conf.coverType) {
            out.append(CommonUtil.coverBySymbol(conf.symbol, 6)).append(in.substring(6));
        } else {
            out.append(in.substring(0, 6))
                        .append(CommonUtil.coverBySymbol(conf.symbol, in.length() - 1 - 6))
                        .append(in.substring(in.length() - 1));
        }
        return 0;
    }

    //判断字符串中是否只包含指定的字符
    public boolean containChar(String src) {
        String regex = "^[0-9-_，, |]+$";
        return src.matches(regex);
    }

    /**
     * 获得银行卡号中的分割字符及索引
     *
     * @param in
     * @return
     */
    public Map<Integer, String> getSpecialCharIndex(String in) {
        String[] specialArray = {",", "-", "_", "|", "，", " "};
        String[] charArray = in.split("");
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < charArray.length; i++) {
            for (int j = 0; j < specialArray.length; j++) {
                if (charArray[i].equals(specialArray[j])) {
                    map.put(i, specialArray[j]);
                }
            }
        }
        return map;
    }

    /**
     * 还原字符串的格式
     */
    public String rebackFormat(Map<Integer, String> specialMap, String in, String result, StringBuilder out) {
        List<Integer> specialArray = new ArrayList<>();
        for (Integer key : specialMap.keySet()) {
            specialArray.add(key);
        }
        Collections.sort(specialArray);
        char[] charArrayOld = in.toCharArray();
        char[] charArray = result.toCharArray();
        int m = 0, n = 0;
        for (int i = 0; i < charArrayOld.length; i++) {
            if (Character.isDigit(charArrayOld[i])) {
                //如果是数字
                out.append(charArray[m]);
                m++;
            } else {
                //如果是特殊字符
                out.append(specialMap.get(specialArray.get(n)));
                n++;
            }
        }
        return out.toString();
    }


    @Override
    public int random(StringBuilder out) {
        String in = "6222023202020196142";

        Conf.ConfMaskBankCard conf = new Conf.ConfMaskBankCard();

        conf.bank = Util.getNumByRange(0, 1) == 0;
        conf.check = Util.getNumByRange(0, 1) == 0;

        conf.seed = Util.getNumByRange(0, 65565);
        this.confMask = conf;

        this.mask(in, out);
        return 0;
    }

    @Override
    public Object[] validateMaskData(Conf.ConfMask conf, String in, String out) {
        if (Strings.isNullOrEmpty(in) || Strings.isNullOrEmpty(out)) {
            return new Object[]{false, "in or out data is null"};
        }
        //发卡行代码
        String inBankNo = in.substring(0, 6);
        String outBankNo = out.substring(0, 6);
        //自定义部分
        String inCustom = in.substring(6, in.length() - 1);
        String outCustom = out.substring(6, in.length() - 1);

        Conf.ConfMaskBankCard cf = (Conf.ConfMaskBankCard) conf;
        switch (conf.process) {
            case MASK:
            case UNMASK:
            case RANDOM:
                //保留发卡行代码
                if (cf.isBank()) {
                    if (!inBankNo.equals(outBankNo)) {
                        return new Object[]{false, "违反保留发卡行代码策略"};
                    }
                } else {
                    if (inBankNo.equals(outBankNo)) {
                        return new Object[]{false, "违反保留发卡行代码策略"};
                    }
                }

                break;
            case COVER:
                String coverBankNo = cf.symbol + cf.symbol + cf.symbol + cf.symbol + cf.symbol + cf.symbol;
                StringBuilder coverCustomBuf = new StringBuilder();
                for (int i = 0; i < inCustom.length(); i++) {
                    coverCustomBuf.append(cf.symbol);
                }
                String coverCustom = coverCustomBuf.toString();

                //遮蔽发卡行代码
                if (cf.isCoverType()) {
                    if (!outBankNo.equals(coverBankNo)) {
                        return new Object[]{false, "违反遮蔽发卡行代码策略"};
                    }
                }
                //遮蔽发卡行代码到校验位之间的部分
                else {
                    if (!outCustom.equals(coverCustom)) {
                        return new Object[]{false, "违反遮蔽自定义部分策略"};
                    }
                }
                break;
        }
        return new Object[]{true, null};
    }
}
