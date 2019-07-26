package top.zigaoliang.algo;


import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;

import java.util.Random;

/**
 * 随机字符串算法
 * Created by byc on 10/24/18.
 */
public class AlgoRandomString extends AlgoBase {
    public AlgoRandomString() {
        super(AlgoId.RANDOMSTRING);
    }

    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        Conf.ConfMaskRandomString conf = (Conf.ConfMaskRandomString)confMask;
        if (conf.number) {
            this.maskByNumber(in, out);
        }
        if (conf.alpha) {
            in = out.length() == 0 ? in : out.toString();
            out.setLength(0);
            this.maskByAlpha(in, out);
        }
        if (conf.chinese) {
            in = out.length() == 0 ? in : out.toString();
            out.setLength(0);
            this.maskByChinese(in, out);
        }
        if (out.length() == 0) {
            out.append(in);
        }
        return 0;
    }

    private void maskByNumber(String in, StringBuilder out) {
        String[] str = in.split("");
        for (String s : str) {
            if (s.matches("[0-9]")) {
                int i = Integer.valueOf(s);
                i = i == 0 ? confMask.seed - 1 : confMask.seed / i;
                Random random = new Random(i);
                out.append(random.nextInt(10));
            } else {
                out.append(s);
            }
        }
    }

    private void maskByAlpha(String in, StringBuilder out) {
        String[] str = in.split("");
        for (String s : str) {
            if (s.matches("[a-zA-Z]")) {
                int i = s.charAt(0);
                i = i == 0 ? confMask.seed - 1 : confMask.seed / i;
                Random random = new Random(i);
                char ch = 'a';
                if (i % 2 == 0) {
                    ch = (char)(random.nextInt(26) + 'a');
                } else {
                    ch = (char)(random.nextInt(26) + 'A');

                }
                out.append(ch);
            } else {
                out.append(s);
            }
        }
    }

    private void maskByChinese(String in, StringBuilder out) {
        String[] str = in.split("");
        for (int i = 0; i < str.length; i++) {
            String s = str[i];
            if (s.matches("[\u4e00-\u9fa5]")) {
                int j = i == 0 ? confMask.seed - 1 : confMask.seed / i;
                Random random = new Random(j);
                char ch = (char)(random.nextInt(0x9fa5 - 0x4e00 + 1) + 0x4e00);
                out.append(ch);
            } else {
                out.append(s);
            }
        }
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return random(in, out);
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        out.append(in);

        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }
}
