package top.zigaoliang.conf.Region;


/**
 * 街
 */
public class Street extends RegionBase{
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
