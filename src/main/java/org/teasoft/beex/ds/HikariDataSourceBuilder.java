/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.ds;

import java.util.Map;
import java.util.Properties;

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
//		System.err.println(map);
		return new HikariDataSource(new HikariConfig(Converter.map2Prop(map)));
	}

}
