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
 * BeeProMultiDS
 * @author Kingstar
 * @since  1.9
 */
public class BeeProMultiDS {

 // prefix= "multiDS_"
	private Boolean enable;
	private Integer type;
	private String defalutDS;
	private String writeDB; //multiDsType=1
	private String readDB; //multiDsType=1
	private Integer rDbRouteWay; //  //multiDsType=1
	private String matchEntityClassPath; //multiDsType=2
	private String matchTable; //multiDsType=2
	//	支持同时使用多种类型数据库的数据源.support different type muli-Ds at same time.
	private Boolean differentDbType;
	private Boolean sharding; //2.0 用于分库分表的分片

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDefalutDS() {
		return defalutDS;
	}

	public void setDefalutDS(String defalutDS) {
		this.defalutDS = defalutDS;
	}

	public String getWriteDB() {
		return writeDB;
	}

	public void setWriteDB(String writeDB) {
		this.writeDB = writeDB;
	}

	public String getReadDB() {
		return readDB;
	}

	public void setReadDB(String readDB) {
		this.readDB = readDB;
	}

	public Integer getrDbRouteWay() {
		return rDbRouteWay;
	}

	public void setrDbRouteWay(Integer rDbRouteWay) {
		this.rDbRouteWay = rDbRouteWay;
	}

	public String getMatchEntityClassPath() {
		return matchEntityClassPath;
	}

	public void setMatchEntityClassPath(String matchEntityClassPath) {
		this.matchEntityClassPath = matchEntityClassPath;
	}

	public String getMatchTable() {
		return matchTable;
	}

	public void setMatchTable(String matchTable) {
		this.matchTable = matchTable;
	}

	public Boolean getDifferentDbType() {
		return differentDbType;
	}

	public void setDifferentDbType(Boolean differentDbType) {
		this.differentDbType = differentDbType;
	}

	public Boolean getSharding() {
		return sharding;
	}

	public void setSharding(Boolean sharding) {
		this.sharding = sharding;
	}
	
}
