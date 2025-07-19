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
import org.teasoft.bee.osql.exception.ConfigWrongException;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.util.StringUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author Kingstar
 * @since  2.1
 */
public class C3p0DataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> p) {
		ComboPooledDataSource ds = new ComboPooledDataSource();
//		ds.setProperties(p);  //不能用这种
		
		if (!p.containsKey("user") && p.containsKey("username")) {
			p.put("user", p.get("username"));
		}
		if (!p.containsKey("jdbcUrl") && p.containsKey("url")) {
			p.put("jdbcUrl", p.get("url"));
		}
		
//		c3p0  V0.9.5.4
		String jdbcUrl=getString(p,"jdbcUrl");
		String user=getString(p,"user");
		if (StringUtils.isBlank(jdbcUrl) || StringUtils.isBlank(user)) {
			throw new ConfigWrongException("The jdbcUrl and user for c3p0 can not be null!");
		}
		if (!p.containsKey("driverClass")) {
			if(p.containsKey("driverClassName")) p.put("driverClass", p.get("driverClassName"));
			if(p.containsKey("driverName")) p.put("driverClass", p.get("driverName"));
		}
		String tempString;
		if(jdbcUrl!=null) ds.setJdbcUrl(jdbcUrl);
		if(user!=null) ds.setUser(user);
		if((tempString=getString(p,"password"))!=null) ds.setPassword(tempString);
		if((tempString=getString(p,"description"))!=null) ds.setDescription(tempString);
		if((tempString=getString(p,"automaticTestTable"))!=null) ds.setAutomaticTestTable(tempString);
		if((tempString=getString(p,"overrideDefaultUser"))!=null) ds.setOverrideDefaultUser(tempString);
		if((tempString=getString(p,"overrideDefaultPassword"))!=null) ds.setOverrideDefaultPassword(tempString);
		if((tempString=getString(p,"preferredTestQuery"))!=null) ds.setPreferredTestQuery(tempString);
		if((tempString=getString(p,"connectionCustomizerClassName"))!=null) ds.setConnectionCustomizerClassName(tempString);
		if((tempString=getString(p,"factoryClassLocation"))!=null) ds.setFactoryClassLocation(tempString);
		
		try {
			if ((tempString = getString(p, "driverClass")) != null)
				ds.setDriverClass(tempString);
		} catch (Exception e) {
			// ignore
		}
		try {
			if ((tempString = getString(p, "contextClassLoaderSource")) != null)
				ds.setContextClassLoaderSource(tempString);
		} catch (Exception e) {
			// ignore
		}
		try {
			if ((tempString = getString(p, "connectionTesterClassName")) != null)
				ds.setConnectionTesterClassName(tempString);
		} catch (Exception e) {
			// ignore
		}
		try {
			if ((tempString = getString(p, "userOverridesAsString")) != null)
				ds.setUserOverridesAsString(tempString);
		} catch (Exception e) {
			// ignore
		}

		Integer tempInt;
		if((tempInt=getInt(p,"checkoutTimeout"))!=null) ds.setCheckoutTimeout(tempInt);
		if((tempInt=getInt(p,"acquireIncrement"))!=null) ds.setAcquireIncrement(tempInt);
		if((tempInt=getInt(p,"acquireRetryAttempts"))!=null) ds.setAcquireRetryAttempts(tempInt);
		if((tempInt=getInt(p,"acquireRetryDelay"))!=null) ds.setAcquireRetryDelay(tempInt);
		if((tempInt=getInt(p,"idleConnectionTestPeriod"))!=null) ds.setIdleConnectionTestPeriod(tempInt);
		if((tempInt=getInt(p,"initialPoolSize"))!=null) ds.setInitialPoolSize(tempInt);
		if((tempInt=getInt(p,"maxIdleTime"))!=null) ds.setMaxIdleTime(tempInt);
		if((tempInt=getInt(p,"maxPoolSize"))!=null) ds.setMaxPoolSize(tempInt);
		if((tempInt=getInt(p,"maxStatements"))!=null) ds.setMaxStatements(tempInt);
		if((tempInt=getInt(p,"maxStatementsPerConnection"))!=null) ds.setMaxStatementsPerConnection(tempInt);
		if((tempInt=getInt(p,"minPoolSize"))!=null) ds.setMinPoolSize(tempInt);
		if((tempInt=getInt(p,"propertyCycle"))!=null) ds.setPropertyCycle(tempInt);
		if((tempInt=getInt(p,"maxAdministrativeTaskTime"))!=null) ds.setMaxAdministrativeTaskTime(tempInt);
		if((tempInt=getInt(p,"maxIdleTimeExcessConnections"))!=null) ds.setMaxIdleTimeExcessConnections(tempInt);
		if((tempInt=getInt(p,"maxConnectionAge"))!=null) ds.setMaxConnectionAge(tempInt);
		if((tempInt=getInt(p,"unreturnedConnectionTimeout"))!=null) ds.setUnreturnedConnectionTimeout(tempInt);
		if((tempInt=getInt(p,"statementCacheNumDeferredCloseThreads"))!=null) ds.setStatementCacheNumDeferredCloseThreads(tempInt);

		Boolean tempBoolean;
		if((tempBoolean=getBoolean(p,"forceUseNamedDriverClass"))!=null) ds.setForceUseNamedDriverClass(tempBoolean);
		if((tempBoolean=getBoolean(p,"autoCommitOnClose"))!=null) ds.setAutoCommitOnClose(tempBoolean);
		if((tempBoolean=getBoolean(p,"forceIgnoreUnresolvedTransactions"))!=null) ds.setForceIgnoreUnresolvedTransactions(tempBoolean);
		if((tempBoolean=getBoolean(p,"privilegeSpawnedThreads"))!=null) ds.setPrivilegeSpawnedThreads(tempBoolean);
		if((tempBoolean=getBoolean(p,"breakAfterAcquireFailure"))!=null) ds.setBreakAfterAcquireFailure(tempBoolean);
		if((tempBoolean=getBoolean(p,"testConnectionOnCheckout"))!=null) ds.setTestConnectionOnCheckout(tempBoolean);
		if((tempBoolean=getBoolean(p,"testConnectionOnCheckin"))!=null) ds.setTestConnectionOnCheckin(tempBoolean);
		if((tempBoolean=getBoolean(p,"usesTraditionalReflectiveProxies"))!=null) ds.setUsesTraditionalReflectiveProxies(tempBoolean);
		if((tempBoolean=getBoolean(p,"debugUnreturnedConnectionStackTraces"))!=null) ds.setDebugUnreturnedConnectionStackTraces(tempBoolean);
		if((tempBoolean=getBoolean(p,"forceSynchronousCheckins"))!=null) ds.setForceSynchronousCheckins(tempBoolean);

		Logger.info("[Bee] Using C3p0DataSourceBuilder...");
		
		return ds;
	}

	private Integer getInt(Map<String, String> p, String key) {
//		String t=(String)p.get("checkoutTimeout");
		String t = (String) p.get(key);
		if (StringUtils.isBlank(t)) return null;
		try {
			return Integer.parseInt(t.trim());
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return null;
	}

	private String getString(Map<String, String> p, String key) {
		String t = (String) p.get(key);
		if (StringUtils.isBlank(t))
			return null;
		else
			return t;
	}

	private Boolean getBoolean(Map<String, String> p, String key) {
		String t = (String) p.get(key);
		if ("true".equalsIgnoreCase(t)) return Boolean.TRUE;
		if ("false".equalsIgnoreCase(t)) return Boolean.FALSE;

		Boolean b = null;
		return b;
	}

}
