/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.ds;

import java.util.Map;

import javax.sql.DataSource;

import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.bee.osql.exception.ConfigWrongException;
import org.teasoft.honey.jdbc.SimpleDataSource;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.util.StringUtils;

/**
 * bee.db.dbs[i].type=BeeSimpleDs
 * @author Kingstar
 * @since  2.1.8
 */
public class BeeSimpleDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> properties) {
		DataSource ds = null;
		try {
			String url = properties.get("url");
			if (StringUtils.isBlank(url)) {
				throw new ConfigWrongException(
						"The url for  BeeSimpleDataSource can not be null!");
			}

			String username = properties.getOrDefault("username", "");
			String p = properties.getOrDefault("password", "");

			ds = new SimpleDataSource(url, username, p);
			
			Logger.info("[Bee] Using BeeSimpleDataSourceBuilder...");
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

	
}
