/*
 * Copyright 2020-2022 the original author.All rights reserved.
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

package org.teasoft.beex.spi;

import java.util.List;

import org.teasoft.bee.spi.JsonTransform;
import org.teasoft.beex.json.JsonUtil;

/**
 * default is jackson.
 * 
 * @author AiTeaSoft
 * @since 2.0
 */
public class JsonTransformDefault implements JsonTransform {

	private static final long serialVersionUID = 1592803913604L;

	@Override
	public <T> T toEntity(String json, Class<T> clazz) {
		return JsonUtil.toEntity(json, clazz);
	}

	@Override
	public String toJson(Object obj) {
		return JsonUtil.toJson(obj);
	}

	@Override
	public <T> T toEntity(String json, Class<T> clazz, Class<?> elementClass) {
		return JsonUtil.toEntity(json, clazz, elementClass);
	}

	@Override
	public <T> List<T> toEntityList(String json, Class<T> elementClass) {
		return JsonUtil.toEntityList(json, elementClass);
	}
	
}
