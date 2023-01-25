/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql;

import java.util.List;

import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.beex.osql.FieldNameUtil.SerialFunction;
import org.teasoft.honey.osql.core.ObjSQLRich;

/**
 * @author Kingstar
 * @since  2.0
 */
@SuppressWarnings("unchecked")
public class ObjSQLRichExt extends ObjSQLRich implements SuidRichExt {

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

}
