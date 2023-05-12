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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.annotation.Geo2dsphere;
import org.teasoft.bee.osql.annotation.GridFs;
import org.teasoft.bee.osql.annotation.GridFsMetadata;
import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.type.SetParaTypeConvert;
import org.teasoft.bee.sharding.ShardingSortStruct;
import org.teasoft.honey.osql.constant.NullEmpty;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.core.StringConst;
import org.teasoft.honey.osql.type.SetParaTypeConverterRegistry;
import org.teasoft.honey.sharding.ShardingUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Sorts;

/**
 * Parameter Convert Util for Mongodb.
 * @author Jade
 * @since  2.0
 */
public class ParaConvertUtil {

	public static Map<String, Object> toMap(Object entity) throws Exception {
		return  toMap(entity, -1);
	}
	
	public static Map<String, Object> toMap(Object entity,int includeType) throws Exception {
		return toMap(entity, includeType, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> toMap(Object entity, int includeType, SuidType suidType)
			throws Exception {
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
				// 是实体的，要转成Json; null不转
				if(value!=null &&  fields[i].isAnnotationPresent(Json.class)) {
					SetParaTypeConvert converter = SetParaTypeConverterRegistry.getConverter(Json.class);
					if (converter != null) {
						value=(String)converter.convert(value);
					}
				}else if(value!=null && suidType==SuidType.INSERT && fields[i].isAnnotationPresent(GridFs.class)) { //V2.1 一个实体只支持一个文件
					GridFs sysValue = fields[i].getAnnotation(GridFs.class);
					String fileid = sysValue.fileIdName();
					String filename = sysValue.fileName();
					fileid=_toColumnName(fileid,entity.getClass());
					filename=_toColumnName(filename,entity.getClass());
					documentAsMap.put(StringConst.GridFs_FileId, fileid);
					documentAsMap.put(StringConst.GridFs_FileName, filename);
					documentAsMap.put(StringConst.GridFs_FileColumnName, column);
				} else if (value != null && suidType == SuidType.INSERT
						&& (fields[i].isAnnotationPresent(Geo2dsphere.class) || fields[i]
								.getType().isAnnotationPresent(Geo2dsphere.class))) {
					Map m = toMap(value);
					if (m.get("coordinates") instanceof Double[]) {
						m.put("coordinates", Arrays.asList((Double[]) m.get("coordinates")));
//						m.put("coordinates",toDoubleList((Double[])m.get("coordinates")));
					}
					value = m;
				}
				
				//set value
				if ("_id".equalsIgnoreCase(column) && value == null) {
					// ignore
				} else {
					if (value != null && fields[i].isAnnotationPresent(GridFsMetadata.class)) {
						documentAsMap.put(GridFsMetadata.class.getName(), value);
					} else {
						documentAsMap.put(column, value);
					}
				}
			}
		}

		return documentAsMap;
	}
	
//	private static List<Double> toDoubleList(Double dArray[]) {
//		if(dArray==null || dArray.length==0) return new ArrayList<>(); 
//		List<Double> list=new ArrayList<>(dArray.length);
//		for (Double d : dArray) {
//			list.add(d);
//		}
//		return list;
//	}
	
//	private static boolean isExcludeField(String excludeFieldList, String checkField) {
//		String excludeFields[] = excludeFieldList.split(",");
//		for (String f : excludeFields) {
//			if (f.equals(checkField)) return true;
//		}
//		return false;
//	}
	
	public static Map<String, Object> toMapExcludeSome(Object entity,String excludeFieldList) throws Exception {
		
//		Map<String, Object> documentAsMap = null;
//		Field fields[] = entity.getClass().getDeclaredFields();
//		boolean isFirst = true;
//		int len = fields.length;
//		String column = "";
//		Object value = null;
//		for (int i = 0; i < len; i++) {
//			fields[i].setAccessible(true);
////			if (HoneyUtil.isContinue(-1, fields[i].get(entity), fields[i])) {
//			if (HoneyUtil.isContinue(NullEmpty.EMPTY_STRING, fields[i].get(entity), fields[i])) {// mongodb,批量插入,不处理null,但会插入是空字符的
//				continue;
//			} else {
//				if (!"".equals(excludeFieldList) && isExcludeField(excludeFieldList, fields[i].getName())) continue;
//				
//				if (isFirst) {
//					isFirst = false;
//					documentAsMap = new LinkedHashMap<String, Object>();
//				}
//				column = _toColumnName(fields[i].getName(), entity.getClass());
//				if ("id".equalsIgnoreCase(column)) {// 替换id为_id
//					column = "_id";
//				}
//				value = fields[i].get(entity); // value
//				documentAsMap.put(column, value);
//			}
//		}
//
//		return documentAsMap;
		
		Map<String, Object> map = toMap(entity, NullEmpty.EMPTY_STRING, SuidType.INSERT);

		String excludeFields[] = excludeFieldList.split(",");
		for (String f : excludeFields) {
			map.remove(_toColumnName(f, entity.getClass()));
		}
		
		return map;
	}
	

	public static List<Map<String, Object>> toListBson(List<Object> objs) throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		for (int i = 0; null != objs && i < objs.size(); i++) {
			list.add(toMap(objs.get(i)));
		}
		return list;
	}

	public static Bson toSortBson(String orderFields[], OrderType[] orderTypes) {
		if (orderFields.length < 1) return null;
		
		int len=orderFields.length;
		
		if (len == 1) {
			int order = -1;
			if (orderTypes!=null && orderTypes.length==1 && orderTypes[0] == OrderType.ASC) order = 1;
//			return new Document(tranferId(orderFields[0]), order);
			return new BasicDBObject(tranferId(orderFields[0]), order);
		}

		Bson b[] = new Bson[len];
		int order;
		for (int i = 0; i < len; i++) {
			if (orderTypes==null || orderTypes[i] == OrderType.ASC)
				order = 1;
			else
				order = -1;
//			b[i] = new Document(tranferId(orderFields[i]), order);
			b[i] = new BasicDBObject(tranferId(orderFields[i]), order);
		}

//		return Filters.and(b);
		return Sorts.orderBy(b);
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
		ShardingSortStruct struct=ShardingUtil.parseOrderByMap(conditionImpl.getOrderBy());
		
		return toSortBson(struct.getOrderFields(), struct.getOrderTypes());
	}
	

	@SuppressWarnings("rawtypes")
	private static String _toColumnName(String fieldName, Class entityClass) {
		return NameTranslateHandle.toColumnName(fieldName, entityClass);
	}

}
