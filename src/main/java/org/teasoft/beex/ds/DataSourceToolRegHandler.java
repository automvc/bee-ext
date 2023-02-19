/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.ds;

import org.teasoft.bee.ds.DataSourceBuilderFactory;

/**
 * @author Kingstar
 * @since  2.1
 */
public class DataSourceToolRegHandler {
	
	static {
		init();
	}

	public static void init() {
		DataSourceBuilderFactory.register("druid", new DruidDataSourceBuilder());
		DataSourceBuilderFactory.register("Hikari", new HikariDataSourceBuilder());
	}

}
