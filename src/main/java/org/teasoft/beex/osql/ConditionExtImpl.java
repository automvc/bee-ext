/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql;

import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.beex.osql.api.ConditionExt;
import org.teasoft.beex.osql.api.SerialFunction;
import org.teasoft.honey.osql.core.ConditionImpl;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public class ConditionExtImpl extends ConditionImpl implements ConditionExt{

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
	
	private <T> String getFieldName(SerialFunction<T, ?> fn) {
		return FieldNameUtil.getFieldName(fn);
	}
	
	

}
