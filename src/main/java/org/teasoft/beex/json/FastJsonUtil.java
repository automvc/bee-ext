package org.teasoft.beex.json;


import java.util.List;

import org.teasoft.honey.logging.Logger;

import com.alibaba.fastjson.JSON;

/**
 * Json工具(用fastjson).Json Util with fastjson.
 * @author Kingstar
 * @since  2.1
 */
public class FastJsonUtil {
	
	public static String toJson(Object obj) {
		try {
			if (obj != null && obj.getClass() == String.class) return (String) obj;
			return JSON.toJSONString(obj);
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}
	
	public static <T> T toEntity(String json, Class<T> clazz) {
		try {
			if (clazz != null && clazz == String.class) return (T) json;
			return (T)JSON.parseObject(json, clazz);
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T toEntity(String json, Class<T> clazz, Class elementClass) {
		try {
			if(json==null) return null;
			if (List.class.isAssignableFrom(clazz))
			  return (T)JSON.parseArray(json, elementClass);// 把字符串转换成List<> ok
			else {
				Logger.warn("This method with fastjson,just support List type!");
			}
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}
	
	
	public static <T> List<T> toEntityList(String json, Class<T> elementClass) {
		if (json == null) return null;
		try {
			return JSON.parseArray(json, elementClass);// 把字符串转换成List<> ok
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}
	
}
