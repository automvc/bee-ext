/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.bson.conversions.Bson;

/**
 * use in Mongodb select cache
 * @author Kingstar
 * @since  2.0
 */
public class MongoSqlStruct {

	String returnType;

	String tableName;
//	Document filter;
	Bson filter;

//	String group;
	Bson sortBson; // orderyBy

	Integer start;
	Integer size;

	String[] selectFields;
	boolean hasId;

	private String sql;

	public MongoSqlStruct(String returnType, String tableName, Bson filter, Bson sortBson,
			Integer start, Integer size, String[] selectFields, boolean hasId) {
		super();
		this.returnType = returnType;
		this.tableName = tableName;
		this.filter = filter;
		this.sortBson = sortBson;
		this.start = start;
		this.size = size;
		this.selectFields = selectFields;
		this.hasId = hasId;
	}

	public String getSql() { // just for cache
		if (this.sql == null) sql = toSql();
		return sql;
	}

	private String toSql() {

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("[table]: ");
		strBuf.append(tableName);
		strBuf.append(" , [where/filter]: ");
//		if (filter != null) strBuf.append(filter.toJson());
		if (filter != null) strBuf.append(filter.toString());
//		strBuf.append("[groupBy]: ");
//		strBuf.append(groupBy);
		strBuf.append(" , [orderyBy/sort]: ");
		if (sortBson != null) strBuf.append(sortBson.toString());
		strBuf.append(" , [skip]: ");
		strBuf.append(start);
		strBuf.append(" , [limit]: ");
		strBuf.append(size);
		strBuf.append(" , [selectFields]: ");
		if (selectFields != null) {
			for (int i = 0; i < selectFields.length; i++) {
				if (i != 0) strBuf.append(",");
				strBuf.append(selectFields[i]);
			}
		}
		strBuf.append(" , [returnType]: ");
		strBuf.append(returnType);

		return strBuf.toString();
	}

}
