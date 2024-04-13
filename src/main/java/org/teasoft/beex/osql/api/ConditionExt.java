/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql.api;

import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.Condition;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public interface ConditionExt extends Condition {

	public <T> Condition op(SerialFunction<T, ?> field, Op op, Object value);

	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, String value);

	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, Number value);

	public <T> Condition opWithField(SerialFunction<T, ?> field1, Op op,
			SerialFunction<T, ?> field2);
	
	//是否全部要移过来?
//	public Condition between(String field, Number low, Number high);
//	public Condition groupBy(String field);
//	
//	public Condition having(FunctionType functionType, String field, Op op, Number value);

}
