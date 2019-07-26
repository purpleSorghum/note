package top.zigaoliang.conf.Region;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *  县的集合
 */
public class County extends RegionBase {
    public List<Town> townList = new ArrayList<>();

    public List<Town> getTownList() {
        return townList;
    }

    public void setTownList(List<Town> townList) {
        this.townList = townList;
    }
}
