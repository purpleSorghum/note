package top.zigaoliang.conf;

import java.util.List;

public class MixedColumn {
    Long customeId;
    SplitMode split;
    String splitChar;
    List<MixedColumnItem> segment;

    public enum SplitMode {
        CHAR, SPLIT
    }

    public Long getCustomeId() {
        return customeId;
    }

    public void setCustomeId(Long customeId) {
        this.customeId = customeId;
    }

    public SplitMode getSplit() {
        return split;
    }

    public void setSplit(SplitMode split) {
        this.split = split;
    }

    public String getSplitChar() {
        return splitChar;
    }

    public void setSplitChar(String splitChar) {
        this.splitChar = splitChar;
    }

    public List<MixedColumnItem> getSegment() {
        return segment;
    }

    public void setSegment(List<MixedColumnItem> segment) {
        this.segment = segment;
    }
}
