package top.zigaoliang.algo;


import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.core.AlgoId;
import top.zigaoliang.util.AlgoMaskUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author yangying
 * @date 18-12-19
 */
public class AlgoDictionary extends AlgoBase {
    private Logger log = Logger.getLogger(AlgoDictionary.class);
    private List<String> dictionary = null;

    public AlgoDictionary() {
        super(AlgoId.DICTIONARY);
    }

    @Override
    public int init(Conf.ConfFind confFind) {
        dictionary = new ArrayList<>();
        this.loadDict(confFind.extend);
        return super.init(confFind);
    }

    @Override
    public boolean find(String in) {
        if (Strings.isNullOrEmpty(in)) {
            return false;
        }

        String[] dict = null;
        String feature = "";
        Integer matchWay = 0;
        for (int i = 0; i < dictionary.size(); i++) {
            if (Strings.isNullOrEmpty(dictionary.get(i))) {
                continue;
            }
            dict = dictionary.get(i).split("\t");
            feature = dict[0];
            matchWay = Integer.valueOf(dict[1]);
            if (AlgoMaskUtil.match(in, feature, matchWay)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        return 0;
    }

    private void loadDict(String path) {
        if (dictionary == null) {
            synchronized (this) {
                try {
                    dictionary = Files.readAllLines(Paths.get(path));
                } catch (Exception e) {
                    log.debug("", e);
                }
            }
        }
    }
}
