/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.type;

import java.time.LocalDate;

import org.teasoft.bee.osql.type.TypeHandler;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class LocalDateTypeHandler<T> implements TypeHandler<LocalDate> {

	@Override
	public LocalDate process(Class<LocalDate> fieldType, Object result) {
		if (result == null) return null;
		if (LocalDate.class.isAssignableFrom(fieldType)) {
			java.time.LocalDateTime t = LocalDateTimeUtil.toLocalDateTime(result);
			if (t != null) return t.toLocalDate();
		}

		return (LocalDate) result;
	}

}
