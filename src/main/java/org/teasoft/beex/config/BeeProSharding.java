/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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
