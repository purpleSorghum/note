package top.zigaoliang.conf.Region;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;



public class Provice extends RegionBase{
    public  String code;  //编号  包括父级的编号 如：4213

    protected String currentCode;  //当前节点的编号

    protected String name;      //地区名称 如：广西壮族自治区

    protected String parentCode;  //父级地区编号 42

    protected String sname;     //名称简写 如：广西

    private String type;  // "省" 或 "市"

    private List<City> cityList = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
