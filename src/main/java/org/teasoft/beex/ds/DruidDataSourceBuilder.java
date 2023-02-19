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
			
//	        map.put(DruidDataSourceFactory.PROP_INITIALSIZE, "10"); //initialSize
//	        // 最小连接池数量
//	        map.put(DruidDataSourceFactory.PROP_MINIDLE, "10"); //minIdle
//	        // 最大连接池数量
//	        map.put(DruidDataSourceFactory.PROP_MAXACTIVE, "50");//maxActive
//	        // 获取连接时最大等待时间，单位毫秒
//	        map.put(DruidDataSourceFactory.PROP_MAXWAIT, "60000");//maxWait
//	        // 检测连接的间隔时间，单位毫秒
//	        map.put(DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS, "60000");//timeBetweenEvictionRunsMillis
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

}
