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

import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.exception.BeeErrorGrammarException;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.Expression;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author Kingstar
 * @since  2.0
 * use DBObject
 * @since  2.1
 */
public class MongoConditionUpdateSetHelper {

	private static final String setAdd = "setAdd";
	private static final String setMultiply = "setMultiply";
	private static final String setAddField = "setAddField";
	private static final String setMultiplyField = "setMultiplyField";
	private static final String setWithField = "setWithField";
	
	static List<DBObject> processConditionForUpdateSet(Condition condition) {

		if (condition == null) return null;

		List<DBObject> updateSetBsonList = null;
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
		
		DBObject setObject = null;
		DBObject incObject = null;
		DBObject mulObject = null;
		boolean hasSet = false;
		boolean hasInc = false;
		boolean hasMul = false;

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

				
				if (opType == null) { // set("fieldName",value)
//					bs = Updates.set(columnName, expression.getValue());
//					updateSetBsonList.add(bs);
					
					if(! hasSet) setObject = new BasicDBObject();
					setObject.put(columnName, expression.getValue());
					hasSet=true;
					continue;
				}

//				if (opType != null) { // 只有set(arg1,arg2) opType=null
////					if (setWithField.equals(opType)) {
////					}
//					continue;
//				}

				if (setAdd.equals(opType)) { // price=price + num
//					bs = Updates.inc(columnName, (Number) expression.getValue());
//					updateSetBsonList.add(bs);
					if(!hasInc) incObject = new BasicDBObject();
					hasInc=true;
					incObject.put(columnName, (Number) expression.getValue());

				} else if (setMultiply.equals(opType)) { // price=price * num
//					bs = Updates.mul(columnName, (Number) expression.getValue());
//					updateSetBsonList.add(bs);
					if(!hasMul) mulObject = new BasicDBObject();
					hasMul=true;
					mulObject.put(columnName, (Number) expression.getValue());

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
