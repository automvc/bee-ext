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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.teasoft.bee.app.BeeSqlForApp;
import org.teasoft.bee.osql.annotation.customizable.Json;
import org.teasoft.bee.osql.type.TypeHandler;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.type.TypeHandlerRegistry;
import org.teasoft.honey.osql.util.AnnoUtil;

import ohos.data.rdb.RdbStore;
import ohos.data.rdb.Statement;
import ohos.data.resultset.ResultSet;

/**
 * SqlLib Extend For Harmony.
 * @author Kingstar
 * @since  1.17
 */
public class SqlLibExtForHarmony implements BeeSqlForApp {

	private static boolean openFieldTypeHandler = HoneyConfig
			.getHoneyConfig().openFieldTypeHandler;

	private RdbStore rdbStore; 

	public RdbStore getRdbStore() {

		// 从上下文获取
		Object obj = HoneyContext.getCurrentAppDB();
		if (obj != null) return (RdbStore) obj;

		if (rdbStore == null || !rdbStore.isOpen()) {
			rdbStore = getRdbStoreFromHelper();
			if (rdbStore == null) rdbStore = BeeRdbStoreRegistry.getRdbStore();
		} //esle,则使用原来的
		HoneyContext.setCurrentAppDBIfNeed(rdbStore);

		return rdbStore;
	}

	public void setRdbStore(RdbStore rdbStore) {
		this.rdbStore = rdbStore;
	}

	private RdbStore getRdbStoreFromHelper() {
		return BeeDatabaseHelper.getRdbStore();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> select(String sql, T entity, String[] sqlArgs) {
		T targetObj = null;
		List<T> rsList = null;
		Map<String, Field> map = null;
		Field field = null;
		String name = null;
		boolean isFirst = true;
		String columnName;
		RdbStore db = null; 
		ResultSet rs= null;
		try {
			db = getRdbStore();
			rs = db.querySql(sql, sqlArgs);

			int columnCount = rs.getColumnCount();
			rsList = new ArrayList<>();
			map = new Hashtable<>();

			while (rs.goToNextRow()) {
				targetObj = (T) entity.getClass().newInstance();

				for (int i = 0; i < columnCount; i++) {
					try {
						columnName = rs.getColumnNameForIndex(i); // 列下标,从0开始
						name = _toFieldName(columnName, entity.getClass());
						if (isFirst) {
							field = entity.getClass().getDeclaredField(name);// 可能会找不到Javabean的字段
							map.put(name, field);
						} else {
							field = map.get(name);
							if (field == null) continue;
						}
					} catch (NoSuchFieldException e) {
						continue;
					}
					field.setAccessible(true);
					Object obj = null;
					boolean isRegHandlerPriority = false;

					try {
						boolean processAsJson = false;
						if (isJoson(field)) {
							obj = rs.getString(i);
							TypeHandler jsonHandler = TypeHandlerRegistry
									.getHandler(Json.class);
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

						if (!processAsJson) obj = getValue(rs, field, i);

						if (isRegHandlerPriority) {
							obj = TypeHandlerRegistry.handlerProcess(field.getType(), obj);
							field.set(targetObj, obj); // 对相应Field设置
						} else {
							field.set(targetObj, obj); // 对相应Field设置
						}
					} catch (IllegalArgumentException e) {
						boolean alreadyProcess = false;
						obj = rs.getString(i);
						if (openFieldTypeHandler) {
							Class type = field.getType();
							TypeHandler handler = TypeHandlerRegistry.getHandler(type);
							if (handler != null) {
								try {
									Object newObj = handler.process(type, obj);
									field.set(targetObj, newObj);
									alreadyProcess = true;
								} catch (Exception e2) {
									alreadyProcess = false;
								}
							}
						}
						if (!alreadyProcess) {
							field.set(targetObj, obj);
						}
					}

				}
				rsList.add(targetObj);
				isFirst = false;
			}

//		} catch (SQLException e) {
//			Logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
//			hasException = true;
			throw ExceptionHelper.convert(e);
		} catch (InstantiationException e) {
//		hasException = true;
			throw ExceptionHelper.convert(e);
		} finally {
//			close(db);  
			closeRs(rs);
		}

		return rsList;

	}

	@Override
	public String selectFun(String sql, String[] sqlArgs) {
		String fun = "";
		RdbStore db = null;
		Statement st = null;
		try {
			db = getRdbStore();
			st = db.buildStatement(sql);
			setPreparedValues(st, sqlArgs); // 绑定参数
			fun=st.executeAndGetString();
			if (fun == null) fun = "";
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		} finally {
			close(st, db);
		}
		return fun;
	}

	@Override
	public int modify(String sql, Object[] bindArgs) {
		int r = 0;
		RdbStore db = null;
		Statement st = null;
		try {
			db = getRdbStore();
			st = db.buildStatement(sql);
			setPreparedValues(st, bindArgs);
			r = st.executeAndGetChanges();
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		} finally {
			close(st, db);
		}
		return r;
	}

	@Override
	public List<String[]> select(String sql, String[] sqlArgs) {
		ResultSet rs = null;
		List<String[]> list = new ArrayList<>();
		try {
			rs = getRdbStore().querySql(sql, sqlArgs);
			list = TransformResultSetForHarmony.toStringsList(rs);
		} finally {
			closeRs(rs);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> selectMapList(String sql, String[] sqlArgs) {
		ResultSet rs = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			rs = getRdbStore().querySql(sql, sqlArgs);
			list = TransformResultSetForHarmony.toMapList(rs);
		} finally {
			closeRs(rs);
		}
		return list;
	}

	@Override
	public List<Map<String, String>> selectMapListWithColumnName(String sql, String[] sqlArgs) {
		ResultSet rs = null;
		List<Map<String, String>> list = new ArrayList<>();
		try {
			rs = getRdbStore().querySql(sql, sqlArgs);
			list = TransformResultSetForHarmony.toMapListWithColumnName(rs);
		} finally {
			closeRs(rs);
		}
		return list;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String selectJson(String sql, String[] sqlArgs, Class entityClass) {
		ResultSet rs = null;
		String r = "";
		try {
			rs = getRdbStore().querySql(sql, sqlArgs);
			r = TransformResultSetForHarmony.toJson(rs, entityClass);
		} finally {
			closeRs(rs);
		}
		return r;
	}

	@Override
	public long insertAndReturnId(String sql, Object[] bindArgs) {
		long r = 0;
		RdbStore db = null;
		Statement st = null;
		try {
			db = getRdbStore();
			st = db.buildStatement(sql);
			setPreparedValues(st, bindArgs);
			r = st.executeAndGetLastInsertRowId();
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		} finally {
			close(st, db);
		}
		return r;
	}

	@Override
	public int batchInsert(String sql0, List<Object[]> listBindArgs) {

		if (sql0 == null || listBindArgs == null || listBindArgs.size() < 1) return 0;

		int r = 0;
		RdbStore db = null;
		Statement st = null;
		try {
			db = getRdbStore();
			db.beginTransaction();
			st = db.buildStatement(sql0);
			for (int i = 0; i < listBindArgs.size(); i++) {
				st.clearValues();
				setPreparedValues(st, listBindArgs.get(i));
				r += st.executeAndGetChanges();
			}
			db.markAsCommit();
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		} finally {
			if (db != null) db.endTransaction();
			close(st, db);
		}
		return r;
	}
	
	private void close(Statement st, RdbStore db) {
		try {
			if (st != null) st.close();
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}

//		try {
//			if (db != null) {
//				db.close();  //todo   do not close RdbStore
//			}
//		} catch (Exception e) {
//			Logger.debug(e.getMessage(), e);
//		}
	}
	
	private void closeRs(ResultSet rs) {
		try {
			if(rs!=null) rs.close();
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
	}

	private void setPreparedValues(Statement st, Object[] bindArgs) {
		if (null != bindArgs && bindArgs.length > 0) {
			for (int i = 0; i < bindArgs.length; i++) {
				int objTypeIndex = -1;
				if (bindArgs[i] != null)
					objTypeIndex = HoneyUtil.getJavaTypeIndex(bindArgs[i].getClass().getName());
				_setPreparedValues(st, objTypeIndex, i, bindArgs[i]);
			}
		}
	}

	private void _setPreparedValues(Statement st, int objTypeIndex, int i, Object value) {
		if (value == null) {
			st.setNull(i + 1);
			return;
		}

//		void    setBlob​(int index, byte[] value)	
//		void	setDouble​(int index, double value)	
//		void	setLong​(int index, long value)	
//		void	setNull​(int index)	
//		void	setObject​(int index, Object value)	//但没有getObject
//		void	setString​(int index, String value)	
//		void	setStrings​(String[] stringValues)

		switch (objTypeIndex) {
			case 1:
				st.setString(i + 1, (String) value);
				break;
			case 2:
//				st.setInt(i + 1, (Integer) value);
				st.setLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 3:
				st.setLong(i + 1, (Long) value);
				break;
			case 4:
				st.setDouble(i + 1, (Double) value);
				break;
			case 5:
//				st.setFloat(i + 1, (Float) value);
				st.setDouble(i + 1, Double.parseDouble(value.toString()));
				break;
			case 6:
//				st.setShort(i + 1, (Short) value);
				st.setLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 7:
//				st.setByte(i + 1, (Byte) value);
				st.setLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 8:
//				st.setBytes(i + 1, (byte[]) value);
				st.setBlob(i + 1, (byte[]) value);
				break;
//			case 9:
//				st.setBoolean(i + 1, (Boolean) value); //??
//				break;
			case 10:
//				st.setBigDecimal(i + 1, (BigDecimal) value); 
				st.setString(i + 1, ((BigDecimal) value).toPlainString()); // 可以转成字符串?? 数据库里要是字符串类型
				break;
			case 12:
//				st.setTime(i + 1, (Time) value);  //可以转成字符串 
				st.setString(i + 1, value.toString());
				break;
			case 13:
//				st.setTimestamp(i + 1, (Timestamp) value);
				st.setString(i + 1, value.toString());
				break;
//			case 17:
//				st.setRowId(i + 1, (RowId) value);
//				break;
//			case 18:
//				st.setSQLXML(i + 1, (SQLXML) value);
//				break;
			case 20:
				st.setString(i + 1, value.toString());
				break;

//			case 26:  //Json Annotation
//			{
//				SetParaTypeConvert converter = SetParaTypeConverterRegistry.getConverter(Json.class);
//				if (converter != null) {
//					st.setString(i + 1, (String) converter.convert(value));
//					break;
//				}
//			}

			default:
				st.setObject(i + 1, (String) value);

		}
	}

	@SuppressWarnings("rawtypes")
	private static String _toFieldName(String columnName, Class entityClass) {
		return NameTranslateHandle.toFieldName(columnName, entityClass);
	}

	// 检测是否有Json注解
	private boolean isJoson(Field field) {
		return AnnoUtil.isJson(field);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object jsonHandlerProcess(Field field, Object obj, TypeHandler jsonHandler) {
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

	private Object getValue(ResultSet st, Field field, int columnIndex) {

		String typeName = field.getType().getName();
		int k = HoneyUtil.getJavaTypeIndex(typeName);
		if ("java.sql.Timestamp".equals(typeName)) {
			k = 1;
		}

		switch (k) {
			case 1:
				return st.getString(columnIndex);
			case 2:
				return st.getInt(columnIndex);
			case 3:
				return st.getLong(columnIndex);
			case 4:
				return st.getDouble(columnIndex);
			case 5:
				return st.getFloat(columnIndex);
			case 6:
				return st.getShort(columnIndex);
//			case 7:
//				return st.getByte(columnIndex);
//			case 8:
//				return st.getBytes(columnIndex);
//			case 9:
//				return st.getBoolean(columnIndex);
			case 10:
				return new BigDecimal(st.getString(columnIndex));
//			case 11:
//				return st.getDate(columnIndex);
//			case 12:
//				return st.getTime(columnIndex);
			case 13:
//				return st.getTimestamp(columnIndex);
				return st.getLong(columnIndex);
			case 14:
				return st.getBlob(columnIndex);
//			case 15:
//				return st.getClob(columnIndex);
//			case 16:
//				return st.getNClob(columnIndex);
//			case 17:
//				return st.getRowId(columnIndex);
//			case 18:
//				return st.getSQLXML(columnIndex);

//				19: BigInteger
//				20:char

//				 21:java.util.Date 
//			case 21:	
//				return st.getTimestamp(columnIndex);//改动态???
//			case 22:
//				return st.getArray(columnIndex);  //java.sql.Array
//			case 23:
//				return st.getBinaryStream(columnIndex); //java.io.InputStream
//			case 24:
//				return st.getCharacterStream(columnIndex); //java.io.Reader
//			case 25:
//				return st.getRef(columnIndex);  //java.sql.Ref	

//			case 27:
//				return st.getURL(columnIndex);

//			case 19:
//	        	no  getBigInteger
//			default:
//				return st.getObject(columnIndex);

			default:
				return st.getString(columnIndex);
		} // end switch

	}

}
