package top.zigaoliang.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基金代码名称字典项
 * Created by root on 18-11-21.
 *
 * @author zaj
 */

@AllArgsConstructor
@NoArgsConstructor
public class FundCodeName {

    private String code;  //代码
    private String name;  //名称
    private String sname;
    private String link;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}




