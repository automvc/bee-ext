package org.teasoft.beex.json;


import java.util.List;

import org.teasoft.honey.osql.core.Logger;

import com.alibaba.fastjson.JSONObject;

/**
 * Json工具(用fastjson).Json Util with fastjson.
 * @author Kingstar
 * @since  2.1
 */
public class FastJsonUtil {
	
	public static String toJson(Object obj) {
		try {
			return JSONObject.toJSONString(obj);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static <T> T toEntity(String json, Class<T> clazz) {
		try {
			return (T)JSONObject.parseObject(json, clazz);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toEntity(String json, Class<T> clazz, Class elementClass) {
		try {
			if(json==null) return null;
			if (List.class.isAssignableFrom(clazz))
			  return (T)JSONObject.parseArray(json, elementClass);// 把字符串转换成List<> ok
			else {
				Logger.warn("This method with fastjson,just support List type!");
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public static <T> List<T> toEntityList(String json, Class<T> elementClass) {
		if (json == null) return null;
		try {
			List<T> list = JSONObject.parseArray(json, elementClass);// 把字符串转换成List<> ok
			return list;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
}
