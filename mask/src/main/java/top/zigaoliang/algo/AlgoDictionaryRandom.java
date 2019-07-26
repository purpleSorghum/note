package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 随机字典
 *
 * @author byc
 * @date 10/24/18
 * Update zaj
 */
public class AlgoDictionaryRandom extends AlgoBase {
    private List<Object[]> dictionary = null;
    private Random random = null;

    public AlgoDictionaryRandom() {
        super(AlgoId.DICTIONARYRANDOM);
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskDictionaryRandom)) {
            return ErrorCode.CONF_INIT_MASK.getCode();
        }
        dictionary = null;
        this.loadDictionary(confMask);
        return super.init(confMask);
    }

    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        if (Strings.isNullOrEmpty(in)) {
            return 0;
        }
        random = new Random();
        for (int i = 0; i < dictionary.size(); i++) {
            Object[] dict = dictionary.get(i);
            String feature = dict[0].toString();
            int matchWay = 0;
            try {
                matchWay = Integer.valueOf(dict[1].toString().trim());
            }
            catch (Exception e) {}

            if (AlgoMaskUtil.match(in, feature, matchWay)) {
                String[] values = (String[])dict[2];
                String repValue = values[random.nextInt(values.length)];

                if (matchWay == 1) {
                    out.append(in.replaceAll(feature, repValue));
                } else {
                    out.append(repValue);
                }
                return 0;
            }
        }
        out.append(in);
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        this.random(in, out);
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return ErrorCode.MASK_CANNOT_UNMASK.getCode();
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    private void loadDictionary(Conf.ConfMask confMask) {
        if (dictionary == null) {
            synchronized (this) {
                if (dictionary == null) {
                    Conf.ConfMaskDictionaryRandom conf = (Conf.ConfMaskDictionaryRandom)confMask;
                    this.loadDict(conf.path);
                }
            }
        }
    }

    private void loadDict(String path) {
        try {
            dictionary = Files.readAllLines(Paths.get(path)).stream().filter(l -> !Strings.isNullOrEmpty(l)).map(l -> {
                String[] ele = l.split("\t+");
                String[] vals = ele.length == 2 ? new String[]{""} : ele[2].split(",|，");
                Object[] objs = new Object[]{ele[0], ele[1], vals};
                return objs;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
