/*
 * Copyright 2016-2024 the original author.All rights reserved.
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

package org.teasoft.beex.spi;

import java.util.HashMap;
import java.util.Map;

import org.teasoft.bee.osql.DatabaseConst;
import org.teasoft.bee.spi.SqlFormat;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.util.StringUtils;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.SqlFormatter.Formatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;

/**
 * @author Kingstar
 * @since  2.1.7
 */
public class BeeSqlFormatter implements SqlFormat {

	private static final long serialVersionUID = 1592803913618L;
	
	private static Map<String, Formatter> cacheFormatter;
	private static volatile boolean isFirst = true;
	private static final byte lock[] = new byte[0];

	private static boolean isOneDs = true;

	private static Formatter singleFormatter;

	public String format(String sql) {
		if (singleFormatter != null) {
			return singleFormatter.format(sql);
		}

		String dbName = "";
		try {
			dbName = HoneyConfig.getHoneyConfig().getDbName();
		} catch (Exception e) {
			// ignore
		}

		return getFormatter(dbName).format(sql);
	}

	private Formatter getFormatter(String dbName) {
		Formatter fm;
		if (isFirst) {
			synchronized (lock) {
				if (isFirst) {
					if (isOneDs) {
						fm = _getFormatter(dbName);
						singleFormatter = fm;
						isFirst = false;
						return fm;
					} else {
						cacheFormatter = new HashMap<>();
						isFirst = false;
					}
				}
			} // syn
		}
		fm = cacheFormatter.get(dbName);
		if (fm != null) return fm;
		fm = _getFormatter(dbName);
		cacheFormatter.put(dbName, fm);
		return fm;
	}

	private Formatter _getFormatter(String dbName) {
		Formatter fm;
		try {
			if (StringUtils.isBlank(dbName))
				fm = SqlFormatter.standard();
			else
				fm = _getFormatter0(dbName);
		} catch (Exception e) {
			fm = SqlFormatter.standard();
		}
		return fm;
	}

	private Formatter _getFormatter0(String dbName) {
		Formatter fm;
		if (DatabaseConst.MYSQL.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.MySql);
		else if (DatabaseConst.MariaDB.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.MariaDb);
		else if (DatabaseConst.PostgreSQL.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.PostgreSql);
		else if (DatabaseConst.ORACLE.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.PlSql);
		else if (DatabaseConst.SQLSERVER.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.TSql);
		else if (DatabaseConst.DB2.equalsIgnoreCase(dbName))
			fm = SqlFormatter.of(Dialect.Db2);
		else
			fm = SqlFormatter.standard();

		return fm;
	}

//	sql - Standard SQL
//	mariadb - MariaDB
//	mysql - MySQL
//	postgresql - PostgreSQL
//	db2 - IBM DB2
//	plsql - Oracle PL/SQL
//	n1ql - Couchbase N1QL
//	redshift - Amazon Redshift
//	spark - Spark
//	tsql - SQL Server Transact-SQL

}
