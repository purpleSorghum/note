package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 映射字典
 *
 * @author byc
 * @date 10/24/18
 * Update zaj
 */
public class AlgoDictionaryMap extends AlgoBase {
    private List<String[]> dictionary = null;

    public AlgoDictionaryMap() {
        super(AlgoId.DICTIONARYMAP);
    }

    @Override
    public int init(Conf.ConfMask confMask) {
        if (!(confMask instanceof Conf.ConfMaskDictionaryMap)) {
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
        for (int i = 0; i < dictionary.size(); i++) {
            String[] dict = dictionary.get(i);
            if (dict.length == 0) {
                continue;
            }
            String feature = dict[0];
            int matchWay = 0;
            try {
                matchWay = Integer.valueOf(dict[1].trim());
            }
            catch (Exception e) {}

            if (AlgoMaskUtil.match(in, feature, matchWay)) {
                String value = dict.length <= 2 ? "" : dict[2];

                if (matchWay == 1) {
                    out.append(in.replaceAll(feature, value));
                } else {
                    out.append(value);
                }
                return 0;
            }
        }
        out.append(in);
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return this.random(in, out);
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
                    try {
                        Conf.ConfMaskDictionaryMap conf = (Conf.ConfMaskDictionaryMap) confMask;
                        List<String> allLines = Files.readAllLines(Paths.get(conf.path));
                        dictionary = new ArrayList<>();
                        allLines.forEach(line -> {
                            dictionary.add(line.split("\t+"));
                        });
                    } catch (Exception e) {
                        Logger.getLogger(this.getClass().getSimpleName()).error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
