/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.exception.BeeErrorGrammarException;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.Expression;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;

import com.mongodb.client.model.Updates;

/**
 * @author Kingstar
 * @since  2.0
 */
public class MongoConditionUpdateSetHelper {

	private static final String setAdd = "setAdd";
	private static final String setMultiply = "setMultiply";
	private static final String setAddField = "setAddField";
	private static final String setMultiplyField = "setMultiplyField";
	private static final String setWithField = "setWithField";

	static List<Bson> processConditionForUpdateSet(Condition condition) {

		if (condition == null) return null;

		List<Bson> updateSetBsonList = null;
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		List<Expression> updateSetList = conditionImpl.getUpdateExpList();

		if (updateSetList != null && updateSetList.size() > 0) {
			if (SuidType.UPDATE != conditionImpl.getSuidType()) {
				throw new BeeErrorGrammarException(conditionImpl.getSuidType()
						+ " do not support the method set ,setAdd or setMultiply!");
			}
		}

		Expression expression = null;
		Class<?> entityClass = null;

		Bson bs = null;
		for (int j = 0; updateSetList != null && j < updateSetList.size(); j++) {
			if (j == 0) updateSetBsonList = new ArrayList<>();
			expression = updateSetList.get(j);
			String opType = expression.getOpType();

			if (opType != null && expression.getValue() == null) {
				throw new BeeErrorGrammarException(
						"the value is null (" + conditionImpl.getSuidType() + ", method:"
								+ opType + ", fieldName:" + expression.getFieldName() + ")!");
			} else {

				String columnName = _toColumnName(expression.getFieldName(), entityClass);

				if (opType == null && expression.getValue() == null) { // set("fieldName",null)
					bs = Updates.set(columnName, null);
					updateSetBsonList.add(bs);

					continue;
				}

//				if (opType != null) { // 只有set(arg1,arg2) opType=null
////					if (setWithField.equals(opType)) {
////					}
//					continue;
//				}

				if (setAdd.equals(opType)) { // price=price + num
					bs = Updates.inc(columnName, (Number) expression.getValue());
					updateSetBsonList.add(bs);

				} else if (setMultiply.equals(opType)) { // price=price * num
					bs = Updates.mul(columnName, (Number) expression.getValue());
					updateSetBsonList.add(bs);

//					System.out.println(bs.toString());
				} else if (setAddField.equals(opType)) {// eg:setAdd("price","delta")--> price=price + field2

					Logger.warn(
							"Mongodb donot support the syntax like: set price=price + field2");

				} else if (setMultiplyField.equals(opType)) {// eg:setMultiply("price","delta")--> price=price * field2

					Logger.warn(
							"Mongodb donot support the syntax like: set price=price * field2");

				} else if (setWithField.equals(opType)) { // set field1=field2
					Logger.warn("Mongodb donot support the syntax like: set field1=field2");
				}
			}

		}

		return updateSetBsonList;
	}

	@SuppressWarnings("rawtypes")
	private static String _toColumnName(String fieldName, Class entityClass) {
		return NameTranslateHandle.toColumnName(fieldName, entityClass);
	}

}
