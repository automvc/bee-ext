/*
 * Copyright 2020-2023 the original author.All rights reserved.
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

package org.teasoft.beex.ds;

import java.util.Map;

import javax.sql.DataSource;

import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.honey.util.Converter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Kingstar
 * @since  2.1
 */
public class HikariDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> map) {

		if(!map.containsKey("jdbcUrl") && map.containsKey("url")) {
			map.put("jdbcUrl", map.get("url"));
			map.remove("url");
		}
		
		if(!map.containsKey("driverClassName") && map.containsKey("driverName")) {
			map.put("driverClassName", map.get("driverName"));
			map.remove("driverName");
		}
		return new HikariDataSource(new HikariConfig(Converter.map2Prop(map)));
	}

}
