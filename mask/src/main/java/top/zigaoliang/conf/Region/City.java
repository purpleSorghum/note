package top.zigaoliang.conf.Region;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 市的集合
 */
@Data
public class City extends RegionBase {

    public List<County> countyList = new ArrayList<>();

    public List<County> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<County> countyList) {
        this.countyList = countyList;
    }
}