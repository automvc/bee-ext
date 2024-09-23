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

import java.sql.SQLException;

import org.postgresql.util.PGobject;
import org.teasoft.bee.osql.type.SetParaTypeConvert;
import org.teasoft.honey.osql.core.Logger;

/**
 * Just for PostgreSQL json type.
 * @author Kingstar
 * @since  2.4.0
 */
@SuppressWarnings("rawtypes")
public class PgSQLJsonTypeConvert implements SetParaTypeConvert {
	
	private String type;
	
	public PgSQLJsonTypeConvert(){
		this.type="json";
	}
	
	PgSQLJsonTypeConvert(String type){
		this.type=type;
	}

	@Override
	public Object convert(Object value) {
		if (value != null && value.getClass() != String.class) {
			Logger.warn("PgSQLJsonTypeConvert convert(Object value) just support String type parameter");
		}

		try {
			PGobject jsonObject = new PGobject();
			jsonObject.setType(type);
			jsonObject.setValue((String) value);
			return jsonObject;
		} catch (SQLException e) {
			Logger.warn("Have exception in PgSQLJsonTypeConvert: " + e.getMessage());
		}

		return value;
	}

}
