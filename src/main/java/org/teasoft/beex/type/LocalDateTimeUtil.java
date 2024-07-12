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

//import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.teasoft.honey.osql.util.DateUtil;

/**
 * @author Kingstar
 * @since  2.4.0
 */
class LocalDateTimeUtil {
	
	public static LocalDateTime toLocalDateTime(Object result) {
		if (result instanceof Timestamp) {
			return toLocalDateTime((Timestamp) result);
		} else if (result.getClass().equals(String.class)) {
			try {
				Long timeNum = Long.parseLong((String) result); // 存的是数字
				return toLocalDateTime(new Timestamp(timeNum));
			} catch (NumberFormatException e) {
			}
			return toLocalDateTime(DateUtil.toTimestamp((String) result));
		}
//		else if("oracle.sql.TIMESTAMP".equals(result.getClass().getName())) {
//			return toLocalDateTime(getOracleTimestamp(result));
//		}
		
		return null;
	}
	
	private static LocalDateTime toLocalDateTime(Timestamp t) {
		return t.toLocalDateTime();
	}
	
//	private static Timestamp getOracleTimestamp(Object value) {
//	    try {
//	        Class clz = value.getClass();
//	        Method m = clz.getMethod("timestampValue");
//	        //m = clz.getMethod("timeValue", null); 时间类型
//	        //m = clz.getMethod("dateValue", null); 日期类型
//	        return (Timestamp) m.invoke(value);
//	 
//	    } catch (Exception e) {
//	        return null;
//	    }
//	}

}
