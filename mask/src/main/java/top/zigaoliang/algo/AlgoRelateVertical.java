package top.zigaoliang.algo;

import org.apache.log4j.Logger;
import top.zigaoliang.conf.ErrorCode;
import top.zigaoliang.core.AlgoId;

import java.util.List;

/**
 * 关联算法--纵向列关联算法
 * Created by byc on 10/24/18.
 */
public class AlgoRelateVertical extends AlgoBase {
    private static Logger log = Logger.getLogger(AlgoRelateVertical.class);

    public AlgoRelateVertical() {
        super(AlgoId.RELATEVERTICAL);
        attr = 2;
    }

    private List<Object> listColData = null;//保存一列数据
    private int index = 0;//列数据提取索引

    public void setListColData(List<Object> listColData) {
        this.listColData = listColData;
    }

    @Override
    public boolean find(String in) {
        return false;
    }

    @Override
    public int random(String in, StringBuilder out) {
        if (listColData == null) {
            out.append(in);
            ErrorCode errorCode = ErrorCode.RELATE_VERTICAL_DATA;
            log.debug(errorCode.getMsg());
            return errorCode.getCode();
        }

        if (listColData.size() <= index) {
            out.append(in);
            ErrorCode errorCode = ErrorCode.RELATE_VERTICAL_INDEX;
            log.debug(errorCode.getMsg());
            return errorCode.getCode();
        }

        out.append(listColData.get(index++));
        return 0;
    }

    @Override
    public int mask(String in, StringBuilder out) {
        random(in, out);
        return 0;
    }

    @Override
    public int unmask(String in, StringBuilder out) {
        random(in, out);
        return 0;
    }

    @Override
    public int cover(String in, StringBuilder out) {
        out.append(in);
        return 0;
    }
}
