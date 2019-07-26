package top.zigaoliang.util;

import org.apache.log4j.Logger;
import top.zigaoliang.common.FileHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapUtil {
    Logger log = Logger.getLogger(this.getClass().getSimpleName());

    /**
     * 是否存在该key
     * @param key
     * @return
     */
    public static boolean containsKey(Map<String, Integer> indexMap, String key){
        if (indexMap == null){
            return  false;
        }
        return indexMap.containsKey(key);
    }
    /**
     *
     * @param idx
     * @return
     */
    public static String getDictName(List<String> stringList, int idx){
        if (stringList == null){
            return  null;
        }
        return stringList.get(idx);
    }

    /**
     *
     * @param indexMap
     * @param key
     * @return
     */
    public static int getMapValue(Map<String, Integer> indexMap, String key){
        if (indexMap == null){
            return -1;
        }
        if (indexMap.containsKey(key)) {
            return indexMap.get(key);
        }
        return -1;
    }

    /**
     * 返回String类型的map 和list
     * @param objects
     * @return
     */
    public static IndexMapList convertToIndexMap(Object[] objects) {
        Map<String, Integer> indexMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (Object value : objects){
            if(value != null) {
                list.add(value.toString());
                indexMap.put(value.toString(), list.size() - 1);
            }
        }
        IndexMapList indexMapList = new IndexMapList();
        indexMapList.setList(list);
        indexMapList.setMap(indexMap);
        return indexMapList;
    }

    public static IndexMapList convertToIndexMap(int[] objects) {
        String[] arr = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            arr[i] = Integer.toString(objects[i]);
        }
        return convertToIndexMap(arr);
    }



    /**
     * 返回String类型的map 和list
     * @param filePath
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static IndexMapList convertToIndexMap(String filePath) {
        Map<String, Integer> indexMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<String> stringList = FileHelper.readSource(filePath, String.class);
        for (String value : stringList){
            list.add(value);
            indexMap.put(value, list.size() -1);
        }
        IndexMapList indexMapList = new IndexMapList();
        indexMapList.setList(list);
        indexMapList.setMap(indexMap);
        return indexMapList;
    }
    /**
     * 返回指定类型的map 和list
     * @param filePath 路径
     * @param clazz 类型
     * @param fieldName 属性名
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static IndexMapList convertToIndexMap(String filePath, Class clazz, String fieldName){
        IndexMapList indexMapList = new IndexMapList();
        try {
            Map<String, Integer> indexMap = new HashMap<>();
            List<String> list = new ArrayList<>();
            fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            List<Object> clazzList = FileHelper.readSource(filePath, clazz);
            Method getm = clazz.getMethod("get" + fieldName);
            for (Object t : clazzList){
                String value = getm.invoke(t, new Object[]{}).toString();
                list.add(value);
                indexMap.put(value, list.size() -1);
            }
            indexMapList.setList(list);
            indexMapList.setMap(indexMap);
        } catch (Exception e){
            e.printStackTrace();
        }
        return indexMapList;
    }
}
