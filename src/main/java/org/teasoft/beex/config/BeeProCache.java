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
 * BeeProCache
 * @author Kingstar
 * @since  1.9
 */
public class BeeProCache {

	 // prefix= "cache_"
	private Integer timeout; //缓存保存时间(毫秒 ms)

	private Integer mapSize; //缓存集数据量大小

	private Double startDeleteRate; //when timeout use

	private Double fullUsedRate; //when add element in cache use

	private Double fullClearRate; //when add element in cache use

	private Boolean keyUseMD5;

	private Boolean nocache;
	
	private Integer prototype;

	private Integer workResultSetSize;

	private String never;

	private String forever;

	private String modifySyn;
	
	//V1.17.21
	private Boolean useLevelTwo; 
	private Boolean levelOneTolevelTwo; 
	private Integer levelTwoTimeout; //二级缓存保存时间(秒 second)
	private String levelTwoEntityList;

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMapSize() {
		return mapSize;
	}

	public void setMapSize(Integer mapSize) {
		this.mapSize = mapSize;
	}

	public Double getStartDeleteRate() {
		return startDeleteRate;
	}

	public void setStartDeleteRate(Double startDeleteRate) {
		this.startDeleteRate = startDeleteRate;
	}

	public Double getFullUsedRate() {
		return fullUsedRate;
	}

	public void setFullUsedRate(Double fullUsedRate) {
		this.fullUsedRate = fullUsedRate;
	}

	public Double getFullClearRate() {
		return fullClearRate;
	}

	public void setFullClearRate(Double fullClearRate) {
		this.fullClearRate = fullClearRate;
	}

	public Boolean getKeyUseMD5() {
		return keyUseMD5;
	}

	public void setKeyUseMD5(Boolean keyUseMD5) {
		this.keyUseMD5 = keyUseMD5;
	}

	public Boolean getNocache() {
		return nocache;
	}

	public void setNocache(Boolean nocache) {
		this.nocache = nocache;
	}
	
	public Integer getPrototype() {
		return prototype;
	}

	public void setPrototype(Integer prototype) {
		this.prototype = prototype;
	}

	public Integer getWorkResultSetSize() {
		return workResultSetSize;
	}

	public void setWorkResultSetSize(Integer workResultSetSize) {
		this.workResultSetSize = workResultSetSize;
	}

	public String getNever() {
		return never;
	}

	public void setNever(String never) {
		this.never = never;
	}

	public String getForever() {
		return forever;
	}

	public void setForever(String forever) {
		this.forever = forever;
	}

	public String getModifySyn() {
		return modifySyn;
	}

	public void setModifySyn(String modifySyn) {
		this.modifySyn = modifySyn;
	}

	public Boolean getUseLevelTwo() {
		return useLevelTwo;
	}

	public void setUseLevelTwo(Boolean useLevelTwo) {
		this.useLevelTwo = useLevelTwo;
	}

	public Boolean getLevelOneTolevelTwo() {
		return levelOneTolevelTwo;
	}

	public void setLevelOneTolevelTwo(Boolean levelOneTolevelTwo) {
		this.levelOneTolevelTwo = levelOneTolevelTwo;
	}

	public Integer getLevelTwoTimeout() {
		return levelTwoTimeout;
	}

	public void setLevelTwoTimeout(Integer levelTwoTimeout) {
		this.levelTwoTimeout = levelTwoTimeout;
	}

	public String getLevelTwoEntityList() {
		return levelTwoEntityList;
	}

	public void setLevelTwoEntityList(String levelTwoEntityList) {
		this.levelTwoEntityList = levelTwoEntityList;
	}

}
