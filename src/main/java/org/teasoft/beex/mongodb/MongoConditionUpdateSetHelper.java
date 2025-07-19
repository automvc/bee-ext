/*
 * Copyright 2020-2023 the original author.All rights reserved.
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

package org.teasoft.beex.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.exception.BeeErrorGrammarException;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.Expression;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.core.OpType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author Kingstar
 * @since  2.0
 * use DBObject
 * @since  2.1
 */
public class MongoConditionUpdateSetHelper {

	static List<DBObject> processConditionForUpdateSet(Condition condition) {

		if (condition == null) return null;

		List<DBObject> updateSetBsonList = null;
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		List<Expression> updateSetList = conditionImpl.getUpdateExpList();

		if (updateSetList != null && updateSetList.size() > 0) {
			if (SuidType.UPDATE != conditionImpl.getSuidType()) {
				throw new BeeErrorGrammarException(
						conditionImpl.getSuidType() + " do not support the method set ,setAdd or setMultiply!");
			}
		}

		Expression expression = null;
		Class<?> entityClass = null;

		DBObject setObject = null;
		DBObject incObject = null;
		DBObject mulObject = null;
		boolean hasSet = false;
		boolean hasInc = false;
		boolean hasMul = false;

		for (int j = 0; updateSetList != null && j < updateSetList.size(); j++) {
			if (j == 0) updateSetBsonList = new ArrayList<>();
			expression = updateSetList.get(j);
			OpType opType = expression.getOpType();
			String columnName = _toColumnName(expression.getFieldName(), entityClass);

			if (opType == OpType.OP2) { // set("fieldName",value)
//					bs = Updates.set(columnName, expression.getValue());
//					updateSetBsonList.add(bs);

				if (!hasSet) setObject = new BasicDBObject();
				setObject.put(columnName, expression.getValue());
				hasSet = true;
				continue;
			}

			if (opType == OpType.SET_ADD) { // price=price + num
//					bs = Updates.inc(columnName, (Number) expression.getValue());
//					updateSetBsonList.add(bs);
				if (!hasInc) incObject = new BasicDBObject();
				hasInc = true;
				incObject.put(columnName, (Number) expression.getValue());

			} else if (opType == OpType.SET_MULTIPLY) { // price=price * num
//					bs = Updates.mul(columnName, (Number) expression.getValue());
//					updateSetBsonList.add(bs);
				if (!hasMul) mulObject = new BasicDBObject();
				hasMul = true;
				mulObject.put(columnName, (Number) expression.getValue());

			} else if (opType == OpType.SET_ADD_FIELD) {// eg:setAdd("price","field2")--> price=price + field2

				Logger.warn("Mongodb donot support the syntax like: set price = price + field2");

			} else if (opType == OpType.SET_MULTIPLY_FIELD) {// eg:setMultiply("price","field2")--> price=price * field2

				Logger.warn("Mongodb donot support the syntax like: set price = price * field2");

			} else if (opType == OpType.SET_WITH_FIELD) { // set field1=field2
				Logger.warn("Mongodb donot support the syntax like: set field1 = field2");
			}
		}

		if (updateSetBsonList != null) {
			updateSetBsonList.add(setObject);
			updateSetBsonList.add(incObject);
			updateSetBsonList.add(mulObject);
		}

		return updateSetBsonList;
	}

	@SuppressWarnings("rawtypes")
	private static String _toColumnName(String fieldName, Class entityClass) {
		return NameTranslateHandle.toColumnName(fieldName, entityClass);
	}

}
