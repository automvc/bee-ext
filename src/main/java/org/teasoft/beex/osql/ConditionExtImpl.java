/*
 * Copyright 2019-2024 the original author.All rights reserved.
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

package org.teasoft.beex.osql;

import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.beex.osql.api.ConditionExt;
import org.teasoft.beex.osql.api.SerialFunction;
import org.teasoft.honey.osql.core.ConditionImpl;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class ConditionExtImpl extends ConditionImpl implements ConditionExt {

	@Override
	public <T> Condition op(SerialFunction<T, ?> field, Op op, Object value) {
		return op(getFieldName(field), op, value);
	}

	@Override
	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, String value) {
		return opOn(getFieldName(field), op, value);
	}

	@Override
	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, Number value) {
		return opOn(getFieldName(field), op, value);
	}

	@Override
	public <T> Condition opWithField(SerialFunction<T, ?> field1, Op op,
			SerialFunction<T, ?> field2) {
		return opWithField(getFieldName(field1), op, getFieldName(field2));
	}

	
	@Override
	public <T> Condition between(SerialFunction<T, ?> field, Number low, Number high) {
		return between(getFieldName(field), low, high);
	}

	@Override
	public <T> Condition groupBy(SerialFunction<T, ?> field) {
		return groupBy(getFieldName(field));
	}

	@Override
	public <T> Condition having(FunctionType functionType, SerialFunction<T, ?> field, Op op,
			Number value) {
		return having(functionType, getFieldName(field), op, value);
	}

	@Override
	public <T> Condition orderBy(SerialFunction<T, ?> field) {
		return orderBy(getFieldName(field));
	}

	@Override
	public <T> Condition orderBy(SerialFunction<T, ?> field, OrderType orderType) {
		return orderBy(getFieldName(field), orderType);
	}

	@Override
	public <T> Condition orderBy(FunctionType functionType, SerialFunction<T, ?> field,
			OrderType orderType) {
		return orderBy(functionType, getFieldName(field), orderType);
	}

	@Override
	@SuppressWarnings({ "unchecked", "varargs" })
	public <T> Condition selectField(SerialFunction<T, ?>... fields) {
		return selectField(getFieldNames(fields));
	}

	@SuppressWarnings({ "unchecked", "varargs" })
	private <T> String[] getFieldNames(SerialFunction<T, ?>... fns) {
		return FieldNameUtil.getFieldNames(fns);
	}

	@Override
	public <T> Condition selectDistinctField(SerialFunction<T, ?> fieldName) {
		return selectDistinctField(getFieldName(fieldName));
	}

	@Override
	public <T> Condition selectDistinctField(SerialFunction<T, ?> fieldName, String alias) {
		return selectDistinctField(getFieldName(fieldName), alias);
	}

	@Override
	public <T> Condition selectFun(FunctionType functionType,
			SerialFunction<T, ?> fieldForFun) {
		return selectFun(functionType, getFieldName(fieldForFun));
	}

	@Override
	public <T> Condition selectFun(FunctionType functionType, SerialFunction<T, ?> fieldForFun,
			String alias) {
		return selectFun(functionType, getFieldName(fieldForFun), alias);
	}

	@Override
	public <T> Condition setAdd(SerialFunction<T, ?> field, Number num) {
		return setAdd(getFieldName(field), num);
	}

	@Override
	public <T> Condition setMultiply(SerialFunction<T, ?> field, Number num) {
		return setMultiply(getFieldName(field), num);
	}

	@Override
	public <T> Condition setAdd(SerialFunction<T, ?> field,
			SerialFunction<T, ?> otherFieldName) {
		return setAdd(getFieldName(field), getFieldName(otherFieldName));
	}

	@Override
	public <T> Condition setMultiply(SerialFunction<T, ?> field,
			SerialFunction<T, ?> otherFieldName) {
		return setMultiply(getFieldName(field), getFieldName(otherFieldName));
	}

	@Override
	public <T> Condition set(SerialFunction<T, ?> fieldName, Number num) {
		return set(getFieldName(fieldName), num);
	}

	@Override
	public <T> Condition set(SerialFunction<T, ?> fieldName, String value) {
		return set(getFieldName(fieldName), value);
	}

	@Override
	public <T> Condition setNull(SerialFunction<T, ?> fieldName) {
		return setNull(getFieldName(fieldName));
	}

	@Override
	public <T> Condition setWithField(SerialFunction<T, ?> field1,
			SerialFunction<T, ?> field2) {
		return setWithField(getFieldName(field1), getFieldName(field2));
	}

	private <T> String getFieldName(SerialFunction<T, ?> fn) {
		return FieldNameUtil.getFieldName(fn);
	}

}
