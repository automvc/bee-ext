package org.teasoft.beex.json;

import java.util.List;

import org.teasoft.bee.spi.JsonTransform;
import org.teasoft.honey.spi.SpiInstanceFactory;

/**
 * Json工具,可定义使用的Json组件.Json Util.
 * 该工具类,在2.0及之前,只支持使用jackson.
 * @author Kingstar
 * @since  2.1
 */
public class JsonUtil {

	private static JsonTransform JSON = SpiInstanceFactory.getJsonTransform();

	public static String toJson(Object obj) {
		return JSON.toJson(obj);
	}

	public static <T> T toEntity(String json, Class<T> clazz) {
		return JSON.toEntity(json, clazz);
	}

	/**
	 * 
	 * @param json
	 * @param clazz eg:List.class
	 * @param elementClass 元素的类型
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T toEntity(String json, Class<T> clazz, Class elementClass) {
		return JSON.toEntity(json, clazz, elementClass);
	}

	public static <T> List<T> toEntityList(String json, Class<T> elementClass) {
		return JSON.toEntityList(json, elementClass);
	}

}
