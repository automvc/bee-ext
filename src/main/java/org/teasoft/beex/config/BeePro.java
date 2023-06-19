/*
 * Copyright 2016-2023 the original author.All rights reserved.
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
 * BeePro
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
	
	private Boolean notCatchModifyDuplicateException;
	
	private Boolean notShowModifyDuplicateException;
	
	private Integer insertBatchSize;

	private Boolean showSQL;
	private Boolean showShardingSQL;//2.0
	
	private String lang;
	
	//2.1.6
	private Boolean openDefineColumn;
	private Boolean openFieldTypeHandler;
	private Boolean closeDefaultParaResultRegistry;
	private String systemLoggerLevel;

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
	
	public Boolean getNotCatchModifyDuplicateException() {
		return notCatchModifyDuplicateException;
	}

	public void setNotCatchModifyDuplicateException(Boolean notCatchModifyDuplicateException) {
		this.notCatchModifyDuplicateException = notCatchModifyDuplicateException;
	}

	public Boolean getNotShowModifyDuplicateException() {
		return notShowModifyDuplicateException;
	}

	public void setNotShowModifyDuplicateException(Boolean notShowModifyDuplicateException) {
		this.notShowModifyDuplicateException = notShowModifyDuplicateException;
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
	
	public Boolean getShowShardingSQL() {
		return showShardingSQL;
	}

	public void setShowShardingSQL(Boolean showShardingSQL) {
		this.showShardingSQL = showShardingSQL;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Boolean getOpenDefineColumn() {
		return openDefineColumn;
	}

	public void setOpenDefineColumn(Boolean openDefineColumn) {
		this.openDefineColumn = openDefineColumn;
	}

	public Boolean getOpenFieldTypeHandler() {
		return openFieldTypeHandler;
	}

	public void setOpenFieldTypeHandler(Boolean openFieldTypeHandler) {
		this.openFieldTypeHandler = openFieldTypeHandler;
	}

	public Boolean getCloseDefaultParaResultRegistry() {
		return closeDefaultParaResultRegistry;
	}

	public void setCloseDefaultParaResultRegistry(Boolean closeDefaultParaResultRegistry) {
		this.closeDefaultParaResultRegistry = closeDefaultParaResultRegistry;
	}

	public String getSystemLoggerLevel() {
		return systemLoggerLevel;
	}

	public void setSystemLoggerLevel(String systemLoggerLevel) {
		this.systemLoggerLevel = systemLoggerLevel;
	}
	
}
