/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.config;

import org.teasoft.bee.osql.annotation.SysValue;

/**
 * @author Kingstar
 * @since  1.11
 */
public class BeeProCacheRedis {

	//  prefix= "cacheRedis_"
	public String host;

	public Integer port;

	public String password;

	public Integer connectionTimeout;

	public Integer soTimeout;

	public Integer database;

	public String clientName;

	public Boolean ssl;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Integer getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(Integer soTimeout) {
		this.soTimeout = soTimeout;
	}

	public Integer getDatabase() {
		return database;
	}

	public void setDatabase(Integer database) {
		this.database = database;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Boolean getSsl() {
		return ssl;
	}

	public void setSsl(Boolean ssl) {
		this.ssl = ssl;
	}

}
