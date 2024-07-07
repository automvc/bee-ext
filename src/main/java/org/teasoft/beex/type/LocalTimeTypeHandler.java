/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.type;

import java.time.LocalTime;

import org.teasoft.bee.osql.type.TypeHandler;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class LocalTimeTypeHandler<T> implements TypeHandler<LocalTime> {

	@Override
	public LocalTime process(Class<LocalTime> fieldType, Object result) {
		if (result == null) return null;
		if (LocalTime.class.isAssignableFrom(fieldType)) {
			java.time.LocalDateTime t = LocalDateTimeUtil.toLocalDateTime(result);
			if (t != null) return t.toLocalTime();
		}

		return (LocalTime) result;
	}

}
