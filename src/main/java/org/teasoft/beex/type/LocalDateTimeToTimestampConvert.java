/*
 * Copyright 2022-2024 the original author.All rights reserved.
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

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.teasoft.bee.osql.type.SetParaTypeConvert;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class LocalDateTimeToTimestampConvert<T> implements SetParaTypeConvert<LocalDateTime> {

	@Override
	public Object convert(LocalDateTime localDateTime) {
		return Timestamp.valueOf(localDateTime);
	}
}
