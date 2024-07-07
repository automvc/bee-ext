/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.type;

import java.time.LocalDateTime;

import org.teasoft.bee.osql.type.TypeHandler;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class LocalDateTimeTypeHandler<T> implements TypeHandler<LocalDateTime> {

	@Override
	public LocalDateTime process(Class<LocalDateTime> fieldType, Object result) {
		if (result == null) return null;
//		System.out.println(result.getClass().getName());
		
		if (LocalDateTime.class.isAssignableFrom(fieldType)) {
			LocalDateTime t = LocalDateTimeUtil.toLocalDateTime(result);
			if (t != null) return t;
		}

		return (LocalDateTime) result;
	}

}
