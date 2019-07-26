package top.zigaoliang.algo;

import lombok.Data;
import top.zigaoliang.core.AlgoId;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * 台胞证算法
 * Created by byc on 10/24/18.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AlgoCompatriotId extends AlgoBase {
    public AlgoCompatriotId() {
        super(AlgoId.COMPATRIOTID);
    }

    @Override
    public boolean find(String in) {
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
}
