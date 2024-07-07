/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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
