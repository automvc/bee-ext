/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.type;

import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.annotation.customizable.JsonHandler;
import org.teasoft.beex.type.JsonTypeConvert;
import org.teasoft.beex.type.JsonTypeHandler;
import org.teasoft.honey.osql.type.SetParaTypeConverterRegistry;
import org.teasoft.honey.osql.type.TypeHandlerRegistry;

/**
 * @author Kingstar
 * @since  1.11-E
 */
public class JsonDefaultHandler implements JsonHandler {

//	static {
//		init();
//	}

	@SuppressWarnings("unchecked")
	public static void init() {
		TypeHandlerRegistry.register(Json.class, new JsonTypeHandler());
		SetParaTypeConverterRegistry.register(Json.class, new JsonTypeConvert());
	}

}
