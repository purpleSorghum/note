package top.zigaoliang.conf.Region;


/**
 *  村
 */

public class Village extends RegionBase{
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
