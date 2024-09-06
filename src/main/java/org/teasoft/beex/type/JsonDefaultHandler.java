/*
 * Copyright 2016-2023 the original author.All rights reserved.
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

import org.teasoft.bee.osql.DatabaseConst;
import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.annotation.customizable.JsonHandler;
import org.teasoft.bee.osql.type.PostgreSQLJsonString;
import org.teasoft.bee.osql.type.PostgreSQLJsonbString2;
import org.teasoft.honey.osql.type.SetParaTypeConverterRegistry;
import org.teasoft.honey.osql.type.TypeHandlerRegistry;

/**
 * @author Kingstar
 * @since  1.11-E
 */
public class JsonDefaultHandler implements JsonHandler {

	@SuppressWarnings("unchecked")
	public static void init() {
		TypeHandlerRegistry.register(Json.class, new JsonTypeHandler());
		SetParaTypeConverterRegistry.register(Json.class, new JsonTypeConvert());

		SetParaTypeConverterRegistry.register(PostgreSQLJsonString.class, new PgSQLJsonTypeConvert(), DatabaseConst.PostgreSQL);
		SetParaTypeConverterRegistry.register(PostgreSQLJsonbString2.class, new PgSQLJsonbTypeConvert2(), DatabaseConst.PostgreSQL);
	}

}
