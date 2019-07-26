package top.zigaoliang.common;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class JSONArray {
    protected com.alibaba.fastjson.JSONArray jsonArray = null;

    public JSONArray() {
        jsonArray = new com.alibaba.fastjson.JSONArray();
    }

    public JSONArray(int size) {
        jsonArray = new com.alibaba.fastjson.JSONArray(size);
    }

    protected JSONArray(com.alibaba.fastjson.JSONArray array) {
        this.jsonArray = array;
    }

    public JSONArray add(Object object) {
        if (object instanceof JSONObject) object = ((JSONObject) object).json;
        if (object instanceof JSONArray) object = ((JSONArray) object).jsonArray;
        jsonArray.add(object);
        return this;
    }

    public JSONArray add(int index, Object object) {
        if (object instanceof JSONObject) object = ((JSONObject) object).json;
        if (object instanceof JSONArray) object = ((JSONArray) object).jsonArray;
        jsonArray.add(index, object);
        return this;
    }

    public void clear() {
        // this.jsonArray.clear();
        this.jsonArray = new com.alibaba.fastjson.JSONArray();
    }

    public final static JSONArray fromObject(Object object) {
        JSONArray array = new JSONArray(com.alibaba.fastjson.JSONArray.parseArray(object instanceof String ? (String) object : JSON.toJSONString(object)));
        return array;
    }

    public void remove(int index) {
        this.jsonArray.remove(index);
    }

    public Object get(int index) {
        Object object = this.jsonArray.get(index);
        if (object instanceof com.alibaba.fastjson.JSONObject)
            object = new JSONObject((com.alibaba.fastjson.JSONObject) object);
        if (object instanceof com.alibaba.fastjson.JSONArray)
            object = new JSONArray((com.alibaba.fastjson.JSONArray) object);
        return object;
    }

    @Override
    public String toString() {
        return this.jsonArray.toString();
    }

    public int size() {
        return this.jsonArray.size();
    }

    public int getInt(int index) {
        return this.jsonArray.getIntValue(index);
    }

    public String getString(int index) {
        return this.jsonArray.getString(index);
    }

    public double getDouble(int index) {
        return this.jsonArray.getDouble(index);
    }

    public boolean getFloat(int index) {
        return this.jsonArray.getBoolean(index);
    }

    public long getLong(int index) {
        return this.jsonArray.getLong(index);
    }

    public JSONObject getJSONObject(int index) {
        return new JSONObject(this.jsonArray.getJSONObject(index));
    }

    public JSONArray getJSONArray(int index) {
        return new JSONArray(this.jsonArray.getJSONArray(index));
    }

    /*
     * 返回jsonObject数组
     */
    public Object[] toArray() {
        return this.jsonArray.toArray();
    }

    /*
     * 返回jsonObject数组
     */
    public <T> T[] toArray(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(clazz, this.jsonArray.size());
        return this.jsonArray.toArray(array);
    }

    public <T> List<T> toList(Class<T> tclass) {
        return JSON.parseArray(this.jsonArray.toJSONString(), tclass);
    }

    public boolean isEmpty() {
        return jsonArray.isEmpty();
    }

    public void addAll(JSONArray jsonArray2) {
        this.jsonArray.addAll(jsonArray2.jsonArray);
    }

    public void set(int index, Object element) {
        this.jsonArray.set(index, element);
    }

    public void addAll(Collection<? extends Object> cls) {
        this.jsonArray.addAll(cls);
    }

    /**
     * json数组转换为map
     *
     * @param categories
     * @param jsonArray
     * @return
     */
    public static Map<String, Map<String, Double>> jsonArray2map(
            JSONArray categories, JSONArray jsonArray) {
        LinkedHashMap<String, Map<String, Double>> map = new LinkedHashMap<String, Map<String, Double>>();
        Map<String, Double> doMap = null;

        if (categories != null && categories.size() > 0) {
            for (int i = 0; i < categories.size(); i++) {
                doMap = new HashMap<String, Double>(); // 有顺序的map
                for (int j = 0; j < jsonArray.size(); j++) {
                    JSONObject jo = jsonArray.getJSONObject(j);
                    doMap.put(jo.getString("name"), jo.getJSONArray("data")
                            .getDouble(i));
                }
                map.put(categories.getString(i), doMap);
            }
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                JSONArray ja = jo.getJSONArray("data");
                doMap = new HashMap<String, Double>(); // 有顺序的map
                for (int j = 0; j < ja.size(); j++) {
                    JSONArray jr = ja.getJSONArray(j);
                    doMap.put(jr.getString(0), jr.getDouble(1));
                }
                map.put(jo.getString("name"), doMap);
            }
        }
        return map;
    }
}