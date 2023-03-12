/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql.mongodb;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.teasoft.bee.mongodb.GridFsFile;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.beex.osql.FieldNameUtil;
//import org.teasoft.beex.osql.SuidRichExt;
import org.teasoft.beex.osql.FieldNameUtil.SerialFunction;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;

/**
 * @author Kingstar
 * @since  2.1
 */
@SuppressWarnings("unchecked")
public class MongodbObjSQLRichExt extends MongodbObjSQLRich implements MongodbSuidRichExt {
	
	private static final long serialVersionUID = 1596710362268L;

	@Override
	public <T> List<T> select(T entity, SerialFunction<T, ?>... selectFields) {
		return select(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> List<T> select(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields) {
		return select(entity, start, size, getFieldNames(selectFields));
	}

	@Override
	public <T> List<String[]> selectString(T entity, SerialFunction<T, ?>... selectFields) {
		return selectString(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> int update(T entity, SerialFunction<T, ?>... updateFields) {
		return update(entity, getFieldNames(updateFields));
	}

	@Override
	public <T> int update(T entity, IncludeType includeType,
			SerialFunction<T, ?>... updateFields) {
		return update(entity, includeType, getFieldNames(updateFields));
	}

	@Override
	public <T> String selectJson(T entity, SerialFunction<T, ?>... selectFields) {
		return selectJson(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> String selectJson(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields) {
		return selectJson(entity, start, size, getFieldNames(selectFields));
	}

	@Override
	public <T> int updateBy(T entity, SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, getFieldNames(whereFields));
	}

	@Override
	public <T> int updateBy(T entity, IncludeType includeType,
			SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, includeType, getFieldNames(whereFields));
	}

	@Override
	public <T> int updateBy(T entity, Condition condition,
			SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, condition, getFieldNames(whereFields));
	}

	@Override
	public <T> int update(T entity, Condition condition, SerialFunction<T, ?>... updateFields) {
		return update(entity, condition, getFieldNames(updateFields));
	}

	private <T> String[] getFieldNames(SerialFunction<T, ?>... fns) {
		String[] fieldNames = FieldNameUtil.getFieldNames(fns);
		return fieldNames;
	}

	
	/////////////// operate file,eg: GridFS
	
	@Override
	public String uploadFile(String filename, InputStream fileStream) {
		return getMongodbBeeSql().uploadFile(filename, fileStream);
	}

	@Override
	public String uploadFile(String filename, InputStream fileStream,
			Map<String, Object> metadataMap) {
		return getMongodbBeeSql().uploadFile(filename, fileStream, metadataMap);
	}

	@Override
	public List<GridFsFile> selectFiles(GridFsFile gridFsFile, Condition condition) {
		return getMongodbBeeSql().selectFiles(gridFsFile, condition);
	}

	@Override
	public byte[] getFileByName(String fileName) {
		return getMongodbBeeSql().getFileByName(fileName);
	}

	@Override
	public byte[] getFileById(String fileId) {
		return getMongodbBeeSql().getFileById(fileId);
	}

	@Override
	public void renameFile(String fileId, String newName) {
		getMongodbBeeSql().renameFile(fileId, newName);
	}

	@Override
	public void deleteFile(String fileId) {
		getMongodbBeeSql().deleteFile(fileId);
	}
	
}
