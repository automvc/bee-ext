/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.bson.Document;
import org.bson.conversions.Bson;

/**
 * use in Mongodb select cache
 * @author Kingstar
 * @since  2.0
 */
public class MongoSqlStruct {

	String returnType;
	
	String tableName;
	Document filter;
	
//	String group;
	Bson sortBson; //orderyBy

	Integer start;
	Integer size;

	String[] selectFields;
	boolean hasId;
	
	private String sql;
	
	public MongoSqlStruct(String returnType, String tableName, Document filter, Bson sortBson,
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
	
	public String getSql() { //just for cache
		if(this.sql==null) sql=toSql();
		return sql;
	}
	
	private String toSql() {
		
		StringBuffer strBuf=new StringBuffer();
		
		strBuf.append("[table]: ");
		strBuf.append(tableName);
		strBuf.append("[where]: ");
		if(filter!=null)
		  strBuf.append(filter.toJson());
//		strBuf.append("[groupBy]: ");
//		strBuf.append(groupBy);
		strBuf.append("[orderyBy]: ");
		if(sortBson!=null)
		  strBuf.append(sortBson.toString());
		strBuf.append("[skip]: ");
		strBuf.append(start);
		strBuf.append("[limit]: ");
		strBuf.append(size);
		strBuf.append("[selectFields]: ");
		strBuf.append(selectFields);
		strBuf.append("[returnType]: ");
		strBuf.append(returnType);
		
		return strBuf.toString();
	}
	
	
}
