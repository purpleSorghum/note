package top.zigaoliang.algo;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import top.zigaoliang.AlgoFactory;
import top.zigaoliang.conf.Conf;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import java.util.regex.Pattern;

/**
 * 正则表达式
 * Created by byc on 10/24/18.
 * Update zaj
 */
public class AlgoRegex extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoRegex.class.getSimpleName());
    private boolean fuzzy = true;
    private int customId;//自定义ID

    public AlgoRegex() {
        super(AlgoId.REGEX);
    }

    public int getCustomId() {
        return customId;
    }
    private Pattern pattern = null;
    @Override
    public int init(Conf.ConfFind confFind) {
        Conf.ConfMaskRegex conf = (Conf.ConfMaskRegex)AlgoFactory.getConfMask(confFind.id, confFind.extend);
        if(conf != null ) {
            pattern = Pattern.compile(conf.regex);
            fuzzy = conf.fuzzy;
        }
        return super.init(confFind);
    }
    @Override
    public boolean find(String in) {
        ErrorCode errorCode = null;
        try{
            if (Strings.isNullOrEmpty(in)) {
                return false;
            }
            return fuzzy ? pattern.matcher(in).find() : pattern.matcher(in).matches();
        }catch (Exception e){
            errorCode = ErrorCode.REGEX_INPUT_ERROR;
            log.debug(errorCode.getMsg() + "; 输入数据：" + in);
            return false;
        }

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
        out.append(in);
        return 0;
    }
}
