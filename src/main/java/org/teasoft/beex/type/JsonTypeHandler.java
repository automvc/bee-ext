/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teasoft.beex.type;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.teasoft.bee.osql.type.TypeHandler;
import org.teasoft.beex.json.JsonUtil;

/**
 * @author Kingstar
 * @since 1.11-E
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class JsonTypeHandler implements TypeHandler {

	@Override
	public Object process(Class fieldType, Object result) {
		if (result == null) return result;
		if (List.class.isAssignableFrom(fieldType)) {
			Object newOjb[] = (Object[]) result;
			return _process((Field) newOjb[1], newOjb[0]);
		} else {
			return JsonUtil.toEntity((String) result, fieldType);
		}
	}

	private Object _process(Field field, Object result) {
		if (result == null) return result;
		Type gType = field.getGenericType();
		Class<?> elementType = null;
		if (gType instanceof ParameterizedType) {
			ParameterizedType paraType = (ParameterizedType) gType;
			elementType = (Class<?>) paraType.getActualTypeArguments()[0]; // 得到元素的泛型类型
		}
		return JsonUtil.toEntity((String) result, field.getType(), elementType);
	}

}
