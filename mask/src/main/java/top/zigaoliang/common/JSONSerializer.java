package top.zigaoliang.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeFilter;

import java.util.List;

import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

public class JSONSerializer {
    public static String serialize(Object object) {
        return JSON.toJSONString(object, DisableCircularReferenceDetect);
    }

    public static String serialize(Object object, PropertyPreFilter filter) {
        return JSON.toJSONString(object, filter, DisableCircularReferenceDetect);
    }

    public static String serialize(Object object, SerializeFilter... filters) {
        return JSON.toJSONString(object, filters, DisableCircularReferenceDetect);
    }

    public static String serializeRef(Object object, PropertyPreFilter filter) {
        return JSON.toJSONString(object, filter);
    }

    public static <T> T deserialize(Class<T> clazz, String text) {
        return JSON.parseObject(text, clazz);
    }

    public static <T> List<T> deserializeArray(Class<T> clazz, String text) {
        return JSON.parseArray(text, clazz);
    }
}
