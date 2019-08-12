package top.zigaoliang;

import org.junit.Test;

import java.lang.annotation.Repeatable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName javabase
 * @Author hanlin
 * @Date 2019/8/7 17:31
 **/
public class javabase {
    @Test
    public void base(){
        int a=3;
        System.out.println(Math.round((float)5/a));
    }

    @Test
    public void testListSub(){
        List<Integer> list = new ArrayList<>();
        for(int i =0 ;i <10;i++){
            list.add(i);
        }
        List subList = list.subList(0,list.size());
        int size = subList.size();
        System.out.println(list);
        System.out.println(subList);
        System.out.println(size);
    }

    @Test
    public void testListAddAll(){
        List<Integer> list = new ArrayList<>();
        for(int i =0 ;i <10;i++){
            list.add(i);
        }
        List<Integer> rsList = new ArrayList<>();
        rsList.addAll(list);
        rsList.addAll(list);
        System.out.println(rsList);
    }

    @Test
    public void testListAddNull(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(null);
        list.add(null);
        list.add(3);

        System.out.println(list);
    }
}
