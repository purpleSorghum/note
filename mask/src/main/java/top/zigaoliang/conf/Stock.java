package top.zigaoliang.conf;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author yangying
 * @date 18-11-26
 */

@AllArgsConstructor
@NoArgsConstructor
public class Stock {
    /**
     * 代码
     */
    private String code;
    /**
     * 名称
     */
    private String name;

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
}
