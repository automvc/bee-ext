/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.type;

import org.teasoft.bee.osql.type.SetParaTypeConvert;
import org.teasoft.beex.json.JsonUtil;

/**
 * @author Kingstar
 * @since  1.11-E
 */
@SuppressWarnings("rawtypes")
public class JsonTypeConvert implements SetParaTypeConvert {

	@Override
	public Object convert(Object value) {
		return JsonUtil.toJson(value);
	}

}
