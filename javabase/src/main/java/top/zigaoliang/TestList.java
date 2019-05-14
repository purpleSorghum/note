package top.zigaoliang;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TestList
 * @Author hanlin
 * @Date 2019/5/14 14:15
 **/
public class TestList {

    public static void main(String[] args) {
        List<TestList> list=new ArrayList<TestList>();
        TestList t=new TestList();
        list.add(t);
        list.add(t);
        System.out.println(list.contains(t));
        System.out.println(list.size());
    }
}
