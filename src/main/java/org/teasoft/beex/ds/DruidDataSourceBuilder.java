/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.ds;

import java.util.Map;

import javax.sql.DataSource;

import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.honey.osql.core.Logger;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * @author Kingstar
 * @since  2.1
 */
public class DruidDataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> properties) {

		DataSource ds = null;
		try {
			ds = DruidDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

}
