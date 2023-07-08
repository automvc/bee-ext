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

package org.teasoft.beex.android;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.teasoft.bee.app.BeeSqlForApp;
import org.teasoft.bee.osql.SuidType;
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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * 操作Android环境SQLite数据库接口的实现类.
 * 通常不直接使用这个类,而是通过Suid,SuidRich,MoreTable,MapSuid,PreparedSql间接使用.
 * @author Kingstar
 * @since 1.17
 */
public class SqlLibExtForAndroid implements BeeSqlForApp {
	
//	private SQLiteDatabase database=null; // 有事务管理.    close in 2.1.7

	/**
	 * 不推荐在多线程环境下使用;若使用,由使用者保证线程安全.
	 * V2.1.7 改为每个操作都重新获取.
	 * @return SQLiteDatabase instance
	 */
	public SQLiteDatabase getDatabase() {
		
		// 从上下文获取
		Object obj = HoneyContext.getCurrentAppDB();
		if (obj != null) return (SQLiteDatabase) obj;

		SQLiteDatabase database=null;
//		if (database == null || !database.isOpen()) {
		  database = getWritableDB();
		  if (database == null) database = BeeSQLiteDatabaseRegistry.getSQLiteDatabase(); 
//		}//不为null时,则使用原来的
		HoneyContext.setCurrentAppDBIfNeed(database);

		return database;
	}

//	public void setDatabase(SQLiteDatabase database) {
//		this.database = database;
//	}

	private SQLiteDatabase getWritableDB() {
		
		return BeeSQLiteOpenHelper.getWritableDB();
	}

	private static boolean openFieldTypeHandler = HoneyConfig
			.getHoneyConfig().openFieldTypeHandler;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> select(String sql, Class<T> entityClass, String[] selectionArgs) {

		T targetObj = null;
		List<T> rsList = null;
		Map<String, Field> map = null;
		Field field = null;
		String name = null;
		boolean isFirst = true;
		String columnName;
		SQLiteDatabase db = null;
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			int columnCount = cursor.getColumnCount();
			rsList = new ArrayList<>();
			map = new Hashtable<>();

			while (cursor.moveToNext()) {
				targetObj = entityClass.newInstance();

				for (int i = 0; i < columnCount; i++) {
					try {
//						columnName = cursor.getColumnName(i + 1);// 会有异常,但提示不明确. Exception: datatype mismatch
						columnName = cursor.getColumnName(i); // 列下标,从0开始
						name = _toFieldName(columnName, entityClass);
						if (isFirst) {
							field = entityClass.getDeclaredField(name);// 可能会找不到Javabean的字段
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
							obj = cursor.getString(cursor.getColumnIndex(columnName));
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

//						if (! processAsJson) obj = cursor.getString(cursor.getColumnIndex(columnName)); 
						if (!processAsJson) obj = getValue(cursor, field, columnName);

						if (isRegHandlerPriority) {
							obj = TypeHandlerRegistry.handlerProcess(field.getType(), obj);
							field.set(targetObj, obj); // 对相应Field设置
						} else {
							field.set(targetObj, obj); // 对相应Field设置
						}
					} catch (IllegalArgumentException e) {
						boolean alreadyProcess = false;
						obj = cursor.getString(cursor.getColumnIndex(columnName));//SQLite in Android get the String first and then transfer
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

		} catch (android.database.SQLException e) {
			Logger.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw ExceptionHelper.convert(e);
		} catch (InstantiationException e) {
			throw ExceptionHelper.convert(e);
		} finally {
			close(db);
		}

		return rsList;
	}

	private Object getValue(Cursor cursor, Field field, String columnName) {

		int columnIndex = cursor.getColumnIndex(columnName);

		String typeName = field.getType().getName();
		int k = HoneyUtil.getJavaTypeIndex(typeName);
		if ("java.sql.Timestamp".equals(typeName)) { // long???
			k = 1;
		}

		switch (k) {
			case 1:
				return cursor.getString(columnIndex);
			case 2:
				return cursor.getInt(columnIndex);
			case 3:
				return cursor.getLong(columnIndex);
			case 4:
				return cursor.getDouble(columnIndex);
			case 5:
				return cursor.getFloat(columnIndex);
			case 6:
				return cursor.getShort(columnIndex);
//			case 7:
//				return cursor.getByte(columnIndex);
//			case 8:
//				return cursor.getBytes(columnIndex);
//			case 9:
//				return cursor.getBoolean(columnIndex);
			case 10:
				return new BigDecimal(cursor.getString(columnIndex));
//			case 11:
//				return cursor.getDate(columnIndex);
//			case 12:
//				return cursor.getTime(columnIndex);
			case 13:
//				return cursor.getTimestamp(columnIndex);
				return cursor.getLong(columnIndex);
			case 14:
				return cursor.getBlob(columnIndex);
//			case 15:
//				return cursor.getClob(columnIndex);
//			case 16:
//				return cursor.getNClob(columnIndex);
//			case 17:
//				return cursor.getRowId(columnIndex);
//			case 18:
//				return cursor.getSQLXML(columnIndex);

//				19: BigInteger
//				20:char

//				 21:java.util.Date 
//			case 21:	
//				return cursor.getTimestamp(columnIndex);//改动态???
//			case 22:
//				return cursor.getArray(columnIndex);  //java.sql.Array
//			case 23:
//				return cursor.getBinaryStream(columnIndex); //java.io.InputStream
//			case 24:
//				return cursor.getCharacterStream(columnIndex); //java.io.Reader
//			case 25:
//				return cursor.getRef(columnIndex);  //java.sql.Ref	

//			case 27:
//				return cursor.getURL(columnIndex);

//			case 19:
//	        	no  getBigInteger
//			default:
//				return cursor.getObject(columnIndex);

			default:
				return cursor.getString(columnIndex);
		} // end switch

	}

	@Override
	public int modify(String sql, Object[] bindArgs) {
		SuidType regType = HoneyContext.getSuidType();
		if (regType == SuidType.INSERT) {
			long i = insert(sql, bindArgs);
			if (i>0) return 1;
			else return 0;
		} else if (regType == SuidType.UPDATE || regType == SuidType.DELETE) {
			return updateOrDelete(sql, bindArgs);
		} else {
			return execSQL(sql, bindArgs);
		}
	}
	
	private long insert(String sql, Object[] bindArgs) {
		SQLiteDatabase db = null;
		SQLiteStatement st = null;
		long r = 0;
		try {
			db = getDatabase();
			st = db.compileStatement(sql);
			setPreparedValues(st, bindArgs);
			r = st.executeInsert();
		} catch (android.database.SQLException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db, st);
		}

		return r;
	}
	
	private int updateOrDelete(String sql, Object[] bindArgs) {
		SQLiteDatabase db = null;
		SQLiteStatement st = null;
		int r = 0;
		try {
			db = getDatabase();
			st = db.compileStatement(sql);
			setPreparedValues(st, bindArgs);
			r = st.executeUpdateDelete();
		} catch (android.database.SQLException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db, st);
		}

		return r;
	}
	
	private int execSQL(String sql, Object[] bindArgs) {
		SQLiteDatabase db = null;
		int r = 0;
		try {
			db = getDatabase();
			db.execSQL(sql, bindArgs);
			r = getAffectRow(db);
		} catch (android.database.SQLException e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}
		return r;
	}

	private int getAffectRow(SQLiteDatabase db) {
		int result;
		SQLiteStatement st = null;
		try {
			// check how many rows been changed
			st = db.compileStatement("select changes()");
			result = (int) st.simpleQueryForLong();
		} catch (android.database.SQLException e) {
			// ignore the exception and just return 1 if it failed
			Logger.error(e.getMessage(), e);
			result = 1; // todo????
		} finally {
			if (st != null) st.close();
//			close(db); //由上层负责
		}
		return result;
	}



	@Override
	public int batchInsert(String sql0, List<Object[]> listBindArgs) {
		SQLiteDatabase db = null;
		SQLiteStatement st = null;
		int total = 0;
		try {
			db = getDatabase();
			st = db.compileStatement(sql0);
			db.beginTransaction();
			long r;

			for (int i = 0; i < listBindArgs.size(); i++) {
				st.clearBindings();
				setPreparedValues(st, listBindArgs.get(i));
				r = st.executeInsert();
				if (r > 0) total++;
			}
			db.setTransactionSuccessful();
		} catch (android.database.SQLException e) {
		   Logger.error(e.getMessage(), e);
		} catch(Exception e2) {
		  Logger.debug(e2.getMessage(), e2);
		}finally {
			try {
				if (db != null) db.endTransaction();	
			} catch (Exception e) {
				Logger.debug(e.getMessage(), e);
			}
			close(db, st);
		}

		return total;
	}

	private void setPreparedValues(SQLiteStatement st, Object[] bindArgs) {
		if (null != bindArgs && bindArgs.length > 0) {
			for (int i = 0; i < bindArgs.length; i++) {
				int objTypeIndex=-1;
				if (bindArgs[i] != null)
					objTypeIndex = HoneyUtil.getJavaTypeIndex(bindArgs[i].getClass().getName());
				_setPreparedValues(st, objTypeIndex, i, bindArgs[i]);
			}
		}
	}

	private void _setPreparedValues(SQLiteStatement st, int objTypeIndex, int i, Object value) {
		if (null == value) {
			st.bindNull(i + 1);
			return;
		}

		switch (objTypeIndex) {
			case 1:
				st.bindString(i + 1, (String) value);
				break;
			case 2:
//				st.bindInt(i + 1, (Integer) value);
				st.bindLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 3:
				st.bindLong(i + 1, (Long) value);
				break;
			case 4:
				st.bindDouble(i + 1, (Double) value);
				break;
			case 5:
//				st.setFloat(i + 1, (Float) value);
				st.bindDouble(i + 1, Double.parseDouble(value.toString()));
				break;
			case 6:
//				st.bindShort(i + 1, (Short) value);
				st.bindLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 7:
//				st.bindByte(i + 1, (Byte) value);
				st.bindLong(i + 1, Long.parseLong(value.toString()));
				break;
			case 8:
//				st.bindBytes(i + 1, (byte[]) value);
				st.bindBlob(i + 1, (byte[]) value);
				break;
//			case 9:
//				st.bindBoolean(i + 1, (Boolean) value); //??
//				break;
			case 10:
//				st.bindBigDecimal(i + 1, (BigDecimal) value); 
				st.bindString(i + 1, ((BigDecimal) value).toPlainString()); // 可以转成字符串?? 数据库里要是字符串类型
				break;
			case 12:
//				st.bindTime(i + 1, (Time) value);  //可以转成字符串 
				st.bindString(i + 1, value.toString());
				break;
			case 13:
//				st.bindTimestamp(i + 1, (Timestamp) value);  //可以转成Long
				st.bindString(i + 1, value.toString());
				break;
//			case 17:
//				st.bindRowId(i + 1, (RowId) value);
//				break;
//			case 18:
//				st.bindSQLXML(i + 1, (SQLXML) value);
//				break;
			case 20:
				st.bindString(i + 1, value.toString());
				break;

			// 如何起效果??? 没能传到这里 改在传过来前,提前转成String
//			case 26:  //Json Annotation
//			{
//				SetParaTypeConvert converter = SetParaTypeConverterRegistry.getConverter(Json.class);
//				if (converter != null) {
//					st.bindString(i + 1, (String) converter.convert(value));
//					break;
//				}
//			}

			default:
				st.bindString(i + 1, (String) value);
				
				//要支持自定义的  todo
		}
	}

	/**
	 * SQL function: max,min,avg,sum,count. 如果统计的结果集为空,除了count返回0,其它都返回空字符.
	 */
	@Override
	public String selectFun(String sql, String[] selectionArgs) {
		SQLiteDatabase db = null;
		String fun = "";
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			if (cursor.moveToNext()) {
				fun = cursor.getString(0);//获取首个元素
				if (fun == null) fun = "";
			} 
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}

		return fun;
	}

	@Override
	public List<String[]> select(String sql, String[] selectionArgs) {
		SQLiteDatabase db = null;
		List<String[]> list = new ArrayList<>();
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			list = TransformResultSetForAndroid.toStringsList(cursor);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> selectMapList(String sql, String[] selectionArgs) {
		SQLiteDatabase db = null;
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			list = TransformResultSetForAndroid.toMapList(cursor);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}
		return list;
	}

	@Override
	public List<Map<String, String>> selectMapListWithColumnName(String sql,
			String[] selectionArgs) {
		SQLiteDatabase db = null;
		List<Map<String, String>> list = new ArrayList<>();
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			list = TransformResultSetForAndroid.toMapListWithColumnName(cursor);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}
		return list;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public String selectJson(String sql, String[] selectionArgs, Class entityClass) {
		SQLiteDatabase db = null;
		String json = "";
		try {
			db = getDatabase();
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			json = TransformResultSetForAndroid.toJson(cursor, entityClass);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		} finally {
			close(db);
		}
		return json;
	}

	@Override
	public long insertAndReturnId(String sql, Object[] bindArgs) {
		return insert(sql, bindArgs);
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
			Object newObj[] = new Object[2];
			newObj[0] = obj;
			newObj[1] = field;
			obj = jsonHandler.process(field.getType(), newObj);
		} else {
			obj = jsonHandler.process(field.getType(), obj);
		}
		return obj;
	}

	private void close(SQLiteDatabase db) {
		try {
			if (db != null) {
				if (HoneyContext.getCurrentAppDB() == null) // 当前没有连接时,才删除
					db.close();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	private void close(SQLiteDatabase db, SQLiteStatement st) {
		try {
			if (st != null) st.close(); // first
			close(db);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

}
