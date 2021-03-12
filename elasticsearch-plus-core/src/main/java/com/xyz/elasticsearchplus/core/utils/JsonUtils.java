package com.xyz.elasticsearchplus.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author sxl
 */
public class JsonUtils {
    public static <T> T jsonToBean(String json, @NonNull Class<T> cls) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, cls);
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> baseType, Type... nestingTypes) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        return JSON.parseObject(jsonStr, buildType(baseType, nestingTypes));
    }

    public static <T> List<T> jsonToList(String json, @NonNull Class<T> cls) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(json, cls);
    }

    public static String beanToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractStrByPath(String contentJson, String path) {
        if (StringUtils.isBlank(contentJson)) {
            return null;
        }
        Object valueObj = JSONPath.read(contentJson, path);
        if (null == valueObj) {
            return null;
        }
        return String.valueOf(valueObj);
    }

    public static <T> List<T> extraListByPath(String contentJson, String path, Class<T> tClass) {
        Object data = JSONPath.read(contentJson, path);
        if (data == null) {
            return Collections.emptyList();
        }
        return JSONObject.parseArray(JsonUtils.beanToJson(data), tClass);
    }

    public static <F, T> T convert(F f, Class<T> clazz) {
        if (f == null) {
            return null;
        }
        return jsonToBean(beanToJson(f), clazz);
    }

    public static <F, T> List<T> convertList(List<F> f, Class<T> clazz) {
        if (CollectionUtils.isEmpty(f)) {
            return Collections.emptyList();
        }
        return jsonToBean(beanToJson(f), List.class, clazz);
    }

    private static <T> Type buildType(Class<T> baseType, Type... nestingTypes) {
        if (nestingTypes == null || nestingTypes.length == 0) {
            return baseType;
        }

        ParameterizedTypeImpl beforeType = null;
        for (int i = nestingTypes.length - 1; i > 0; i--) {
            beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? nestingTypes[i] : beforeType}, null,
                    nestingTypes[i - 1]);
        }
        beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? nestingTypes[0] : beforeType},
                null, baseType);
        return beforeType;
    }
}