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
import org.teasoft.honey.logging.Logger;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * @author Kingstar
 * @since  2.1
 */
public class DruidDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> propertiesMap) {

		DataSource ds = null;
		try {
			//2.4.0
			if (!propertiesMap.containsKey("url") && propertiesMap.containsKey("jdbcUrl")) {
				propertiesMap.put("url", propertiesMap.get("jdbcUrl"));
				propertiesMap.remove("jdbcUrl");
			}
			//2.4.0
			if (!propertiesMap.containsKey("driverName") && propertiesMap.containsKey("driverClassName")) {
				propertiesMap.put("driverName", propertiesMap.get("driverClassName"));
				propertiesMap.remove("driverClassName");
			}
			
			ds = DruidDataSourceFactory.createDataSource(propertiesMap);
			Logger.info("[Bee] Using DruidDataSourceBuilder...");
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

}
