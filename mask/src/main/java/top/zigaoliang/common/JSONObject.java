package top.zigaoliang.common;

import com.alibaba.fastjson.JSON;

import java.util.Collection;
import java.util.Map;

/**
 * 目前项目中使用的json-lib的效率是最慢的，所有先提取出常用的方法，一边日后替换其他的json库
 *
 * @author zh
 */
public class JSONObject {
    protected com.alibaba.fastjson.JSONObject json = null;

    public JSONObject() {
        json = new com.alibaba.fastjson.JSONObject();
    }

    public JSONObject(int size) {
        json = new com.alibaba.fastjson.JSONObject(size);
    }

    protected JSONObject(com.alibaba.fastjson.JSONObject object) {
        this.json = object;
    }

    public JSONObject put(String key, Object value) {
        if (value instanceof JSONArray)
            value = ((JSONArray) value).jsonArray;
        if (value instanceof JSONObject)
            value = ((JSONObject) value).json;
        json.put(key, value);
        return this;
    }

    public void clear() {
        // json.clear();
        json = new com.alibaba.fastjson.JSONObject();
    }

    @Override
    public String toString() {
        return json.toString();
    }

    public final static JSONObject fromObject(Object o) {
        if (o instanceof String) {
            return new JSONObject(
                    com.alibaba.fastjson.JSONObject.parseObject((String) o));
        } else if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        JSONObject object = new JSONObject(
                com.alibaba.fastjson.JSONObject.parseObject(JSON
                        .toJSONString(o)));
        return object;
    }


    public Object get(Object object) {
        Object ob = this.json.get(object);
        if (ob instanceof com.alibaba.fastjson.JSONArray)
            return new JSONArray((com.alibaba.fastjson.JSONArray) ob);
        if (ob instanceof com.alibaba.fastjson.JSONObject)
            return new JSONObject((com.alibaba.fastjson.JSONObject) ob);
        return ob;
    }

    public int getInt(String key) {
        return this.json.getIntValue(key);
    }

    public String getString(String key) {
        if (!this.json.containsKey(key))
            return null;
        return this.json.getString(key);
    }

    public double getDouble(String key) {
        return this.json.getDouble(key);
    }

    public boolean getBoolean(String key) {
        Object object = this.json.get(key);
        if (object == null)
            return false;
        return this.json.getBoolean(key);
    }

    public long getLong(String key) {
        return this.json.getLong(key);
    }

    public JSONObject getJSONObject(String key) {
        return new JSONObject(this.json.getJSONObject(key));
    }

    public JSONArray getJSONArray(String key) {
        if (this.json.get(key) == null)
            return new JSONArray();
        return new JSONArray(this.json.getJSONArray(key));
    }

    public <T> T toBean(Class<T> tclass) {
        return com.alibaba.fastjson.JSONObject.toJavaObject(this.json, tclass);
    }

    public static final String toEayuiTable(long total, Collection<?> rows) {
        JSONObject object = new JSONObject();
        object.put("total", total);
        object.put("rows", JSONArray.fromObject(rows));
        return object.toString();
    }


    public boolean isEmpty() {
        return this.json.isEmpty();
    }

    public boolean isNullObject() {
        return this.json == null ? true : false;
    }

    public boolean has(String key) {
        return this.json.containsKey(key);
    }

    public Map<String, Object> getMap() {
        return this.json;
    }

    public boolean containsKey(String string) {
        return this.json.containsKey(string);
    }
}
