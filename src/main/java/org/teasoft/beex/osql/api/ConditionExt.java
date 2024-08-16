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

package org.teasoft.beex.osql.api;

import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.bee.osql.api.Condition;

/**
 * @author Kingstar
 * @since  2.4.0
 */
public interface ConditionExt extends Condition {

	public <T> Condition op(SerialFunction<T, ?> field, Op op, Object value);

	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, String value);

	public <T> Condition opOn(SerialFunction<T, ?> field, Op op, Number value);

	public <T> Condition opWithField(SerialFunction<T, ?> field1, Op op, SerialFunction<T, ?> field2);

	public <T> Condition between(SerialFunction<T, ?> field, Number low, Number high);

	public <T> Condition groupBy(SerialFunction<T, ?> field);

	/**
	 * having
	 * <br>eg: having(FunctionType.MIN, Orders::getField", Op.ge, 60)-->having min(field)>=60
	 * @param functionType FunctionType
	 * @param field Entity field,it will be translated according the config.
	 * @param op operator
	 * @param value Value of the field.
	 * @return Condition
	 */
	public <T> Condition having(FunctionType functionType, SerialFunction<T, ?> field, Op op, Number value);

	/**
	 * order by
	 * <br>eg: orderBy(Orders::getPrice)-->order by price
	 * @param field field name.
	 * @return Condition
	 */
	public <T> Condition orderBy(SerialFunction<T, ?> field);

	/**
	 * order by
	 * <br>eg: orderBy(Orders::getPrice, OrderType.DESC)-->order by price desc
	 * @param field Field name.
	 * @param orderType order type(asc or desc)
	 * @return Condition
	 */
	public <T> Condition orderBy(SerialFunction<T, ?> field, OrderType orderType);

	/**
	 * order by
	 * <br>eg: orderBy(FunctionType.MAX, Orders::getTotal, OrderType.DESC)-->order by max(total) desc
	 * @param functionType FunctionType of SQL.
	 * @param field Field name.
	 * @param orderType order type(asc or desc)
	 * @return Condition
	 */
	public <T> Condition orderBy(FunctionType functionType, SerialFunction<T, ?> field, OrderType orderType);

	/**
	 * Specify the partial fields to be queried (only for select of SQL).
	 * @param fields select fields,if more than one,separate with comma or use variable-length arguments.
	 * @return Condition
	 */
	@SuppressWarnings({ "unchecked", "varargs" })
	public <T> Condition selectField(SerialFunction<T, ?>... fields);

	/**
	 * set fieldName for distinct(Orders::getName)
	 * <br>eg: selectDistinctField(Orders::getName) --> distinct(name)
	 * @param fieldName Field name
	 * @return Condition
	 */
	public <T> Condition selectDistinctField(SerialFunction<T, ?> fieldName);

	/**
	 * set fieldName for distinct(Orders::getName)
	 * eg: selectDistinctField(Orders::getName,alias) --> distinct(name) as alias
	 * @param fieldName Field name
	 * @param alias Name of alias
	 * @return Condition
	 */
	public <T> Condition selectDistinctField(SerialFunction<T, ?> fieldName, String alias);

	/**
	 * set for select result with function.
	 * <br>eg: condition.selectFun(FunctionType.COUNT, Orders::getName);-->count(name)
	 * @param functionType FunctionType of SQL.
	 * @param fieldForFun Field name for function.
	 * @return Condition
	 */
	public <T> Condition selectFun(FunctionType functionType, SerialFunction<T, ?> fieldForFun);

	/**
	 * set for select result with function.
	 * <br>eg:selectFun(FunctionType.MAX, Course::getScore,"maxScore")-->max(score) as maxScore
	 * @param functionType FunctionType of SQL.
	 * @param fieldForFun field name for function.
	 * @param alias Name of alias for the function result.
	 * @return Condition
	 */
	public <T> Condition selectFun(FunctionType functionType, SerialFunction<T, ?> fieldForFun, String alias);

	//////////////////////////////// -------just use in update-------------start-

	/**
	 * Set the fields to be updated (for only update of SQL),and the field change on itself.
	 * <br>eg: setAdd(Orders::getPrice,2.0)--> set set price=price+2.0
	 * @param field Field name.
	 * @param num number
	 * @return Condition
	 */
	public <T> Condition setAdd(SerialFunction<T, ?> field, Number num);

	/**
	 * Set the fields to be updated (for only update of SQL),and the field change on itself.
	 * <br>eg: setMultiply(Orders::getPrice,1.05)--> set price=price*1.05
	 * @param field Field name.
	 * @param num number
	 * @return Condition
	 */
	public <T> Condition setMultiply(SerialFunction<T, ?> field, Number num);

	/**
	 * Set the fields to be updated (for only update of SQL),and the field change on itself.
	 * <br>eg:setAdd(Orders::getPrice,Orders::getDelta)--> set price=price+delta
	 * @param field Field name.
	 * @param otherFieldName
	 * @return Condition
	 */
	public <T> Condition setAdd(SerialFunction<T, ?> field, SerialFunction<T, ?> otherFieldName);

	/**
	 * Set the fields to be updated (for only update of SQL),and the field change on itself.
	 * <br>eg: setMultiply(Orders::getPrice,Orders::getDelta)--> set price=price*delta
	 * @param field Field name.
	 * @param otherFieldName other fieldName
	 * @return Condition
	 */
	public <T> Condition setMultiply(SerialFunction<T, ?> field, SerialFunction<T, ?> otherFieldName);

	/**
	 * Set the fields that need to be updated (only for update of SQL ); 
	 * <br>this method can be used when the set fields also need to be used for the where expression.
	 * <br>eg: set(Orders::getMaxid, 1000)-->update table_name set maxid=1000
	 * @param fieldName field name
	 * @param num number
	 * @return Condition
	 */
	public <T> Condition set(SerialFunction<T, ?> fieldName, Number num);

	/**
	 * Set the fields that need to be updated (only for update of SQL); 
	 * <br>this method can be used when the set fields also need to be used for the where expression.
	 * <br>eg: set(Orders::getName, 'bee')--> set name='bee'
	 * @param fieldName Field name
	 * @param value
	 * @return Condition
	 */
	public <T> Condition set(SerialFunction<T, ?> fieldName, String value);

	/**
	 * Set the fields with null value (only for update of SQL); 
	 * @param fieldName
	 * @return Condition
	 */
	public <T> Condition setNull(SerialFunction<T, ?> fieldName);

	/**
	 * set one field with other field value
	 * <br>eg: setWithField(Orders::getField1,Orders::getField2)--> set field1=field2
	 * @param field1 first field name
	 * @param field2 second field name
	 * @return Condition
	 */
	public <T> Condition setWithField(SerialFunction<T, ?> field1, SerialFunction<T, ?> field2);

	//////////////////////////////// -------just use in update-------------end-

}
