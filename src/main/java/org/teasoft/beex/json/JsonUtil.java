/*
 * Copyright 2016-2022 the original author.All rights reserved.
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

package org.teasoft.beex.json;

import org.teasoft.honey.osql.core.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json工具.Json Util.
 * @author Kingstar
 * @since  1.11
 */
public class JsonUtil {

	public static <T> T toEntity(String json, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}

	public static String toJson(Object obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			Logger.warn(e.getMessage(), e);
		}
		return null;
	}

}
