package top.zigaoliang.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeNode {
    Map<String, TreeNode> map = new HashMap<>();
    List<Integer> indexes = new ArrayList<>();

    public Map<String, TreeNode> getMap() {
        return map;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }
}
