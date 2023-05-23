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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.teasoft.bee.osql.annotation.Geo2dsphere;
import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.type.TypeHandler;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.JsonResultWrap;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.type.TypeHandlerRegistry;
import org.teasoft.honey.osql.util.AnnoUtil;
import org.teasoft.honey.osql.util.DateUtil;
import org.teasoft.honey.util.ObjectCreatorFactory;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

/**
 * Transform Result Util for Mongodb.
 * @author Jade
 * @since  2.0
 */
public class TransformResult {
	
	// 检测是否有Json注解
	private static boolean isJoson(Field field) {
		return AnnoUtil.isJson(field);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object jsonHandlerProcess(Field field, Object obj, TypeHandler jsonHandler) {
		if (List.class.isAssignableFrom(field.getType())) {
			Object newObj[] = new Object[2];
			newObj[0] = obj;
			newObj[1] = field;
			obj = jsonHandler.process(field.getType(), newObj);
		} else {
			obj = jsonHandler.process(field.getType(), obj);
		}
		return obj;
	}
	
	public static <T> JsonResultWrap toJson(MongoCursor<Document> cursor, Class<T> entityClass){
		
		if(cursor==null) return null;
		
		StringBuffer json = new StringBuffer("");
		boolean ignoreNull = HoneyConfig.getHoneyConfig().selectJson_ignoreNull;
		String temp = "";

		boolean dateWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_dateWithMillisecond;
		boolean timeWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_timeWithMillisecond;
		boolean timestampWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_timestampWithMillisecond;
		boolean longToString = HoneyConfig.getHoneyConfig().selectJson_longToString;
        int rowCount=0;
        String fieldName="";
        boolean isJsonString=false;
        Field currField=null;
        while(cursor.hasNext()) {
        	Document document = cursor.next();
			rowCount++;
			json.append(",{");
			for (Map.Entry<String, Object> entry : document.entrySet()) {
				if (entry.getValue() == null && ignoreNull) {
					continue;
				}
				fieldName = _toFieldName(entry.getKey(), entityClass);
				if ("_id".equalsIgnoreCase(fieldName)) {// 替换id为_id
					fieldName = "id";
				}else if(entityClass!=null){//判断存的是否是Json String; 使用了Json即认为是
					try {
						currField = entityClass.getDeclaredField(fieldName);
						isJsonString=isJoson(currField);
					} catch (NoSuchFieldException e) {
						//ignore
					}
				}
						
				json.append("\"");
				json.append(fieldName);
				json.append("\":");

				if (entry.getValue() != null) {
					
					boolean processAsDocument=false;
					temp=entry.getValue().toString();
					if (entry.getValue() instanceof Document) { //Document转成Json String;然后再转目标json的转换
						temp=((Document) entry.getValue()).toJson();
						processAsDocument=true;
					}
					
					if (processAsDocument || isJsonString) {
						json.append(temp);
					} else if (entry.getValue() instanceof ObjectId) {
						json.append("\"");
						json.append(temp);
						json.append("\"");
					} else if (entry.getValue() instanceof String) {
						
						json.append("\"");
						temp=temp.replace("\\", "\\\\"); //1
						temp=temp.replace("\"", "\\\""); //2
						
						json.append(temp);
						json.append("\"");
						
					} else if (entry.getValue() instanceof java.sql.Date) {
						if (dateWithMillisecond) {
							json.append(((java.sql.Date)entry.getValue()).getTime());
						} else {
							try {
								temp = entry.getValue().toString();
								Long.valueOf(temp); //test value
								json.append(temp);
							} catch (NumberFormatException e) {
								json.append("\"");
								json.append(temp.replace("\"", "\\\""));
								json.append("\"");
							}
						}
					} else if (entry.getValue() instanceof java.sql.Time) {
						if (timeWithMillisecond) {
							json.append(((java.sql.Time)entry.getValue()).getTime());
						} else {
							try {
								temp = entry.getValue().toString();
								Long.valueOf(temp); //test value
								json.append(temp);
							} catch (NumberFormatException e) {
								json.append("\"");
								json.append(temp.replace("\"", "\\\""));
								json.append("\"");
							}
						}
					} else if (entry.getValue() instanceof java.sql.Timestamp) {
						if (timestampWithMillisecond) {
							json.append(((java.sql.Time)entry.getValue()).getTime());
						} else {
							try {
								temp = entry.getValue().toString();
								Long.valueOf(temp); //test value
								json.append(temp);
							} catch (NumberFormatException e) {
								json.append("\"");
								json.append(temp.replace("\"", "\\\""));
								json.append("\"");
							}
						}
					} else if (longToString && entry.getValue() instanceof Long) {
						json.append("\"");
						json.append(entry.getValue().toString());
						json.append("\"");
					} else {
						json.append(entry.getValue().toString());
					}

				} else {// null
					json.append(entry.getValue());
				}
				json.append(","); 
			} //one record end
			if(json.toString().endsWith(",")) json.deleteCharAt(json.length()-1); //fix bug
			json.append("}");
		}//array end
		if (json.length() > 0) {
			json.deleteCharAt(0);
		}
		json.insert(0, "[");
		json.append("]");
		
		JsonResultWrap wrap =new JsonResultWrap();
		wrap.setResultJson(json.toString());
		wrap.setRowCount(rowCount);
		
		return wrap;

	}
	
	//要跟声明的字段顺序一样时,要将查的字段也传入.
	public static List<String[]> toListString(MongoCursor<Document> cursor,String selectFields[]) {
		List<String[]> list = new ArrayList<>();
		if (cursor == null) return list;

		boolean nullToEmptyString = HoneyConfig.getHoneyConfig().returnStringList_nullToEmptyString;
		String str[] = null;
		boolean firstRow = true;

		while (cursor.hasNext()) {
			Document document = cursor.next();
			str = new String[selectFields.length];
			for (int i = 0; i < selectFields.length; i++) {
				String key=selectFields[i];
				if (nullToEmptyString && document.get(key) == null) {
					str[i] = "";
				} else {
					Object obj=document.get(key);
					if (obj == null)
						str[i] = null;
					else {
						if (Document.class == obj.getClass()) { // Document要转成Json String
							try {
								str[i] = ((Document) obj).toJson();
							} catch (Exception e) {
								Logger.warn(e.getMessage(), e);
							}
						} else {
							str[i] = obj.toString();
						}
					}
				}
				if (firstRow) { // 2.0
					firstRow = false;
//					regSort(rmeta);
				}
			}
			list.add(str);
		}
		return list;
	}
	
	public static Map<String, Object> doc2Map(Document document) {
		Map<String, Object> map = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : document.entrySet()) {
			map.put(_toFieldName(entry.getKey(), null), entry.getValue());
		}
		return map;
	}
	
	
	private static boolean openFieldTypeHandler = HoneyConfig.getHoneyConfig().openFieldTypeHandler;

	@SuppressWarnings("rawtypes")
	public static <T> T toEntity(Map<String, Object> document, Class<T> entityClass)
			throws Exception {

		if (document == null) return null;

		T targetObj = entityClass.newInstance();
		Field field = null;
		String name = null;
		boolean first = true;
		boolean isId = false;
		Field idField = null;
		for (Map.Entry<String, Object> entry : document.entrySet()) {
			isId = false;
			try {
				name = _toFieldName(entry.getKey(), entityClass);
				if ("_id".equalsIgnoreCase(name)) {// 替换id为_id
					name = "id";
					isId = true;
				}
				field = entityClass.getDeclaredField(name);// 可能会找不到Javabean的字段
			} catch (NoSuchFieldException e) {
				if ("id".equalsIgnoreCase(name)) { //可能主键不叫id,
					isId = true;
					try {
						if (first && idField == null) {
							first = false;
							idField = HoneyUtil.getPkField(entityClass); //继承寻找主键
						}
					} catch (Exception e2) {
                         //no id, skip     
					}
					field = idField;
					if (field == null) continue;
				} else {
					continue;
				}
			}
//  			if(firstRow) { //2.0
//  				firstRow=false;
//  				regSort(rmeta);  片合并后，如何重新排序？？
//  			}

			field.setAccessible(true);
			Object obj = entry.getValue();
			boolean isRegHandlerPriority = false;
			try {
				boolean processAsJson = false;
				// isJoson> isRegHandlerPriority(if open)
				if (isJoson(field)) {
					if (Document.class == obj.getClass()) {
						processAsJson = true;
						obj = toEntity((Document) obj, field.getType());
					} else if (obj.getClass() == String.class) {
						TypeHandler jsonHandler = TypeHandlerRegistry.getHandler(Json.class);
						if (jsonHandler != null) {
							obj = jsonHandlerProcess(field, obj, jsonHandler);
							processAsJson = true;
						}
					}
				} else {
					if (openFieldTypeHandler) {
						isRegHandlerPriority = TypeHandlerRegistry.isPriorityType(field.getType());
					}
				}

				if (processAsJson) {
                        
				} else if (isRegHandlerPriority) {
					obj = TypeHandlerRegistry.handlerProcess(field.getType(), obj);
				} else if (isId && Number.class.isAssignableFrom(field.getType())
						&& MongodbUtil.isMongodbId(obj.toString())) {
					Logger.debug("The id value(" + obj.toString()
							+ ") can not convert to Number, the id value in entity will be null !");
					obj = null;
					
				}else if (obj != null && Document.class == obj.getClass() && field.getType()==String.class) {
//						obj=toMap((Document) obj).toString();
////					obj=JsonUtil.toJson(obj);
					if(obj!=null) {
						String str=obj.toString().trim();
						obj=str.substring(9, str.length()-1);  //del: Document{...}
					}
				} else if (obj != null && Document.class == obj.getClass()
						&& (   field.isAnnotationPresent(Geo2dsphere.class)
						    || field.getType().isAnnotationPresent(Geo2dsphere.class) )) {
					obj = toEntity((Document) obj, field.getType());
					
				} else if (field.getType() == Integer.class && obj != null
						&& obj instanceof Double) {
					obj = ((Double) obj).intValue();
				} else if (obj != null && (!Collection.class.isAssignableFrom(field.getType())
						&& field.getType() != Map.class
						&& field.getType() != java.util.Date.class
						&& field.getType() != java.sql.Date.class
						&& field.getType() != java.sql.Timestamp.class
						&& !HoneyUtil.isNumberArray(field.getType())
						) ) { 
					Object t_obj = ObjectCreatorFactory.create(obj.toString(), field.getType());
					if (t_obj != null) obj = t_obj; // 转换成功才要.
				}else if(field.getType() == java.sql.Timestamp.class && obj.getClass()== java.util.Date.class)  {
					obj=DateUtil.toTimestamp((java.util.Date)obj);
				} 
				
//				java.util.ArrayList
//				[Ljava.lang.Double;
				if (obj != null && List.class.isAssignableFrom(obj.getClass())) {  // not else if
//					if ("[Ljava.lang.Double;".equals(field.getType().getName())) {// List-> Double[]
//					if(isNumArray(field.getType().getName(),0)) {
					if (Double[].class.equals(field.getType())) {
						List list = (List) obj;
						if (list.size() > 0) {
							Double[] arr = (Double[]) list.toArray(new Double[list.size()]);
							if (arr != null) obj = arr;
						}
//					}else if ("[Ljava.lang.Long;".equals(field.getType().getName())) {// List-> Long[]
//					}else if(isNumArray(field.getType().getName(),1)) {
					}if (Long[].class.equals(field.getType())) {
						List list = (List) obj;
						if (list.size() > 0) {
							Long[] arr = (Long[]) list.toArray(new Long[list.size()]);
							if (arr != null) obj = arr;
						}
//					}if ("[Ljava.lang.Integer;".equals(field.getType().getName())) {// List-> Integer[]
//					}if (isNumArray(field.getType().getName(), 2)) {
					}if (Integer[].class.equals(field.getType())) {
						List list = (List) obj;
						if (list.size() > 0) {
							Integer[] arr = (Integer[]) list.toArray(new Integer[list.size()]);
							if (arr != null) obj = arr;
						}
					}
					
//					if(HoneyUtil.isNumberArray(field.getType())){
//						List list = (List) obj;
//						if (list.size() > 0) {
//							Number arr[] = (Number[]) list.toArray(new Number[list.size()]);
////							[Ljava.lang.Number; cannot be cast to [Ljava.lang.Double;
//							if (arr != null) obj = (Double[])arr;
//						}
//					}
				}
				field.set(targetObj, obj); // 对相应Field设置
			} catch (IllegalArgumentException e) {
				Logger.debug(e.getMessage(), e);
			}
		}
		return targetObj;
	}
	
//	private static boolean isNumArray(String type, int index) {
//		String arrayTypes[] = { 
//				"[Ljava.lang.Double;", 
//				"[Ljava.lang.Long;",
//				"[Ljava.lang.Integer;" 
//				};
//		return arrayTypes[index].equals(type);
//	}
	
	
//	private static Map<String, Object> toMap(Map<String, Object> document) {
//		if (document == null || document.size() < 1) return new LinkedHashMap<>();
//
//		Map<String, Object> map = new LinkedHashMap<>();
//		for (Map.Entry<String, Object> entry : document.entrySet()) {
//			map.put(entry.getKey(), entry.getValue());
//		}
//
//		return map;
//	}

	public static <T> List<T> toListEntity(MongoIterable<Document> docIterable, Class<T> entityClass) {
		List<T> list = new ArrayList<>();

		try {
			MongoCursor<Document> cursor = docIterable.iterator();
			while (cursor.hasNext()) {
				Document document = cursor.next();
				list.add(toEntity(document, entityClass));
			}
		} catch (Exception e) {
			if(e instanceof MongoTimeoutException)
			    Logger.warn("Can not connect the Mongodb server. Maybe you did not start the Mongodb server!");
			throw ExceptionHelper.convert(e);
		}

		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private static String _toFieldName(String columnName, Class entityClass) {
		return NameTranslateHandle.toFieldName(columnName, entityClass);
	}
	
	
	static <T> List<T> toListEntity2(List<Document> listDoc, Class<T> entityClass) {
		List<T> list = new ArrayList<>();

		try {
			for (Document doc : listDoc) {
				list.add(toEntity(doc, entityClass));
			}
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(
					"Can not connect the Mongodb server. Maybe you did not start the Mongodb server!");
			throw ExceptionHelper.convert(e);
		}

		return list;
	}

}
