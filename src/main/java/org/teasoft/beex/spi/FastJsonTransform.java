package org.teasoft.beex.spi;

import java.util.List;

import org.teasoft.bee.spi.JsonTransform;
import org.teasoft.beex.json.FastJsonUtil;

/**
 * JsonTransform实现(用fastjson).JsonTransform implement with fastjson.
 * @author Kingstar
 * @since  2.1
 */
public class FastJsonTransform implements JsonTransform {

	private static final long serialVersionUID = 1592803913605L;

	@Override
	public <T> T toEntity(String json, Class<T> clazz) {
		return FastJsonUtil.toEntity(json, clazz);
	}

	@Override
	public String toJson(Object obj) {
		return FastJsonUtil.toJson(obj);
	}

	@Override
	public <T> T toEntity(String json, Class<T> clazz, Class<?> elementClass) {
		return FastJsonUtil.toEntity(json, clazz, elementClass);
	}

	@Override
	public <T> List<T> toEntityList(String json, Class<T> elementClass) {
		return FastJsonUtil.toEntityList(json, elementClass);
	}

}
