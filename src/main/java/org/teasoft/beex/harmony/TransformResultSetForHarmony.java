/*
 * Copyright 2020-2022 the original author.All rights reserved.
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

package org.teasoft.beex.harmony;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.NameTranslateHandle;

import ohos.data.resultset.ResultSet;

/**
 * 为Harmony的查询结果转换.
 * @author Kingstar
 * @since 1.17
 */
public class TransformResultSetForHarmony {

	private TransformResultSetForHarmony() {}

	private static final int INTEGER = 1;
	private static final int FLOAT = 2;
	private static final int STRING = 3;
	private static final int BLOB = 4;

	@SuppressWarnings("rawtypes")
	public static String toJson(ResultSet rs, Class entityClass) {
		StringBuffer json = new StringBuffer("");
		int columnCount = rs.getColumnCount();
		boolean ignoreNull = HoneyConfig.getHoneyConfig().selectJson_ignoreNull;
		String temp = "";

		String columnName;
		String value;
		while (rs.goToNextRow()) {
			json.append(",{");
			for (int i = 0; i < columnCount; i++) { // 0..n-1
				columnName = rs.getColumnNameForIndex(i);  //从0开始
				value = rs.getString(i);
				
				if (value == null && ignoreNull) {
					continue;
				}
				json.append("\"");
				json.append(_toFieldName(columnName, entityClass));
				json.append("\":");

				if (value != null) {

//					if ("String".equals(HoneyUtil.getFieldType(rmeta.getColumnTypeName(i)))) {
					if (rs.getColumnTypeForIndex(i).getValue() == STRING) {
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

	public static List<String[]> toStringsList(ResultSet rs) {
		List<String[]> list = new ArrayList<>();

		int columnCount = rs.getColumnCount();

		boolean nullToEmptyString = HoneyConfig
				.getHoneyConfig().returnStringList_nullToEmptyString;
		String str[] = null;
		while (rs.goToNextRow()) {
			str = new String[columnCount];
//			String columnName;
			String value;
			for (int i = 0; i < columnCount; i++) {
//				columnName = rs.getColumnNameForIndex(i);
//				value = rs.getString(rs.getColumnIndex(columnName));
				value = rs.getString(i);
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

	public static List<Map<String, Object>> toMapList(ResultSet rs) {
		List<Map<String, Object>> list = new ArrayList<>();
		int columnCount = rs.getColumnCount();
		Map<String, Object> rowMap = null;

		while (rs.goToNextRow()) {
			rowMap = new LinkedHashMap<>();
			String columnName;
			for (int i = 0; i < columnCount; i++) {
				columnName = rs.getColumnNameForIndex(i);
				rowMap.put(_toFieldName(columnName, null), getValue(rs, i)); // ignore Column annotation
			}
			list.add(rowMap);
		}
		return list;
	}

	public static List<Map<String, String>> toMapListWithColumnName(ResultSet rs) {
		List<Map<String, String>> list = new ArrayList<>();
		int columnCount = rs.getColumnCount();
		Map<String, String> rowMap = null;

		while (rs.goToNextRow()) {
			rowMap = new LinkedHashMap<>();
			String columnName;
			for (int i = 0; i < columnCount; i++) {
				columnName = rs.getColumnNameForIndex(i);
				rowMap.put(columnName, rs.getString(i)); // ignore Column annotation
			}
			list.add(rowMap);
		}
		return list;
	}

	private static Object getValue(ResultSet st, int columnIndex) {

		int type = st.getColumnTypeForIndex(columnIndex).getValue();

		switch (type) {
			case INTEGER:
				return st.getInt(columnIndex);
			case FLOAT:
				return st.getFloat(columnIndex);
			case STRING:
				return st.getString(columnIndex);
			case BLOB:
				return st.getBlob(columnIndex);

			default:
				return st.getString(columnIndex);
		}
	}

}
