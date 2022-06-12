/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.android;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.NameTranslateHandle;

import android.database.Cursor;

/**
 * 为Android的结果转换.
 * @author Kingstar
 * @since 1.17
 */
public class TransformResultSetForAndroid {

	private TransformResultSetForAndroid() {}

//	private static final int NULL = Cursor.FIELD_TYPE_NULL;
	private static final int INTEGER = Cursor.FIELD_TYPE_INTEGER;
	private static final int FLOAT = Cursor.FIELD_TYPE_FLOAT;
	private static final int STRING = Cursor.FIELD_TYPE_STRING;
	private static final int BLOB = Cursor.FIELD_TYPE_BLOB;

	@SuppressWarnings("rawtypes")
	public static String toJson(Cursor cursor, Class entityClass) {
		StringBuffer json = new StringBuffer("");
		int columnCount = cursor.getColumnCount();
		boolean ignoreNull = HoneyConfig.getHoneyConfig().selectJson_ignoreNull;
		String temp = "";

		String columnName;
		String value;
		while (cursor.moveToNext()) {
			json.append(",{");
			for (int i = 1; i <= columnCount; i++) { // 1..n
				columnName = cursor.getColumnName(i);
				value = cursor.getString(cursor.getColumnIndex(columnName));
				if (value == null && ignoreNull) {
					continue;
				}
				json.append("\"");
				json.append(_toFieldName(columnName, entityClass));
				json.append("\":");

				if (value != null) {

//					if ("String".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
					if (cursor.getType(i) == STRING) {
						json.append("\"");
						temp = value;
						temp = temp.replace("\\", "\\\\"); // 1
						temp = temp.replace("\"", "\\\""); // 2

						json.append(temp);
						json.append("\"");
					}

//					else if ("Date".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
//						if (dateWithMillisecond) {
//							json.append(rs.getDate(i).getTime());
//						} else {
//							try {
//								temp = rs.getString(i);
//								Long.valueOf(temp); //test value
//								json.append(temp);
//							} catch (NumberFormatException e) {
//								json.append("\"");
//								json.append(temp.replace("\"", "\\\""));
//								json.append("\"");
//							}
//						}
//					} else if ("Time".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
//						if (timeWithMillisecond) {
//							json.append(rs.getTime(i).getTime());
//						} else {
//							try {
//								temp = rs.getString(i);
//								Long.valueOf(temp); //test value
//								json.append(temp);
//							} catch (NumberFormatException e) {
//								json.append("\"");
//								json.append(temp.replace("\"", "\\\""));
//								json.append("\"");
//							}
//						}
//					} else if ("Timestamp".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
//						if (timestampWithMillisecond) {
//							json.append(rs.getTimestamp(i).getTime());
//						} else {
//							try {
//								temp = rs.getString(i);
//								Long.valueOf(temp); //test value
//								json.append(temp);
//							} catch (NumberFormatException e) {
//								json.append("\"");
//								json.append(temp.replace("\"", "\\\""));
//								json.append("\"");
//							}
//						}
//					}

//					else if (longToString && "Long".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
//						json.append("\"");
//						json.append(value);
//						json.append("\"");
//					} else {
					json.append(value);
//					}

				} else {// null
					json.append(value);
				}

				if (i != columnCount) json.append(","); // bug, if last field is null and ignore.
			} // one record end
			if (json.toString().endsWith(",")) json.deleteCharAt(json.length() - 1); // fix bug
			json.append("}");
		} // array end
		if (json.length() > 0) {
			json.deleteCharAt(0);
		}
		json.insert(0, "[");
		json.append("]");

		return json.toString();
	}

	@SuppressWarnings("rawtypes")
	private static String _toFieldName(String columnName, Class entityClass) {
		return NameTranslateHandle.toFieldName(columnName, entityClass);
	}

	public static List<String[]> toStringsList(Cursor cursor) {
		List<String[]> list = new ArrayList<>();

		int columnCount = cursor.getColumnCount();

		boolean nullToEmptyString = HoneyConfig
				.getHoneyConfig().returnStringList_nullToEmptyString;
		String str[] = null;
		while (cursor.moveToNext()) {
			str = new String[columnCount];
			String columnName;
			String value;
			for (int i = 0; i < columnCount; i++) {
				columnName = cursor.getColumnName(i);
				value = cursor.getString(cursor.getColumnIndex(columnName));
				if (nullToEmptyString && value == null) {
					str[i] = "";
				} else {
					str[i] = value;
				}
			}
			list.add(str);
		}
		return list;
	}

	public static List<Map<String, Object>> toMapList(Cursor cursor) {
		List<Map<String, Object>> list = new ArrayList<>();
		int columnCount = cursor.getColumnCount();
		Map<String, Object> rowMap = null;

		while (cursor.moveToNext()) {
			rowMap = new LinkedHashMap<>();
			String columnName;
			for (int i = 0; i < columnCount; i++) {
				columnName = cursor.getColumnName(i);
				rowMap.put(_toFieldName(columnName, null), getValue(cursor, i)); // ignore Column annotation
			}
			list.add(rowMap);
		}
		return list;
	}

	public static List<Map<String, String>> toMapListWithColumnName(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<>();
		int columnCount = cursor.getColumnCount();
		Map<String, String> rowMap = null;

		while (cursor.moveToNext()) {
			rowMap = new LinkedHashMap<>();
			String columnName;
			for (int i = 0; i < columnCount; i++) {
				columnName = cursor.getColumnName(i);
				rowMap.put(columnName, cursor.getString(i)); // ignore Column annotation
			}
			list.add(rowMap);
		}
		return list;
	}

	private static Object getValue(Cursor cursor, int columnIndex) {

		int type = cursor.getType(columnIndex);

		switch (type) {
			case INTEGER:
				return cursor.getInt(columnIndex);
			case FLOAT:
				return cursor.getFloat(columnIndex);
			case STRING:
				return cursor.getString(columnIndex);
			case BLOB:
				return cursor.getBlob(columnIndex);

			default:
				return cursor.getString(columnIndex);
		}
	}

}
