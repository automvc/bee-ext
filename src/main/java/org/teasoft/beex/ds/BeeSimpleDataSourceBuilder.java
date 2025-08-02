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

package org.teasoft.beex.ds;

import java.util.Map;

import javax.sql.DataSource;

import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.bee.osql.exception.ConfigWrongException;
import org.teasoft.honey.jdbc.SimpleDataSource;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.util.StringUtils;

/**
 * bee.db.dbs[i].type=BeeSimpleDs
 * @author Kingstar
 * @since  2.1.8
 */
public class BeeSimpleDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> propertiesMap) {
		DataSource ds = null;
		try {
			
			//2.5.2
			if (!propertiesMap.containsKey("url") && propertiesMap.containsKey("jdbcUrl")) {
				propertiesMap.put("url", propertiesMap.get("jdbcUrl"));
				propertiesMap.remove("jdbcUrl");
			}
			
			String url = propertiesMap.get("url");
			if (StringUtils.isBlank(url)) {
				throw new ConfigWrongException(
						"The url for  BeeSimpleDataSource can not be null!");
			}

			String username = propertiesMap.getOrDefault("username", "");
			String p = propertiesMap.getOrDefault("pass"+"word", "");

			ds = new SimpleDataSource(url, username, p);
			
			Logger.info("[Bee] Using BeeSimpleDataSourceBuilder...");
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

	
}
