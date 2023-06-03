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
 * BeeProGenid
 * @author Kingstar
 * @since  1.9
 */
public class BeeProGenid {

	 // prefix= "genid_"
	private Integer workerid;
	private Integer generatorType;
	private Boolean forAllTableLongId;
	private Boolean replaceOldId; 
	private String includesEntityList;
	private String excludesEntityList;
	private Integer startYear; //2.0

	public Integer getWorkerid() {
		return workerid;
	}

	public void setWorkerid(Integer workerid) {
		this.workerid = workerid;
	}

	public Integer getGeneratorType() {
		return generatorType;
	}

	public void setGeneratorType(Integer generatorType) {
		this.generatorType = generatorType;
	}

	public Boolean getForAllTableLongId() {
		return forAllTableLongId;
	}

	public void setForAllTableLongId(Boolean forAllTableLongId) {
		this.forAllTableLongId = forAllTableLongId;
	}
	
	public Boolean getReplaceOldId() {
		return replaceOldId;
	}

	public void setReplaceOldId(Boolean replaceOldId) {
		this.replaceOldId = replaceOldId;
	}

	public String getIncludesEntityList() {
		return includesEntityList;
	}

	public void setIncludesEntityList(String includesEntityList) {
		this.includesEntityList = includesEntityList;
	}

	public String getExcludesEntityList() {
		return excludesEntityList;
	}

	public void setExcludesEntityList(String excludesEntityList) {
		this.excludesEntityList = excludesEntityList;
	}

	public Integer getStartYear() {
		return startYear;
	}

	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
	}

}
