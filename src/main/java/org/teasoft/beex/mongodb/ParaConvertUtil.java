/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.bee.sharding.ShardingSortStruct;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.sharding.ShardingUtil;

import com.mongodb.client.model.Filters;

/**
 * @author Jade
 * @since  2.0
 */
public class ParaConvertUtil {

	public static Map<String, Object> toMap(Object entity) throws Exception {
		return  toMap(entity, -1);
		
	}

	public static Map<String, Object> toMap(Object entity,int includeType) throws Exception {
		Map<String, Object> documentAsMap = null;
		Field fields[] = entity.getClass().getDeclaredFields();
		boolean isFirst = true;
		int len = fields.length;
		String column = "";
		Object value = null;
		for (int i = 0; i < len; i++) {
			fields[i].setAccessible(true);
			if (HoneyUtil.isContinue(includeType, fields[i].get(entity), fields[i])) {
				continue;
			} else {
				if (isFirst) {
					isFirst = false;
					documentAsMap = new LinkedHashMap<String, Object>();
				}
				column = _toColumnName(fields[i].getName(), entity.getClass());
				if ("id".equalsIgnoreCase(column)) {// 替换id为_id
					column = "_id";
				}
				value = fields[i].get(entity); // value
				documentAsMap.put(column, value);
			}
		}

		return documentAsMap;
	}
	
	
	private static boolean isExcludeField(String excludeFieldList, String checkField) {
		String excludeFields[] = excludeFieldList.split(",");
		for (String f : excludeFields) {
			if (f.equals(checkField)) return true;
		}
		return false;
	}
	
	public static Map<String, Object> toMapExcludeSome(Object entity,String excludeFieldList) throws Exception {
		Map<String, Object> documentAsMap = null;
		Field fields[] = entity.getClass().getDeclaredFields();
		boolean isFirst = true;
		int len = fields.length;
		String column = "";
		Object value = null;
		for (int i = 0; i < len; i++) {
			fields[i].setAccessible(true);
			if (HoneyUtil.isContinue(-1, fields[i].get(entity), fields[i])) {
				continue;
			} else {
				if (!"".equals(excludeFieldList) && isExcludeField(excludeFieldList, fields[i].getName())) continue;
				
				if (isFirst) {
					isFirst = false;
					documentAsMap = new LinkedHashMap<String, Object>();
				}
				column = _toColumnName(fields[i].getName(), entity.getClass());
				if ("id".equalsIgnoreCase(column)) {// 替换id为_id
					column = "_id";
				}
				value = fields[i].get(entity); // value
				documentAsMap.put(column, value);
			}
		}

		return documentAsMap;
	}
	

	public static List<Map<String, Object>> toListBson(List<Object> objs) throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		for (int i = 0; null != objs && i < objs.size(); i++) {
			list.add(toMap(objs.get(i)));
		}
		return list;
	}

	public static Bson toSortBson(String orderFields[], OrderType[] orderTypes) {
		if (orderTypes.length < 1) return null;
		
		
		if (orderTypes.length == 1) {
			int order = -1;
			if (orderTypes[0] == OrderType.ASC) order = 1;
			return new Document(tranferId(orderFields[0]), order);
		}

		Bson b[] = new Bson[orderTypes.length];
		int order;
		for (int i = 0; i < orderTypes.length; i++) {
			if (orderTypes[i] == OrderType.ASC)
				order = 1;
			else
				order = -1;
			b[i] = new Document(tranferId(orderFields[i]), order);
		}

		return Filters.and(b);
	}
	
	private static String tranferId(String fieldName) {
		if ("id".equalsIgnoreCase(fieldName))
			return "_id";
		else
			return fieldName;
	}
	
	public static Bson toSortBson(Condition condition) {
		
		if(condition==null) return null;
		
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		ShardingSortStruct struct=ShardingUtil.parseOrderByMap(conditionImpl.getOrderByMap());
		
		return toSortBson(struct.getOrderFields(), struct.getOrderTypes());
	}
	

	@SuppressWarnings("rawtypes")
	private static String _toColumnName(String fieldName, Class entityClass) {
		return NameTranslateHandle.toColumnName(fieldName, entityClass);
	}

//	private static void checkLikeEmptyException(String value) {
//		if ("".equals(value)) throw new BeeIllegalSQLException(
//				"Like has SQL injection risk! the value can not be empty string!");
//	}

}
