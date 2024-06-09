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
 * @author Kingstar
 * @since  2.0
 */
public class BeeProSharding {
	
	// prefix= "sharding_"
	private Boolean forkJoinBatchInsert;
	private Boolean jdbcStreamSelect; 
	private Boolean notSupportUnionQuery;
	private Integer executorSize;

	public Boolean getForkJoinBatchInsert() {
		return forkJoinBatchInsert;
	}

	public void setForkJoinBatchInsert(Boolean forkJoinBatchInsert) {
		this.forkJoinBatchInsert = forkJoinBatchInsert;
	}

	public Boolean getJdbcStreamSelect() {
		return jdbcStreamSelect;
	}

	public void setJdbcStreamSelect(Boolean jdbcStreamSelect) {
		this.jdbcStreamSelect = jdbcStreamSelect;
	}

	public Boolean getNotSupportUnionQuery() {
		return notSupportUnionQuery;
	}

	public void setNotSupportUnionQuery(Boolean notSupportUnionQuery) {
		this.notSupportUnionQuery = notSupportUnionQuery;
	}

	public Integer getExecutorSize() {
		return executorSize;
	}

	public void setExecutorSize(Integer executorSize) {
		this.executorSize = executorSize;
	}
	
}
