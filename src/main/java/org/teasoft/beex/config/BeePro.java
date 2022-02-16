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
 * @author Kingstar
 * @since  1.9
 */
public class BeePro {

	private String loggerType; //v1.8
	
	private String sqlLoggerLevel; //v1.9.8

	private Boolean logDonotPrintLevel;

	private String dateFormat; //v1.7.2   use in DateUtil

	private String sqlKeyWordCase;

	private Boolean notDeleteWholeRecords;

	private Boolean notUpdateWholeRecords;

	private Integer insertBatchSize;

	private Boolean showSQL;
	
	private String lang;

	public String getLoggerType() {
		return loggerType;
	}

	public void setLoggerType(String loggerType) {
		this.loggerType = loggerType;
	}
	
	public String getSqlLoggerLevel() {
		return sqlLoggerLevel;
	}

	public void setSqlLoggerLevel(String sqlLoggerLevel) {
		this.sqlLoggerLevel = sqlLoggerLevel;
	}

	public Boolean getLogDonotPrintLevel() {
		return logDonotPrintLevel;
	}

	public void setLogDonotPrintLevel(Boolean logDonotPrintLevel) {
		this.logDonotPrintLevel = logDonotPrintLevel;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getSqlKeyWordCase() {
		return sqlKeyWordCase;
	}

	public void setSqlKeyWordCase(String sqlKeyWordCase) {
		this.sqlKeyWordCase = sqlKeyWordCase;
	}

	public Boolean getNotDeleteWholeRecords() {
		return notDeleteWholeRecords;
	}

	public void setNotDeleteWholeRecords(Boolean notDeleteWholeRecords) {
		this.notDeleteWholeRecords = notDeleteWholeRecords;
	}

	public Boolean getNotUpdateWholeRecords() {
		return notUpdateWholeRecords;
	}

	public void setNotUpdateWholeRecords(Boolean notUpdateWholeRecords) {
		this.notUpdateWholeRecords = notUpdateWholeRecords;
	}

	public Integer getInsertBatchSize() {
		return insertBatchSize;
	}

	public void setInsertBatchSize(Integer insertBatchSize) {
		this.insertBatchSize = insertBatchSize;
	}

	public Boolean getShowSQL() {
		return showSQL;
	}

	public void setShowSQL(Boolean showSQL) {
		this.showSQL = showSQL;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
