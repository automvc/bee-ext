/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.type.TypeHandler;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.JsonResultWrap;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.type.TypeHandlerRegistry;
import org.teasoft.honey.osql.util.AnnoUtil;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

/**
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
			Object newOjb[] = new Object[2];
			newOjb[0] = obj;
			newOjb[1] = field;
			obj = jsonHandler.process(field.getType(), newOjb);
		} else {
			obj = jsonHandler.process(field.getType(), obj);
		}
		return obj;
	}
	
	public static <T> JsonResultWrap toJson(MongoCursor<Document> cursor, T entity){
		
		if(cursor==null || entity==null) return null;
		
		StringBuffer json = new StringBuffer("");
		boolean ignoreNull = HoneyConfig.getHoneyConfig().selectJson_ignoreNull;
		String temp = "";

		boolean dateWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_dateWithMillisecond;
		boolean timeWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_timeWithMillisecond;
		boolean timestampWithMillisecond = HoneyConfig.getHoneyConfig().selectJson_timestampWithMillisecond;
		boolean longToString = HoneyConfig.getHoneyConfig().selectJson_longToString;
        int rowCount=0;
        String fieldName="";
//		while (rs.next()) {
        while(cursor.hasNext()) {
        	Document document = cursor.next();
			rowCount++;
			json.append(",{");
			for (Map.Entry<String, Object> entry : document.entrySet()) {
				if (entry.getValue() == null && ignoreNull) {
					continue;
				}
//				System.err.println(entry.getValue().getClass().getName() + entry.getValue());
				fieldName = _toFieldName(entry.getKey(), entity.getClass());
				if ("_id".equalsIgnoreCase(fieldName)) {// 替换id为_id
					fieldName = "id";
				}		
						
				json.append("\"");
				json.append(fieldName);
				json.append("\":");

				if (entry.getValue() != null) {

//					if ("String".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) { 
					if(entry.getValue() instanceof String) {
						json.append("\"");
						//json.append(rs.getString(i));
						temp=entry.getValue().toString();
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
//					} else if ("Time".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
					} else if (entry.getValue() instanceof java.sql.Time) {
						if (timeWithMillisecond) {
//							json.append(rs.getTime(i).getTime());
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
//					} else if ("Timestamp".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
					} else if (entry.getValue() instanceof java.sql.Timestamp) {
						if (timestampWithMillisecond) {
//							json.append(rs.getTimestamp(i).getTime());
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
//					} else if (longToString && "Long".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
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
	public static <T> List<String[]> toListString(MongoCursor<Document> cursor, T entity,String selectFields[]) {
		List<String[]> list = new ArrayList<>();
		if (cursor == null || entity == null) return list;

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
					if (document.get(key) == null)
						str[i] = null;
					else
						str[i] = document.get(key).toString();
				}
				if (firstRow) { // 2.0
					firstRow = false;
//					regSort(rmeta);  //TODO
				}
			}
			list.add(str);
		}
		return list;
	}
	
	/*//要跟声明的字段顺序一样时,要将查的字段也传入.
	public static <T> List<String[]> toListString(MongoCursor<Document> cursor, T entity) {
		List<String[]> list = new ArrayList<>();
		if (cursor == null || entity == null) return list;
	
		int columnCount ;
		boolean nullToEmptyString = HoneyConfig
				.getHoneyConfig().returnStringList_nullToEmptyString;
		String str[] = null;
		boolean firstRow = true;
	
		while (cursor.hasNext()) {
			Document document = cursor.next();
			columnCount=document.size();
			str = new String[columnCount];
			int i = -1;
			for (Map.Entry<String, Object> entry : document.entrySet()) {
				i++;
				if (nullToEmptyString && entry.getValue() == null) {
					str[i] = "";
				} else {
					if (entry.getValue() == null)
						str[i] = null;
					else
						str[i] = entry.getValue().toString();
				}
				if (firstRow) { // 2.0
					firstRow = false;
	//					regSort(rmeta);
				}
			}
			list.add(str);
		}
		return list;
	}*/
	
//	private static boolean openFieldTypeHandler = HoneyConfig.getHoneyConfig().openFieldTypeHandler;
	private static boolean openFieldTypeHandler = false;  //TODO 会有启动问题

	public static <T> T toEntity(Document document, T entity) throws Exception {

//		Set<Map.Entry<String, Object>> set=document.entrySet();
//		int columnCount=set.size();

//      public static <T> T rowToEntity(ResultSet rs, T entity) throws SQLException,IllegalAccessException,InstantiationException {

		T targetObj = (T) entity.getClass().newInstance();
//  		ResultSetMetaData rmeta = rs.getMetaData();

//  		if(rs.isBeforeFirst()) rs.next();

//  		int columnCount = rmeta.getColumnCount();
		Field field = null;
		String name = null;
		boolean firstRow = true;
//  		for (int i = 0; i < columnCount; i++) {

		for (Map.Entry<String, Object> entry : document.entrySet()) {
//  			System.out.println("key: "+entry.getKey());
//  			System.out.println("value: "+entry.getValue());
			try {
				name = _toFieldName(entry.getKey(), entity.getClass());
				if ("_id".equalsIgnoreCase(name)) {// 替换id为_id
					name = "id";
				}
				field = entity.getClass().getDeclaredField(name);// 可能会找不到Javabean的字段
			} catch (NoSuchFieldException e) {
				continue;
			}
//  			if(firstRow) { //2.0
//  				firstRow=false;
//  				regSort(rmeta);
//  			}
			field.setAccessible(true);
			Object obj = null;
			boolean isRegHandlerPriority = false;
			try {
				boolean processAsJson = false;
				if (isJoson(field)) {
//  				obj = rs.getString(i + 1);
					obj = entry.getValue();
//					System.err.println(obj.getClass().getTypeName());
					TypeHandler jsonHandler = TypeHandlerRegistry.getHandler(Json.class);
					if (jsonHandler != null) {
						obj = jsonHandlerProcess(field, obj, jsonHandler);
						processAsJson = true;
					}
				} else {
					if (openFieldTypeHandler) {
						isRegHandlerPriority = TypeHandlerRegistry
								.isPriorityType(field.getType());
					}
				}

//  			if (!processAsJson) obj = rs.getObject(i + 1);
				if (!processAsJson) obj = entry.getValue();
//				System.err.println(obj.getClass().getTypeName());
				if (isRegHandlerPriority) {
					obj = TypeHandlerRegistry.handlerProcess(field.getType(), obj);
					field.set(targetObj, obj); // 对相应Field设置
				} else {
//					System.out.println("--------------------"+obj);
//					obj=(Integer)obj;
					
					if(field.getType()==Integer.class && obj!=null && obj instanceof Double) obj=((Double)obj).intValue();
					else if(field.getType()==BigDecimal.class && obj!=null) obj=new BigDecimal(obj.toString());
					else if(field.getType()==BigInteger.class && obj!=null) obj=new BigDecimal(obj.toString());
					//TODO 类型转换
					
					field.set(targetObj, obj); // 对相应Field设置
				}
			} catch (IllegalArgumentException e) {
				Logger.warn(e.getMessage(), e);
			}
		}
		return targetObj;
	}

	public static <T> List<T> toListEntity(FindIterable<Document> docIterable, T entity) {
		List<T> list = new ArrayList<>();

		try {
			MongoCursor<Document> cursor = docIterable.iterator();
			while (cursor.hasNext()) {
				Document document = cursor.next();
				list.add(toEntity(document, entity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	@SuppressWarnings("rawtypes")
	private static String _toFieldName(String columnName, Class entityClass) {
		return NameTranslateHandle.toFieldName(columnName, entityClass);
	}

}
