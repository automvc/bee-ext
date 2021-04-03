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
public class BeeProNaming {

	//	prefix = "naming_"
	private Boolean toLowerCaseBefore;

	private Integer translateType;

	private String entity2tableMappingList;

	public Boolean getToLowerCaseBefore() {
		return toLowerCaseBefore;
	}

	public void setToLowerCaseBefore(Boolean toLowerCaseBefore) {
		this.toLowerCaseBefore = toLowerCaseBefore;
	}

	public Integer getTranslateType() {
		return translateType;
	}

	public void setTranslateType(Integer translateType) {
		this.translateType = translateType;
	}

	public String getEntity2tableMappingList() {
		return entity2tableMappingList;
	}

	public void setEntity2tableMappingList(String entity2tableMappingList) {
		this.entity2tableMappingList = entity2tableMappingList;
	}

}
