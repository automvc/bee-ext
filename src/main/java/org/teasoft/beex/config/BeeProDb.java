/*
 * Copyright 2016-2021 the original author.All rights reserved.
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

package org.teasoft.beex.config;

import java.util.List;
import java.util.Map;

/**
 * BeeProDb
 * @author Kingstar
 * @since  1.9
 */
public class BeeProDb {

	private String dbName;
	private String driverName;
	private String url;
	private String username;
	private String password;
	private String schemaName;
	
	private Boolean jndiType;
	private String jndiName;
	
	private Boolean pagingWithLimitOffset;
	
//    private List<Map<String,String>> dbs; //V2.1 配置多个数据源, 属性值已具体工具对应
    
	private Map<String, Map<String, String>> dbs; // V2.1.10
    
	private Map<String, Map<String, String>> sharding; // 2.4.0
    
    private Boolean extendFirst;//V2.1

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public Boolean getJndiType() {
		return jndiType;
	}

	public void setJndiType(Boolean jndiType) {
		this.jndiType = jndiType;
	}
	
	public Boolean getExtendFirst() {
		return extendFirst;
	}

	public void setExtendFirst(Boolean extendFirst) {
		this.extendFirst = extendFirst;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public Boolean getPagingWithLimitOffset() {
		return pagingWithLimitOffset;
	}

	public void setPagingWithLimitOffset(Boolean pagingWithLimitOffset) {
		this.pagingWithLimitOffset = pagingWithLimitOffset;
	}

	public Map<String, Map<String, String>> getDbs() {
		return dbs;
	}

	public void setDbs(Map<String, Map<String, String>> dbs) {
		this.dbs = dbs;
	}

	public Map<String, Map<String, String>> getSharding() {
		return sharding;
	}

	public void setSharding(Map<String, Map<String, String>> sharding) {
		this.sharding = sharding;
	}
	
}
