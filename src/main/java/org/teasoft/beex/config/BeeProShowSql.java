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

/**
 * BeeProShowSql
 * @author Kingstar
 * @since  1.9
 */
public class BeeProShowSql {

	// prefix= "showSql_"
	private Boolean showType;

	private Boolean showExecutableSql;
	private Boolean sqlFormat; //2.1.6

	private Boolean donotPrintCurrentDate;

	public Boolean getShowType() {
		return showType;
	}

	public void setShowType(Boolean showType) {
		this.showType = showType;
	}

	public Boolean getShowExecutableSql() {
		return showExecutableSql;
	}

	public void setShowExecutableSql(Boolean showExecutableSql) {
		this.showExecutableSql = showExecutableSql;
	}
	
	public Boolean getSqlFormat() {
		return sqlFormat;
	}

	public void setSqlFormat(Boolean sqlFormat) {
		this.sqlFormat = sqlFormat;
	}

	public Boolean getDonotPrintCurrentDate() {
		return donotPrintCurrentDate;
	}

	public void setDonotPrintCurrentDate(Boolean donotPrintCurrentDate) {
		this.donotPrintCurrentDate = donotPrintCurrentDate;
	}

}
