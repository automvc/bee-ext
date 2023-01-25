/*
 * Copyright 2020-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql;

import java.util.List;

import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.beex.osql.FieldNameUtil.SerialFunction;

/**
 * @author Kingstar
 * @since  2.0
 */
@SuppressWarnings("unchecked")
public interface SuidRichExt extends SuidRich {

	public <T> List<T> select(T entity, SerialFunction<T, ?>... selectFields);

	public <T> List<T> select(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields);

	public <T> List<String[]> selectString(T entity, SerialFunction<T, ?>... selectFields);

	public <T> int update(T entity, SerialFunction<T, ?>... updateFields);

	public <T> int update(T entity, IncludeType includeType,
			SerialFunction<T, ?>... updateFields);

	public <T> String selectJson(T entity, SerialFunction<T, ?>... selectFields);

	public <T> String selectJson(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields);

	public <T> int updateBy(T entity, SerialFunction<T, ?>... whereFields);

	public <T> int updateBy(T entity, IncludeType includeType,
			SerialFunction<T, ?>... whereFields);

	public <T> int updateBy(T entity, Condition condition, SerialFunction<T, ?>... whereFields);

	public <T> int update(T entity, Condition condition, SerialFunction<T, ?>... updateFields);

}
