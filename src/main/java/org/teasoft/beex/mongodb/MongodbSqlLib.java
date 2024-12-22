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

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.max;
import static com.mongodb.client.model.Accumulators.min;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.teasoft.bee.mongodb.GridFsFile;
import org.teasoft.bee.mongodb.MongoSqlStruct;
import org.teasoft.bee.mongodb.MongodbBeeSql;
import org.teasoft.bee.mongodb.SuidFile;
import org.teasoft.bee.osql.Cache;
import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.ObjSQLException;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.annotation.GridFsMetadata;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.exception.BeeErrorGrammarException;
import org.teasoft.bee.osql.exception.BeeIllegalBusinessException;
import org.teasoft.bee.osql.exception.NotSupportedException;
import org.teasoft.beex.json.JsonUtil;
import org.teasoft.beex.mongodb.ds.MongoContext;
import org.teasoft.beex.mongodb.ds.SingleMongodbFactory;
import org.teasoft.beex.osql.mongodb.CreateIndex;
import org.teasoft.beex.osql.mongodb.GeoFind;
import org.teasoft.beex.osql.mongodb.IndexPair;
import org.teasoft.beex.osql.mongodb.IndexType;
import org.teasoft.honey.database.DatabaseClientConnection;
import org.teasoft.honey.osql.core.AbstractBase;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.ConditionImpl.FunExpress;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.JsonResultWrap;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.core.StringConst;
import org.teasoft.honey.osql.mongodb.MongoConditionHelper;
import org.teasoft.honey.osql.name.NameUtil;
import org.teasoft.honey.sharding.ShardingReg;
import org.teasoft.honey.sharding.ShardingUtil;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingDdlEngine;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingSelectEngine;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingSelectFunEngine;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingSelectJsonEngine;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingSelectListStringArrayEngine;
import org.teasoft.honey.sharding.engine.mongodb.MongodbShardingUpdateOrDeleteEngine;
import org.teasoft.honey.util.ObjectUtils;
import org.teasoft.honey.util.StreamUtil;
import org.teasoft.honey.util.StringParser;
import org.teasoft.honey.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoGridFSException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;

/**
 * Mongodb SqlLib.
 * @author Jade
 * @author Kingstar
 * @since  2.0
 * support output mongo shell command
 * @since  2.1
 */
public class MongodbSqlLib extends AbstractBase
		implements MongodbBeeSql, SuidFile, CreateIndex, GeoFind, Serializable {
	
	private static final long serialVersionUID = 1596710362261L;
	
	private static final String IDKEY = "_id";

	private DatabaseClientConnection getConn() {
		if (!HoneyConfig.getHoneyConfig().multiDS_enable) {
			return null;
		} else {
			return HoneyContext.getDatabaseConnection();
		}
	}

	private MongoDatabase getMongoDatabase(DatabaseClientConnection conn) {
		if (conn == null) { //TODO
			return SingleMongodbFactory.getMongoDb(); // 单个数据源时,
		}
		return (MongoDatabase) conn.getDbConnection();
	}
	
	private boolean isShardingMain() {//有分片(多个)
		return   HoneyContext.getSqlIndexLocal() == null && ShardingUtil.hadSharding(); //前提要是HoneyContext.hadSharding()
	}
	
	@Override
	public <T> List<T> select(T entity) {
		
		return select(entity, null);
	}
	
	@SuppressWarnings("unchecked")
	private <T> Class<T> toClassT(T entity) {
		return (Class<T>)entity.getClass();
	}

	@Override
	public <T> int update(T entity) {
//		checkShardingSupport(); 
		if (entity == null) return -1;
		String tableName = _toTableName(entity);
		BasicDBObject doc = null;
		try {

			String pkName = HoneyUtil.getPkFieldName(entity);
			if ("".equals(pkName)) pkName = "id";
			String pks[] = pkName.split(",");
			StringUtils.trim(pks);

			if (pks.length < 1) throw new ObjSQLException(
					"ObjSQLException: in the update(T entity) or update(T entity,IncludeType includeType), the id field is missing !");

			Map<String, Object> map = ParaConvertUtil.toMap(entity);

			BasicDBObject filter = new BasicDBObject();
			String column = "";
			for (int i = 0; i < pks.length; i++) {
				column = pks[i];
				if ("id".equalsIgnoreCase(column)) {// 替换id为_id
					column = IDKEY;
				}
				filter.append(column, map.get(column));
				map.remove(column);
			}

			doc = newDBObject(map);
			BasicDBObject updateDocument = new BasicDBObject("$set", doc);

			MongoSqlStruct struct = new MongoSqlStruct("int", tableName, filter, null, null, null, null, false,
					entity.getClass(), updateDocument);
			struct.setSuidType(SuidType.UPDATE);
			
			return updateOrDelete(struct);

		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
	}
	
	// 2.5.0
	private int _update(MongoSqlStruct struct, boolean isClearCache) {
		int num = 0;
		String sql = null;
		DatabaseClientConnection conn = null;
		try {
			sql = struct.getSql();
			initRoute(SuidType.MODIFY, struct.getEntityClass(), sql);

			logSQLForMain("Mongodb::update: " + sql);
			logUpdate(struct);

			HoneyContext.addInContextForCache(sql, struct.getTableName());

			conn = getConn();

			ClientSession session = getClientSession();

			BasicDBObject filter = (BasicDBObject) struct.getFilter();
			BasicDBObject updateDocument = (BasicDBObject) struct.getUpdateSetOrInsertOrFunOrOther();

			String tableName = struct.getTableName();
			UpdateResult rs = null;
			if (session == null)
				rs = getMongoDatabase(conn).getCollection(tableName).updateMany(filter, updateDocument);
			else
				rs = getMongoDatabase(conn).getCollection(tableName).updateMany(session, filter, updateDocument);

			if (rs != null) {
				long updateCount = rs.getModifiedCount();
				if (updateCount > Integer.MAX_VALUE) {
					Logger.error(
							"in MongodbSqlLib._update method, affect num(long) more than Integer.MAX_VALUE, it will have error when long convert to int!");
				}
				num = (int) updateCount;
			}

			return num;
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			logAffectRow(num);
			if (isClearCache) clearInCache(sql, "int", SuidType.MODIFY, num);
			close(conn);
		}
	}

	@Override
	public <T> int insert(T entity) {
		String tableName = _toTableName(entity);
		Document doc = null;
		DatabaseClientConnection conn =null;
		String sql="";
		int num = 0;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity, -1, SuidType.INSERT);
			
			conn = getConn();
			MongoDatabase db=getMongoDatabase(conn);
			
			_storeFile(map,db); //处理保存文件
			
			doc = newDoc(map);
			MongoSqlStruct struct = new MongoSqlStruct("int", tableName, null, null, null,
					null, null, false,entity.getClass(),newDBObject(map)); //insert 放在updateSet
			sql=struct.getSql();
			initRoute(SuidType.MODIFY, struct.getEntityClass(), sql);
			logSQLForMain("Mongodb::insert: "+sql);
			logInsertOne(struct);
//			HoneyContext.addInContextForCache(sql, struct.getTableName());
			_addInContextForCache(struct);
	
			ClientSession session = getClientSession();
			if (session == null)
				db.getCollection(tableName).insertOne(doc);
			else
				db.getCollection(tableName).insertOne(session, doc);
			
			num=1; //有异常不会执行到这
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			boolean notCatch=HoneyConfig.getHoneyConfig().notCatchModifyDuplicateException;
			if (!notCatch && isConstraint(e)) { //内部捕获并且是重复异常,则由Bee框架处理 
				boolean notShow=HoneyConfig.getHoneyConfig().notShowModifyDuplicateException;
				if(! notShow) Logger.warn(e.getMessage());
				return num;
			}
			throw ExceptionHelper.convert(e);
		} finally {
			logAffectRow(num);
			clearInCache(sql, "int", SuidType.MODIFY, num);
			close(conn);
		}
		return num;
	}
	
	@SuppressWarnings("unchecked")
	private void _storeFile(Map<String, Object> map, MongoDatabase database) {
		if (map.containsKey(StringConst.GridFs_FileId)) {
			String fileColumnName = (String) map.get(StringConst.GridFs_FileColumnName);
			String filename_key = (String) map.get(StringConst.GridFs_FileName);
			String filename_value = (String) map.get(filename_key);
//			InputStream source = (InputStream) map.get(fileColumnName);
			Object source0 =  map.get(fileColumnName);
			InputStream source=null;
			
			if (byte[].class.equals(source0.getClass())) {
				source =StreamUtil.byteArray2Stream((byte[])source0);
//			} else if (InputStream.class.equals(source0.getClass())) {
			} else if (InputStream.class.isAssignableFrom(source0.getClass())) {
				source = (InputStream) source0;
			} 

			// 如何区分哪些是metadataMap??? 使用注解?? 标明哪些字段作为Metadata??    使用GridFsMetadata注解标注的Map<String,Object>
			Map<String, Object> metadataMap = (Map<String, Object>) map.get(GridFsMetadata.class.getName());

			String fileid = _uploadFile(filename_value, source, metadataMap, database);
			map.put((String) map.get(StringConst.GridFs_FileId), fileid); // 将返回的fileid,存到保存它的字段

			// 文件已另外存,这些不需要了
			map.remove(StringConst.GridFs_FileId);
			map.remove(StringConst.GridFs_FileName);
			map.remove(StringConst.GridFs_FileColumnName);
			map.remove(fileColumnName);
			map.remove(GridFsMetadata.class.getName());
		}
	}
	
	@Override
	public <T> int delete(T entity) {
		return delete(entity, null);
	}

	@Override
	public <T> List<T> select(T entity, Condition condition) {
		if (condition != null &&  Boolean.TRUE.equals(condition.hasGroupBy())) {
			return selectWithGroupBy(entity, condition);
		} else {

			if (entity == null) return Collections.emptyList();

			MongoSqlStruct struct = parseMongoSqlStruct(entity, condition, "List<T>");
			Class<T> entityClass = toClassT(entity);
			
			List<T> list= select(struct, entityClass);
			
			fillGridFs(entityClass, condition, list); //文件没有放缓存;每次都是重新获取；       //上下文处理？？
			
			return list;
		}
	}
	
	private <T> void fillGridFs(Class<T> entityClass, Condition condition, List<T> list) {
		
		Map<String, Object> map = ParaConvertUtil.toMapForGridFsSelect(entityClass, getIncludeType(condition));
		if (map == null || map.size() == 0) return;

		if (map.containsKey(StringConst.GridFs_FileId)) {
			String fileColumnName = (String) map.get(StringConst.GridFs_FileColumnName);
			String filename_name = (String) map.get(StringConst.GridFs_FileName); // 存文件名的字段名
			String fileid_name = (String) map.get(StringConst.GridFs_FileId); // 存文件id的字段名

			boolean isByteArray = false;
			boolean isInputStream = false;
			boolean isFirst = true;

			byte[] data = null;
			for (T t : list) { //更新查询到的list
				try {
					Field field3 = HoneyUtil.getField(t.getClass(),fileColumnName);
					if (isFirst) {
						isFirst = false;
						if (byte[].class.equals(field3.getType())) {
							isByteArray = true;
						} else if (InputStream.class.equals(field3.getType())) {
							isInputStream = true;
						} else {
							break;
						}
					}

					Field field = HoneyUtil.getField(t.getClass(),fileid_name);
					HoneyUtil.setAccessibleTrue(field);
					String fileid_value = (String) field.get(t);
					if (StringUtils.isNotBlank(fileid_value)) {
						// 上下文处理？？
						data = getFileById(fileid_value); // fileid_value为GridFs文件对应的fileid,插入后会存入实体表对应的字段fileid_name(若有);查询时即可使用
					} else {
						Field field2 = HoneyUtil.getField(t.getClass(),filename_name); // GridFs文件对应的文件名字段的名称
						HoneyUtil.setAccessibleTrue(field2);	
						String filename_value = (String) field2.get(t); // 文件名的值:filename_value
						if (StringUtils.isNotBlank(filename_value)) {
							data = getFileByName(filename_value);
						}
					}

					HoneyUtil.setAccessibleTrue(field3);
					if (isByteArray) {
						HoneyUtil.setFieldValue(field3, t, data);
					} else if (isInputStream) {
						HoneyUtil.setFieldValue(field3, t, StreamUtil.byteArray2Stream(data));
					}

				} catch (Exception e) {
                    Logger.debug(e.getMessage(), e);
				}
			}
		}
	}
	
	
	@Override
	public <T> List<T> selectById(Class<T> entityClass, Object id) {

		String tableName = _toTableNameByClass(entityClass);

		Object[] obj = processId(entityClass, id);
		BasicDBObject one = (BasicDBObject) obj[0];
		Bson moreFilter = (Bson) obj[1];
		Bson filter = null;
		if (moreFilter != null)
			filter = moreFilter;
		else
			filter = one;

		MongoSqlStruct struct = new MongoSqlStruct("List<T>", tableName, filter, null, null,
				null, null, true,entityClass);

		List<T> list= select(struct, entityClass);
		fillGridFs(entityClass, null, list); 
		
		return list;
		
	}
	
	@Override
	public <T> List<T> selectOrderBy(T entity, String orderFields, OrderType[] orderTypes) {
		String tableName = _toTableName(entity);
		
		Bson filter = toDocument(entity);
		String ofs[]=orderFields.split(",");
		StringUtils.trim(ofs);
		Bson sortBson = ParaConvertUtil.toSortBson(ofs, orderTypes);
		
		Class<T> entityClass = toClassT(entity);
		MongoSqlStruct struct = new MongoSqlStruct("List<T>", tableName, filter, sortBson, null,
				null, null, true,entityClass);
		
		List<T> list= select(struct, entityClass);
		fillGridFs(entityClass, null, list); 
		
		return list;
	}
	
	//用于判断单源和分片的, selectById也可以用
	@Override
	public <T> List<T> select(MongoSqlStruct struct, Class<T> entityClass) {
		if (!ShardingUtil.hadSharding()) {
			return _select(struct, entityClass); // 不用分片走的分支
		} else {
			
			if (HoneyContext.getSqlIndexLocal() == null) { //分片,主线程
				
				List<T> list =_select(struct, entityClass); //检测缓存的           
				if (list != null) {// 若缓存是null,就无法区分了,所以没有数据,最好是返回空List,而不是null
					logDsTab();
					return list; 
				}
				ShardingReg.regShadingPage("", "", struct.getStart(), struct.getSize());
				List<T> rsList = new MongodbShardingSelectEngine().asynProcess(entityClass, this, struct);//此处会设置真正的table
				addInCache(struct.getSql(), rsList, rsList.size());
				logSelectRows(rsList.size());
				return rsList;
				
			} else { // 子线程执行
				return _select(struct, entityClass);
			}
		}
	}
	
	private static final String JUST_SUPPORT_UPDATE_DELETE = "updateOrDelete method just support update and delete type";

	// 2.5.0
	@Override
	public int updateOrDelete(MongoSqlStruct struct) {

		String sql = struct.getSql();
		if (sql == null || "".equals(sql)) return -1;
		boolean isClearCache;
		if (!ShardingUtil.hadSharding()) {// 1.x版本及不用分片走的分支
			isClearCache = true;
			if (struct.getSuidType() == SuidType.DELETE)
				return _delete(struct, isClearCache);
			else if (struct.getSuidType() == SuidType.UPDATE)
				return _update(struct, isClearCache);
			else
				throw new NotSupportedException(JUST_SUPPORT_UPDATE_DELETE);
		} else {
			if (HoneyContext.getSqlIndexLocal() == null) {// 拦截到的要分片的主线程
				try {
//					HoneyContext.addInContextForCache(sql, struct.getTableName());
					_addInContextForCache(struct);
					int num = new MongodbShardingUpdateOrDeleteEngine().asynProcess(this, struct);
					logAffectRow(num);
					clearInCache(sql, "int", SuidType.MODIFY, num); // 父线程才清缓存
					return num;
				} finally {
					clearContext(sql); // 分片的主线程都要清主线程的上下文
				}
			} else { // 子线程执行
				isClearCache = false;
				if (struct.getSuidType() == SuidType.DELETE)
					return _delete(struct, isClearCache);
				else if (struct.getSuidType() == SuidType.UPDATE)
					return _update(struct, isClearCache);
				else
					throw new NotSupportedException(JUST_SUPPORT_UPDATE_DELETE);
			}
		}
	}
	
//	单表查,一次只涉及一张表
	@SuppressWarnings("unchecked")
	private <T> List<T> _select(MongoSqlStruct struct, Class<T> entityClass) {

		String sql = struct.getSql();
		logSQLForMain("Mongodb::select: " + sql);
		log(struct);
		
//		String tableName=struct.getTableName();
//		tableName =ShardingUtil.appendTableIndexIfNeed(tableName);
//		HoneyContext.addInContextForCache(sql, tableName);
		_addInContextForCache(struct);
		
//		boolean isReg  //不需要添加returnType判断,因MongoSqlStruct已有returnType
//		initRoute(SuidType.SELECT, entityClass, sql);
		Object cacheObj = getCache().get(sql);
		if (cacheObj != null) {
			clearContext(sql);
			List<T> list = (List<T>) cacheObj;
			logSelectRows(list.size());
			return list;
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.

		List<T> rsList = null;

		try {
			FindIterable<Document> docIterable = findIterableDocument(struct);
			rsList = TransformResult.toListEntity(docIterable, entityClass);

			addInCache(sql, rsList, rsList.size());
			logSelectRows(rsList.size());
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}

		return rsList;
	}
	
	private void _addInContextForCache(MongoSqlStruct struct) {
		String tableName = struct.getTableName();
		tableName = ShardingUtil.appendTableIndexIfNeed(tableName);
		HoneyContext.addInContextForCache(struct.getSql(), tableName);
	}
	
	private void _addInContextForCache(String sql, String tableName) {
		HoneyContext.addInContextForCache(sql, tableName);
	}
	
	@Override
	public <T> String selectJson(T entity, Condition condition) {
		if (entity == null) return null;
		
		MongoSqlStruct struct = parseMongoSqlStruct(entity, condition, "StringJson");
		Class<T> entityClass = toClassT(entity);
		
		return selectJson(struct, entityClass);
	}
	
	@Override
	public <T> String selectJson(MongoSqlStruct struct, Class<T> entityClass) {

		String sql = struct.getSql();
		
		if (!ShardingUtil.hadSharding()) { // 无分片
			return _selectJson(struct, entityClass);
		} else { // 有分片
			if (HoneyContext.getSqlIndexLocal() == null) { // 有分片的主线程

				String cacheValue = _selectJson(struct, entityClass); // 检测缓存的
				if (cacheValue != null) {
					logDsTab();
					return cacheValue;
				}
				ShardingReg.regShadingPage("", "", struct.getStart(), struct.getSize());
				JsonResultWrap wrap = new MongodbShardingSelectJsonEngine().asynProcess(entityClass, this, struct); // 应该还要传suid类型
				logSelectRows(wrap.getRowCount());
				String json = wrap.getResultJson();
				addInCache(sql, json, -1); // 没有作最大结果集判断

				return json;
			} else { // 子线程执行
				return _selectJson(struct, entityClass);
			}
		}
	}

	private <T> String _selectJson(MongoSqlStruct struct, Class<T> entityClass) {
		String sql = struct.getSql();
		logSQLForMain("Mongodb::selectJson: "+sql);
		log(struct);
//		HoneyContext.addInContextForCache(sql, struct.getTableName());
		_addInContextForCache(struct);
		
		initRoute(SuidType.SELECT, entityClass, sql);
		Object cacheObj = getCache().get(sql); // 这里的sql还没带有值
		if (cacheObj != null) {
			clearContext(sql);
			return (String) cacheObj;
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.

		String json = "";

		try {
			FindIterable<Document> docIterable = findIterableDocument(struct);

			JsonResultWrap wrap = TransformResult.toJson(docIterable.iterator(), entityClass);
			json = wrap.getResultJson();

			logSelectRows(wrap.getRowCount());
			addInCache(sql, json, -1); // 没有作最大结果集判断
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}

		return json;
	}
	
	@Override
	public <T> List<String[]> selectString(T entity, Condition condition) {
		if (entity == null) return Collections.emptyList();
		
		MongoSqlStruct struct = parseMongoSqlStruct(entity, condition, "List<String[]>");
		Class<T> entityClass = toClassT(entity);
		
		return selectString(struct, entityClass);
		
	}
	
	
	@Override
	public <T> List<String[]> selectString(MongoSqlStruct struct, Class<T> entityClass) {
	
		String sql = struct.getSql();
		
		if (!ShardingUtil.hadSharding()) {
			return _selectString(struct, entityClass); // 不用分片走的分支
		} else {
			if (HoneyContext.getSqlIndexLocal() == null) {
				List<String[]> list = _selectString(struct, entityClass); // 检测缓存的
				if (list != null) {
					logDsTab();
					return list;
				}
				ShardingReg.regShadingPage("", "", struct.getStart(), struct.getSize());
				List<String[]> rsList = new MongodbShardingSelectListStringArrayEngine().asynProcess(entityClass, this, struct);
				addInCache(sql, rsList, rsList.size());

				return rsList;

			} else { // 子线程执行
				return _selectString(struct, entityClass);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> List<String[]> _selectString(MongoSqlStruct struct, Class<T> entityClass) {
		
		String sql = struct.getSql();
		logSQLForMain("Mongodb::selectString: "+sql);
		log(struct);
//		HoneyContext.addInContextForCache(sql, struct.getTableName());
		_addInContextForCache(struct);
		
		initRoute(SuidType.SELECT, entityClass, sql);
		Object cacheObj = getCache().get(sql); // 这里的sql还没带有值
		if (cacheObj != null) {
			clearContext(sql);
			List<String[]> list = (List<String[]>) cacheObj;
			logSelectRows(list.size());
			return list;
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.

		List<String[]> list = null;

		try {
			FindIterable<Document> docIterable = findIterableDocument(struct);
			list = TransformResult.toListString(docIterable.iterator(), struct.getSelectFields());

			logSelectRows(list.size());
			addInCache(sql, list, list.size());
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}

		return list;
	}
	
	private <T> MongoSqlStruct parseMongoSqlStruct(T entity, Condition condition, String returnType) {

		if (condition != null) condition.setSuidType(SuidType.SELECT);

		String tableName = _toTableName(entity);
		BasicDBObject filter = toDBObjectForFilter(entity, condition);
		Bson sortBson = ParaConvertUtil.toSortBson(condition);
		
		Integer size = null;
		Integer start = null;
		String[] selectFields = null;
		boolean hasId = false;
		
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		if (condition != null) {
			size = conditionImpl.getSize();
			start = conditionImpl.getStart();
			selectFields = conditionImpl.getSelectField();
			if (selectFields != null) {
				if (selectFields.length == 1) selectFields = selectFields[0].split(",");
				StringUtils.trim(selectFields);
				for (int i = 0; i < selectFields.length; i++) {
					if ("id".equalsIgnoreCase(selectFields[i])) {
						selectFields[i] = IDKEY;
						hasId = true;
						break;
					}
				}
			}
		}
	
		return new MongoSqlStruct(returnType, tableName, filter, sortBson, start, size, selectFields, hasId, entity.getClass());
	}

	private <T> FindIterable<Document> findIterableDocument(MongoSqlStruct struct) {

		String tableName = struct.getTableName();
		Bson filter = (Bson)struct.getFilter();
		Bson sortBson = (Bson)struct.getSortBson();

		Integer size = struct.getSize();
		Integer start = struct.getStart();
		String[] selectFields = struct.getSelectFields();
		StringUtils.trim(selectFields);
		boolean hasId = struct.isHasId();

		DatabaseClientConnection conn = getConn();
		FindIterable<Document> docIterable = null;

		try {
			MongoCollection<Document> collection=getMongoDatabase(conn).getCollection(tableName);
			ClientSession session = getClientSession();
			if (session == null) {
				if (filter != null)
					docIterable = collection.find(filter);
				else
					docIterable = collection.find();
			} else {
				if (filter != null)
					docIterable = collection.find(session,filter);
				else
					docIterable = collection.find(session);
			}

			if (sortBson != null) docIterable = docIterable.sort(sortBson); //And Filter{filters=[Document{{_id=1}}, Document{{userid=1}}]}

			if (size != null && size > 0) {
				if (start == null || start < 0) start = 0;
				docIterable = docIterable.skip(start).limit(size);
			}
			if (selectFields != null) {
				if (hasId)
					docIterable = docIterable.projection(fields(include(selectFields)));
				else
					docIterable = docIterable
							.projection(fields(include(selectFields), excludeId()));
			}
		} finally {
			close(conn);
		}

		return docIterable;
	}

	@Override
	public <T> int delete(T entity, Condition condition) {
//		checkShardingSupport();
		String tableName = _toTableName(entity);
		BasicDBObject filter = toDBObjectForFilter(entity, condition);

		MongoSqlStruct struct = new MongoSqlStruct("int", tableName, filter, null, null, null, null, false,
				entity.getClass());

		struct.setSuidType(SuidType.DELETE);
		return updateOrDelete(struct);
	}

	@Override
	public <T> int update(T oldEntity, T newEntity) {
		String tableName = _toTableName(oldEntity);
		try {
			Map<String, Object> oldMap = ParaConvertUtil.toMap(oldEntity);
			Map<String, Object> newMap = ParaConvertUtil.toMap(newEntity);
			return update(oldMap, newMap, tableName, null, oldEntity);
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
	}

	@Override
	public <T> int update(T entity, Condition condition, String... setFields) {
		String tableName = _toTableName(entity);
		Map<String, Object> reMap[] = toMapForUpdateSet(entity, condition, setFields);
		return update(reMap[0], reMap[1], tableName, condition, entity);
	}

	@Override
	public <T> int updateBy(T entity, Condition condition, String... whereFields) {
		String tableName = _toTableName(entity);
		Map<String, Object> reMap[] = toMapForUpdateBy(entity, condition, whereFields);
		return update(reMap[0], reMap[1], tableName, condition, entity);
	}
	
	private <T> int update(Map<String, Object> filterMap, Map<String, Object> newMap, String tableName,
			Condition condition, T entity) {
//		checkShardingSupport();
		BasicDBObject oldDoc = null;
		BasicDBObject newDoc = null;
		BasicDBObject updateSet = new BasicDBObject();
		boolean hasNewValue = false;
		try {

			boolean notUpdateWholeRecords = HoneyConfig.getHoneyConfig().notUpdateWholeRecords;
			if (notUpdateWholeRecords && ObjectUtils.isEmpty(filterMap)) {
				throw new BeeIllegalBusinessException(
						"BeeIllegalBusinessException: It is not allowed update whole documents(records) in one collection(table). If need, you can change the config in bee.osql.notUpdateWholeRecords !");
			}

			if (filterMap == null) filterMap = new HashMap<>();

			oldDoc = new BasicDBObject(filterMap); // filter
//			List<Bson> updateBsonList=new ArrayList<>();

			if (newMap != null && newMap.size() > 0) {
				newDoc = new BasicDBObject(newMap);
				hasNewValue = true;
//				updateBsonList.add(new BasicDBObject("$set", newDoc));
			}

			List<DBObject> updateSetBsonList = MongoConditionUpdateSetHelper.processConditionForUpdateSet(condition);
			if (updateSetBsonList != null) {
//				updateBsonList.addAll(updateSetBsonList);

				DBObject setObject = updateSetBsonList.get(0);
				DBObject incObject = updateSetBsonList.get(1);
				DBObject mulObject = updateSetBsonList.get(2);

				if (setObject != null) {
					if (newDoc != null)
						newDoc.putAll(setObject);
					else
						newDoc = (BasicDBObject) setObject;
				}

//				DBObject newObj = new BasicDBObject();

				if (newDoc != null) {
					updateSet.put("$set", newDoc);
					hasNewValue = true;
				}
				if (incObject != null) {
					updateSet.put("$inc", incObject);
					hasNewValue = true;
				}
				if (mulObject != null) {
					updateSet.put("$mul", mulObject);
					hasNewValue = true;
				}
			} else if (newDoc != null) {
				updateSet.put("$set", newDoc);
			}

			if (!hasNewValue) {
				throw new BeeErrorGrammarException("The update set part is empty!");
			}

			MongoSqlStruct struct = new MongoSqlStruct("int", tableName, oldDoc, null, null, null, null, false,
					entity.getClass(), updateSet); // this method no entityClass

			struct.setSuidType(SuidType.UPDATE);
			return updateOrDelete(struct);
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
	}

	private <T> Map<String, Object>[] toMapForUpdateBy(T entity, Condition condition, String... specialFields) {
		return toMapForUpdate(entity, condition, true, specialFields);
	}

	private <T> Map<String, Object>[] toMapForUpdateSet(T entity, Condition condition, String... specialFields) {
		return toMapForUpdate(entity, condition, false, specialFields);
	}
	
	private String[] adjustVariableString(String... fieldList) {

		if (fieldList == null) return new String[] { "" };

		String fields[];

		if (fieldList.length == 1) { // 变长参数,只有一个时,才允许用逗号隔开
			fields = fieldList[0].split(",");
		} else {
			fields = fieldList;
		}
		StringUtils.trim(fields);
		return fields;
	}

//	没指定为whereFields的字段,作为set部分(默认只处理非空,非null的字段)
//	condition中op,between,notBetween方法设置的字段,不受includeType的值影响
	@SuppressWarnings("unchecked")
	private <T> Map<String, Object>[] toMapForUpdate(T entity, Condition condition,
			boolean isFilterField, String... specialFields) {
//		checkShardingSupport();
		Map<String, Object> reMap[] = new Map[2];
		try {
			if (condition != null) condition.setSuidType(SuidType.UPDATE);
			Map<String, Object> entityMap = ParaConvertUtil.toMap(entity, getIncludeType(condition));

			Map<String, Object> filterMapFromC = MongoConditionHelper.processCondition(condition);
//			Map<String, Object> setMapFromC = MongoConditionHelper.processCondition(condition); // 只获取set的部分     condition set的部分,在下一方法才获取

//			String fields[] = specialFields.split(",");
			String fields[] = adjustVariableString(specialFields);
			
			Map<String, Object> specialMap = new LinkedHashMap<String, Object>();
			for (int i = 0; i < fields.length; i++) {
				fields[i] = _toColumnName(fields[i], entity.getClass());
				
				if ("id".equalsIgnoreCase(fields[i])) {// 替换id为_id   fixed bug v2.0.2.14
					fields[i] = "_id";
				}
				
//				entityMap分为两部分, 先找出特殊的部分
				if (entityMap.containsKey(fields[i])) { //将entity的字段转到filter,作为过滤
						specialMap.put(fields[i], entityMap.get(fields[i]));
						entityMap.remove(fields[i]);
				}
				
//				Condition.set(arg1,arg2) 另外设置的字段,不受updateFields的约束;因此updateFields只指定在entity里的字段即可.
				//mongodb没必要像下一句说的那么麻烦.
////			一个字段既在指定的updateFields,也用在了Condition.set(arg1,arg2)等方法设置,entity里相应的字段会按规则转化到where部分.(V1.9.8)
			}

			Map<String, Object> filterMap;
			Map<String, Object> setMap;
			if (isFilterField) {
				filterMap = specialMap;
				setMap = entityMap;
			} else {
				filterMap = entityMap;
				setMap = specialMap;
			}
			
			//这个方法,filterMapFromC 只从Condition提取过滤条件
			if (ObjectUtils.isNotEmpty(filterMapFromC)) filterMap.putAll(filterMapFromC);

			reMap[0] = filterMap;
			reMap[1] = setMap;

		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}

		return reMap;
	}

	@Override
	public <T> int insert(T entity[], int batchSize, String excludeFields) {
		String tableName = _toTableName(entity[0]);
		int len = entity.length;
		List<Document> list = null;
		StringBuffer logValueSql=null;
		int count = 0;
		
		MongoSqlStruct struct = new MongoSqlStruct("int", tableName, null, null, null,
				null, null, false,entity.getClass()," (Artificial sql) Just define for cache: insert batch "); //insert 放在updateSet
		String sql=struct.getSql();
		initRoute(SuidType.MODIFY, struct.getEntityClass(), sql);
//		HoneyContext.addInContextForCache(sql, struct.getTableName());
		_addInContextForCache(struct);
		_log("Mongodb::batch insert: "+sql);
		
		DatabaseClientConnection conn = getConn();
		MongoDatabase db=getMongoDatabase(conn);
		
		try {
			for (int i = 1; i <= len; i++) { // i 1..len
				Map<String, Object> map = toDocumentExcludeSomeAndStoreFile(entity[i - 1], excludeFields,db);
				Document doc = newDoc(map);
				
				if (i % batchSize ==1) {
					list = new ArrayList<>();
					logValueSql=new StringBuffer("[");
				}
				
//				String res = JSON.toJSONString(map);
//				System.out.println(res);
				
				if(list.size()!=0) logValueSql.append(",");
//				logValueSql.append(newDBObject(map).toJson());
//				logValueSql.append(JSON.toJSONString(map));
				logValueSql.append(JsonUtil.toJson(map));
				list.add(doc);			
						
				if (i % batchSize == 0 || i == len) {
					logValueSql.append("]");
					struct = new MongoSqlStruct("int", tableName, null, null, null,
							null, null, false,entity.getClass(),logValueSql.toString());
					logInsertMany(struct); //_log
					
					InsertManyResult irs;
					ClientSession session = MongoContext.getCurrentClientSession();
					if (session == null) {
						irs = db.getCollection(tableName).insertMany(list);
					} else {
						irs = db.getCollection(tableName).insertMany(session, list);
					}
					
					count += irs.getInsertedIds().size();
//				MongoUtils.getCollection(tableName).bulkWrite(list);
//					if (i != len) {
//						list = new ArrayList<>();
////						listForLog = new ArrayList<>();
//						logValueSql=new StringBuffer();
//					}
				}
			}
			logAffectRow(count);
			return count;
			
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			logAffectRow(count);
			if (isConstraint(e)) {
				Logger.warn(e.getMessage());
				return count;
			}
			throw ExceptionHelper.convert(e);
		} finally {
			clearInCache(sql, "int", SuidType.MODIFY, count);
			close(conn);
		}
	}

	private <T> BasicDBObject toDocument(T entity) {
		BasicDBObject doc = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity);
			if (ObjectUtils.isNotEmpty(map)) doc = newDBObject(map);
		} catch (Exception e) {
			throw ExceptionHelper.convert(e);
		}
		return doc;
	}

	private <T> Map<String, Object> toDocumentExcludeSomeAndStoreFile(T entity, String excludeFields,MongoDatabase db) {
//		Document doc = null;
		Map<String, Object> map = null;
		try {
			map = ParaConvertUtil.toMapExcludeSome(entity, excludeFields);
			_storeFile(map,db); //处理保存文件,如果有
//			doc = newDoc(map);
		} catch (Exception e) {
			throw ExceptionHelper.convert(e);
		}
//		return doc;
		return map;
	}

	@SuppressWarnings("rawtypes")
	private <T> Object[] processId(Class clazz, Object id) {

		Object obj[] = new Object[2];
		BasicDBObject one = new BasicDBObject();
		BasicDBObject moreFilter = null;

		if (id instanceof String) {
			String ids[] = ((String) id).split(",");
			StringUtils.trim(ids);
			String idType = getIdType(clazz, getPkName(clazz));
			if (ids.length > 1) {
				BasicDBObject idFilters[] = new BasicDBObject[ids.length];
				int k = 0;
				for (String idValue : ids) {
					if ("String".equals(idType) && MongodbUtil.isMongodbId(idValue))
						idFilters[k++] = new BasicDBObject(IDKEY, new ObjectId(idValue)); // 改为in 也可以
					else
						idFilters[k++] = new BasicDBObject(IDKEY, tranIdObject(idType, idValue)); // 改为in 也可以

				}
//				moreFilter = (BasicDBObject)Filters.or(idFilters);
				moreFilter = new BasicDBObject();
				moreFilter.put("$or", idFilters);
			} else {
				if ("String".equals(idType) && MongodbUtil.isMongodbId(ids[0]))
					one.put(IDKEY, new ObjectId(ids[0]));
				else
					one.put(IDKEY, tranIdObject(idType, ids[0]));

			}
		} else {
			one.put(IDKEY, id);
		}

		obj[0] = one;
		obj[1] = moreFilter;

		return obj;
	}

	@SuppressWarnings("rawtypes")
	private String getIdType(Class clazz, String pkName) {
		Field field = null;
		String type = null;
		try {
			field = HoneyUtil.getField(clazz,pkName);
			type = field.getType().getSimpleName();
		} catch (Exception e) {
			// ignore
		}

		return type;
	}

	@SuppressWarnings("rawtypes")
	private String getPkName(Class c) {
		return HoneyUtil.getPkName(c);
	}

	private Object tranIdObject(String idType, String idValue) {
		if (idType != null) {
			if ("Long".equals(idType) || "long".equals(idType)) {
				return Long.parseLong(idValue);
			} else if ("Integer".equals(idType) || "int".equals(idType)) {
				return Integer.parseInt(idValue);
			} else if ("Short".equals(idType) || "short".equals(idType)) {
				return Short.parseShort(idValue);
			} else {
				return idValue;
			}
		}
		return idValue;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public int deleteById(Class c, Object id) {
//		checkShardingSupport();
		String tableName = _toTableNameByClass(c);

		Object[] obj = processId(c, id);
		BasicDBObject one = (BasicDBObject) obj[0];
		Bson moreFilter = (Bson) obj[1];

		Object filter;
		boolean isDeleteOne = false;
		if (moreFilter != null) {
			filter = moreFilter;
		} else {
			filter = one;
			isDeleteOne = true;
		}

		MongoSqlStruct struct = new MongoSqlStruct("int", tableName, (Bson) filter, null, null, null, null, false, c);

		struct.setSuidType(SuidType.DELETE);
		struct.setDeleteOne(isDeleteOne);
		return updateOrDelete(struct);
	}
	
	// 2.5.0
	private int _delete(MongoSqlStruct struct, boolean isClearCache) {

		int num = 0;
		String sql = null;
		DatabaseClientConnection conn = null;
		try {
			sql = struct.getSql();
			initRoute(SuidType.MODIFY, struct.getEntityClass(), sql);
			boolean isDeleteOne = struct.isDeleteOne();

			String logType = "Mongodb::delete: ";
			if (isDeleteOne) logType = "Mongodb::deleteById: ";
			logSQLForMain(logType + sql);
			logDelete(struct, isDeleteOne);

//			if (isClearCache) HoneyContext.addInContextForCache(sql, struct.getTableName());
			if (isClearCache) _addInContextForCache(struct);

			conn = getConn();
			ClientSession session = getClientSession();
			Bson filter = (Bson) struct.getFilter();
			String tableName = struct.getTableName();

			DeleteResult rs = null;

			if (isDeleteOne) {
				if (session == null)
					rs = getMongoDatabase(conn).getCollection(tableName).deleteOne(filter);
				else
					rs = getMongoDatabase(conn).getCollection(tableName).deleteOne(session, filter);
			} else if (filter != null) {
				if (session == null)
					rs = getMongoDatabase(conn).getCollection(tableName).deleteMany(filter);
				else
					rs = getMongoDatabase(conn).getCollection(tableName).deleteMany(session, filter);
			} else {
				boolean notDeleteWholeRecords = HoneyConfig.getHoneyConfig().notDeleteWholeRecords;
				if (notDeleteWholeRecords) {
					throw new BeeIllegalBusinessException(
							"BeeIllegalBusinessException: It is not allowed delete whole documents(records) in one collection(table).If need, you can change the config in bee.osql.notDeleteWholeRecords !");
				}
				if (session == null)
					rs = getMongoDatabase(conn).getCollection(tableName).deleteMany(new Document(new HashMap()));
				else
					rs = getMongoDatabase(conn).getCollection(tableName).deleteMany(session, new Document(new HashMap()));
			}
			if (rs != null) {
				long deletedCount = rs.getDeletedCount();
				if (deletedCount > Integer.MAX_VALUE) {
					Logger.error(
							"in MongodbSqlLib._delete method, affect num(long) more than Integer.MAX_VALUE, it will have error when long convert to int!");
				}
				num = (int) deletedCount;
			}

			return num;
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			logAffectRow(num);
			if (isClearCache) clearInCache(sql, "int", SuidType.MODIFY, num); // has clearContext(sql)
			close(conn);
		}
	}

	@Override
	public <T> int count(T entity, Condition condition) {
		String tableName = _toTableName(entity);
		BasicDBObject filter = toDBObjectForFilter(entity, condition);

		Class<T> entityClass = toClassT(entity);
		MongoSqlStruct struct = new MongoSqlStruct("int", tableName, filter, null, null, null,
				null, false, entityClass);

		String total = count(struct, entityClass);
		return StringUtils.isBlank(total) ? 0 :Integer.parseInt(total);
	}
	
	@Override
	public <T> String count(MongoSqlStruct struct, Class<T> entityClass) {

		if (!ShardingUtil.hadSharding()) {
			return _count(struct, entityClass);
		} else {
			if (HoneyContext.getSqlIndexLocal() == null) {
				String cacheValue = _count(struct, entityClass); // 检测缓存的
				if (cacheValue != null) {
					logDsTab();
					return cacheValue;
				}
				
				String fun = "";
				fun = new MongodbShardingSelectFunEngine().asynProcess(entityClass, this,
						struct);
				String sql = struct.getSql();

				addInCache(sql, fun, 1);

				return fun;

			} else { // 子线程执行
				return _count(struct, entityClass);
			}
		}
	}
	
	public <T> String _count(MongoSqlStruct struct, Class<T> entityClass) {
		
		String sql=struct.getSql();
		logSQLForMain("Mongodb::count: "+sql);
		logCount(struct);
//		HoneyContext.addInContextForCache(sql, struct.getTableName());
		_addInContextForCache(struct);
		
		Object cacheObj = getCache().get(sql);
		if (cacheObj != null) {
			clearContext(sql);
			return (String) cacheObj;
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.
		
		
		String tableName=struct.getTableName();
		BasicDBObject filter=(BasicDBObject)struct.getFilter();
		
		DatabaseClientConnection conn = getConn();
		try {
			long c; //2.5.0
			ClientSession session = getClientSession();
			
			if (session == null) {
				if (filter != null)
					c = (int) getMongoDatabase(conn).getCollection(tableName).countDocuments(filter);
				else
					c = (int) getMongoDatabase(conn).getCollection(tableName).countDocuments();
			} else {
				if (filter != null)
					c = (int) getMongoDatabase(conn).getCollection(tableName).countDocuments(session, filter);
				else
					c = (int) getMongoDatabase(conn).getCollection(tableName).countDocuments(session);
			}
			
			addInCache(sql, c + "", 1);
			
			if (c > Integer.MAX_VALUE) {
				Logger.error("in _count method, affect num(long) more than Integer.MAX_VALUE, it will have error when long convert to int!");
			}
			logAffectRow((int) c);
			
			return c+"";
		} finally {
			close(conn);
		}
	}
	

	@Override
	public <T> long insertAndReturnId(T entity, IncludeType includeType) {
//		checkShardingSupport();
		String tableName = _toTableName(entity);
		String sql = "";
		int num = 0;

//		Condition condition = null;
//		if (includeType != null) condition = BeeFactoryHelper.getCondition().setIncludeType(includeType);
//		Document doc = toDocument(entity, condition);

		Document doc = null;
		DatabaseClientConnection conn = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity,includeType==null?-1:includeType.getValue(),SuidType.INSERT);

			conn = getConn();
			MongoDatabase db = getMongoDatabase(conn);
			
			_storeFile(map, db); //处理保存文件
			doc = newDoc(map);

			MongoSqlStruct struct = new MongoSqlStruct("int", tableName, null, null, null, null,
					null, false, entity.getClass(), newDBObject(map));
			sql = struct.getSql();
			initRoute(SuidType.MODIFY, struct.getEntityClass(), sql);
			logSQLForMain("Mongodb::insertAndReturnId: " + sql);
			logInsertOne(struct);
//			HoneyContext.addInContextForCache(sql, struct.getTableName());
			_addInContextForCache(struct);

			ClientSession session = getClientSession();
			BsonValue bv =null;
			if (session == null)
				bv = db.getCollection(tableName).insertOne(doc).getInsertedId();
			else
				bv = db.getCollection(tableName).insertOne(session,doc).getInsertedId();
			
			long r = 0;
			if (bv != null) {
				if(bv instanceof BsonString) { //支持是数字的字符串
					r =Long.parseLong(((BsonString)bv).getValue());
				}else {
				    r = bv.asInt64().longValue();
				}
				if (r > 0) num = 1;
				
			}
			return r;
		} catch (Exception e) {

			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			boolean notCatch = HoneyConfig.getHoneyConfig().notCatchModifyDuplicateException;
			if (!notCatch && isConstraint(e)) { // 内部捕获并且是重复异常,则由Bee框架处理
				boolean notShow = HoneyConfig.getHoneyConfig().notShowModifyDuplicateException;
				if (!notShow) Logger.warn(e.getMessage());
				return num;
			}

			Logger.warn("Confirm that the returned value is numeric type!");
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			logAffectRow(num);
			clearInCache(sql, "int", SuidType.MODIFY, num);
			close(conn);
		}
	}
	
	/**
	 * SQL function: max,min,avg,sum,count. 如果统计的结果集为空,除了count返回0,其它都返回空字符.
	 */
	@Override
	public <T> String selectWithFun(T entity, FunctionType functionType, String fieldForFun,
			Condition condition) {
		if(entity==null) return null; 
		
		if (FunctionType.COUNT == functionType) {
			return count(entity, condition)+"";
		}
		
//		 last pipeline stage can not be null     不能在这拆分
		
		String tableName = _toTableName(entity);
		BasicDBObject filter = toDBObjectForFilter(entity, condition);
		
		Bson funBson = null;
		if ("id".equalsIgnoreCase(fieldForFun)) fieldForFun = IDKEY;
//		if (filter != null) listBson.add(Aggregates.match(filter)); // 过滤条件,要放在match里

		if (FunctionType.MAX == functionType) {
//		fun=Arrays.asList(Aggregates.match(filter), group(null, max("_fun", "$"+fieldForFun)) );
			funBson = group(null, max("_fun", "$" + fieldForFun));
		} else if (FunctionType.MIN == functionType) {
			funBson = group(null, min("_fun", "$" + fieldForFun));
		} else if (FunctionType.AVG == functionType) {
			funBson = group(null, avg("_fun", "$" + fieldForFun));
		} else if (FunctionType.SUM == functionType) {
			funBson = group(null, sum("_fun", "$" + fieldForFun)); // 统计的值为null时, sum: 0
		}
		
		Class<T> entityClass = toClassT(entity);
		MongoSqlStruct struct = new MongoSqlStruct("int", tableName, filter, null, null,
				null, null, false,entityClass,funBson);
		return selectWithFun(struct, entityClass);
	
	}
	
	@Override
	public <T> String selectWithFun(MongoSqlStruct struct, Class<T> entityClass) {
		
		if (!ShardingUtil.hadSharding()) {
			return _selectWithFun(struct, entityClass);
		} else {
			if (HoneyContext.getSqlIndexLocal() == null) {
				
				String cacheValue=_selectWithFun(struct, entityClass); //检测缓存的
				if(cacheValue!=null) {
					logDsTab();
					return cacheValue;
				}
				String fun = "";
				String funType = HoneyContext.getSysCommStrInheritableLocal(StringConst.FunType);
				if (FunctionType.AVG.getName().equalsIgnoreCase(funType)) {
					Logger.warn("AVG do not process here!");
				} else {
					fun = new MongodbShardingSelectFunEngine().asynProcess(entityClass, this, struct);
				}
				String sql = struct.getSql();
				addInCache(sql, fun, 1);
				
				return fun;
				
			} else { // 子线程执行
				return _selectWithFun(struct, entityClass);
			}
		}
	}
	

	public <T> String _selectWithFun(MongoSqlStruct struct, Class<T> entityClass) {

		BasicDBObject filter = (BasicDBObject) struct.getFilter();
		String tableName = struct.getTableName();
		String sql = struct.getSql();
		logSQLForMain("Mongodb::selectWithFun: "+sql);
//		HoneyContext.addInContextForCache(sql, tableName);
		_addInContextForCache(struct);
		
		Object cacheObj = getCache().get(sql);
		if (cacheObj != null) {
			clearContext(sql);
			return (String) cacheObj;
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.
		
		DatabaseClientConnection conn = getConn();
		try {
			MongoCollection<Document> collection = getMongoDatabase(conn).getCollection(tableName);

			List<Bson> listBson = new ArrayList<>();
			Bson funBson = (Bson) struct.getUpdateSetOrInsertOrFunOrOther();
			BasicDBObject match=null;
			if (filter != null) {
				listBson.add(Aggregates.match(filter)); // 过滤条件,要放在match里
				match=new BasicDBObject();
				match.put("$match", filter);
			}

//			if (FunctionType.MAX == functionType) {
////			fun=Arrays.asList(Aggregates.match(filter), group(null, max("_fun", "$"+fieldForFun)) );
//				funBson = group(null, max("_fun", "$" + fieldForFun));
//			} else if (FunctionType.MIN == functionType) {
//				funBson = group(null, min("_fun", "$" + fieldForFun));
//			} else if (FunctionType.AVG == functionType) {
//				funBson = group(null, avg("_fun", "$" + fieldForFun));
//			} else if (FunctionType.SUM == functionType) {
//				funBson = group(null, sum("_fun", "$" + fieldForFun)); // 统计的值为null时, sum: 0
//			}
			

			struct = new MongoSqlStruct("int", tableName, match, null, null,
					null, null, false, null, funBson); // this method no entityClass
			logGroup(struct); //不准确 todo
			
			ClientSession session = getClientSession();
			
			listBson.add(funBson);
			Document rs = null;
			if (session == null)
				rs = collection.aggregate(listBson).first();
			else
				rs = collection.aggregate(session, listBson).first();
			
			String fun = "";

			if (rs != null) {
				Logger.debug("selectWithFun result raw json: "+rs.toJson());
//				Logger.debug(rs.get("_fun")+"");
//				
//				Map<String, Object> jsonMap = null;
//				try {
//					ObjectMapper objectMapper = new ObjectMapper();
//					jsonMap = objectMapper.readValue(rs.toJson(),
//							new TypeReference<Map<String, Object>>() {
//							});
//				} catch (Exception e) {
//					Logger.debug(e.getMessage(), e);
//				}
//				if (jsonMap != null && jsonMap.get("_fun") != null)
//					fun = jsonMap.get("_fun").toString();
//				else
//					fun = "";
				
				Object json = rs.get("_fun");
				if (json != null)
					fun = json.toString();
				else
					fun = "";
			}
			
			addInCache(sql, fun, 1);

			return fun;
			
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}
	}
	
	//table,where:doc.toJson(),  group:     orderyBy:   skip:   limit:  selectFields:   
	
	private <T> List<T> selectWithGroupBy(T entity,Condition condition) {
		checkShardingSupport(); //TODO
		String tableName = _toTableName(entity);

		BasicDBObject filter = toDBObjectForFilter(entity, condition); // 加过滤条件.

		DatabaseClientConnection conn = getConn();
		try {
			MongoCollection<Document> collection = getMongoDatabase(conn).getCollection(tableName);
			
			List<String> groupNameslist = condition.getGroupByFields();
			int size = groupNameslist == null ? -1 : groupNameslist.size();
			StringBuffer groupColumn = new StringBuffer("  '_id' : {");
//			{ '$group' : {  '_id' : {'id':'$id'},'_count' : { '$sum' : 1 }}}
			String fieldName="";
			for (int i = 0; groupNameslist != null && i < size; i++) {
				if (i != 0) groupColumn.append(" , ");
				fieldName=groupNameslist.get(i);
				if("id".equalsIgnoreCase(fieldName)) fieldName="_id";
				groupColumn.append("'");
				groupColumn.append(fieldName);
				groupColumn.append("':'$");
				groupColumn.append(fieldName);
				groupColumn.append("'");
			}
			groupColumn.append("}");
			StringBuffer groupSearch = new StringBuffer("{ '$group' : {");
			groupSearch.append(groupColumn);
			String[] selectFunBsonFieldArray = selectFunBsonField(condition);
			for (int i = 0; selectFunBsonFieldArray != null
					&& i < selectFunBsonFieldArray.length; i++) {
				groupSearch.append(",");
				groupSearch.append(selectFunBsonFieldArray[i]);
			}
			groupSearch.append("}}");
			
			List<Bson> listBson = new ArrayList<>();
			BasicDBObject match=null;
			if (filter != null) {
				listBson.add(Aggregates.match(filter)); // 过滤条件,要放在match里
				match=new BasicDBObject();
				match.put("$match", filter);
			}

			listBson.add(BsonDocument.parse(groupSearch.toString()));
			
//			if(filter!=null)err.println(filter.toString());
//			err.println(groupSearch.toString());
			
			Class<T> entityClass = toClassT(entity);
			MongoSqlStruct struct = new MongoSqlStruct("List<T>", tableName, match, null, null,
					null, null, false,entityClass,groupSearch.toString());
			String sql = struct.getSql();
			logSQLForMain("Mongodb::selectWithGroupBy: " + sql);
			logGroup(struct);
			
			ClientSession session = getClientSession();
			AggregateIterable<Document> iterable=null;
			if (session == null)
				iterable = collection.aggregate(listBson);
			else
				iterable = collection.aggregate(session, listBson);
			
			//////// test start
//			System.out.println("--------------------start--");
//			Class<T> entityClass = toClassT(entity);
			List<T> list = new ArrayList<>();
			MongoCursor<Document> it=iterable.iterator();
//			String json="";
			while(it.hasNext()) {
				Document doc=it.next();
				
//				{"_id": {"id": null}, "id": 10299, "max_total": {"$numberDecimal": "298893.12"}, "min_total": {"$numberDecimal": "70.7"}}
//				System.out.println(doc.toJson());
//				
//				Object id=doc.get("id");
//				Object max_total=doc.get("max_total");
//				Object min_total=doc.get("min_total");
//				
//				System.out.println(id);
//				System.out.println(max_total);
//				System.out.println(min_total);
				//直接按字段名获取.
				
//				{"_id": {"abc": "test bee ", "name": "mongodb21"}, "_count": 2}
				
//				Object name=doc.get("name");
//				System.out.println(name);  //不行
//				Object _count=doc.get("_count");
//				System.out.println(_count);
//				
//				Document g=(Document)doc.get("_id");  //多个排序字段.
//				System.out.println(g.get("name"));
//				System.out.println(g.get("abc"));
				
				Map<String,Object> groupNameMap=null;
				if(size>0) {
					Document groupNameDoc=(Document)doc.get("_id");
					groupNameMap=TransformResult.doc2Map(groupNameDoc);
				}
				
				Map<String,Object> rsMap=TransformResult.doc2Map(doc);
				rsMap.remove("_id");
				
				if(size>0) rsMap.putAll(groupNameMap); //要是排序字段有_id,会在groupNameMap保留
				
				Logger.debug("selectWithGroupBy doc2Map result raw json: "+rsMap.toString());
				
				try {
					list.add(TransformResult.toEntity(rsMap, entityClass));
				} catch (Exception e) {
					if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
					throw ExceptionHelper.convert(e);
				}
				
				
				
/*//				System.out.println(it.next().toJson());
				json=it.next().toJson();
				System.out.println(json);
				
				Map<String, Object> jsonMap = null;
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					jsonMap = objectMapper.readValue(json,new TypeReference<Map<String, Object>>(){});
					
					if (jsonMap != null && jsonMap.get("_id") != null) {
						
					}
						
				} catch (Exception e) {
					Logger.debug(e.getMessage(), e);
				}*/
				
				
				
			}
	        //////// test end
			
			return list;   
			
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}
	}

	@SuppressWarnings("rawtypes")
	private String _toTableNameByClass(Class c) {
		return NameTranslateHandle.toTableName(c.getName());
	}
	
	private String[] selectFunBsonField(Condition condition) {
		
		String[] bsonFieldArray = null;
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		List<FunExpress> funExpList = conditionImpl.getFunExpList();
		for (int i = 0; funExpList != null && i < funExpList.size(); i++) {
			if (i == 0) bsonFieldArray = new String[funExpList.size()];

			String functionType = funExpList.get(i).getFunctionType();
			String alias = funExpList.get(i).getAlias();
			String fieldForFun = funExpList.get(i).getField();
			if(StringUtils.isBlank(alias)) alias=fieldForFun;
			
			if ("id".equalsIgnoreCase(fieldForFun)) fieldForFun = IDKEY;
			
			if (FunctionType.MAX.getName().equals(functionType)) {
				bsonFieldArray[i] ="'XfieldX1' : { '$max' : '$XfieldX2' }".replace("XfieldX1", alias).replace("XfieldX2", fieldForFun);
			} else if (FunctionType.MIN.getName().equals(functionType)) {
				bsonFieldArray[i] ="'XfieldX1' : { '$min' : '$XfieldX2' }".replace("XfieldX1", alias).replace("XfieldX2", fieldForFun);
			} else if (FunctionType.AVG.getName().equals(functionType)) {
				bsonFieldArray[i] ="'XfieldX1' : { '$avg' : '$XfieldX2' }".replace("XfieldX1", alias).replace("XfieldX2", fieldForFun);
			} else if (FunctionType.SUM.getName().equals(functionType)) {
				bsonFieldArray[i] ="'XfieldX1' : { '$sum' : '$XfieldX2' }".replace("XfieldX1", alias).replace("XfieldX2", fieldForFun);
			} else if (FunctionType.COUNT.getName().equals(functionType)) {
				bsonFieldArray[i] ="'_countX1' : { '$sum' : 1 }".replace("_countX1", alias);
			}
		}
		
		if(bsonFieldArray==null && Boolean.TRUE.equals(conditionImpl.hasGroupBy())) {
			bsonFieldArray=new String[1];
			bsonFieldArray[0] = "'_count' : { '$sum' : 1 }";
		}
		
		return bsonFieldArray;
	}
	
	private Cache cache;

	public Cache getCache() {
		if (cache == null) {
			cache = BeeFactory.getHoneyFactory().getCache();
		}
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	private void close(DatabaseClientConnection conn) {
		try {
			if (conn != null) conn.close();
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	private int getIncludeType(Condition condition) {
		if (condition == null) return -1;
		return condition.getIncludeType() == null ? -1 : condition.getIncludeType().getValue();
	}
	
	//用于过滤条件
	private <T> BasicDBObject toDBObjectForFilter(T entity, Condition condition) {
//		Document doc = null;
		BasicDBObject doc = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity, getIncludeType(condition));
			if (condition == null) {
				if (ObjectUtils.isNotEmpty(map))
					return newDBObject(map);
				else
					return null;
			}

			Map<String, Object> map2 = MongoConditionHelper.processCondition(condition);
			if (ObjectUtils.isNotEmpty(map) && ObjectUtils.isNotEmpty(map2))
				map.putAll(map2); // map的值,会被map2中有同样key的值覆盖.
			else if (ObjectUtils.isEmpty(map)) map = map2;
			
			//toMapForGridFsSelect   没用.   要在查fs时才有用.       //与插入不同.  插入也是放在fs
//			Map<String, Object> map3=ParaConvertUtil.toMapForGridFsSelect(entity.getClass(), getIncludeType(condition));
//			if(ObjectUtils.isNotEmpty(map3) ) {
//			  Object metadata=map3.get(StringConst.GridFsMetadata_FieldName);
//			}
//			Map<String,Object> metadata=new HashMap<>();
//			metadata.put("type", "txt-7z-23");
//			history.setMetadata(metadata);   // select时,可以作为过滤条件吗?  可以   db.fs.files.find({"metadata.type":"7z"});
//			JAVA Mongodb驱动,没有相应接口.
			
			if (ObjectUtils.isNotEmpty(map)) doc = newDBObject(map);

		} catch (Exception e) {
			throw ExceptionHelper.convert(e);
		}

		return doc;
	}
	
	@Override
	public <T> boolean createTable(Class<T> entityClass, boolean isDropExistTable) {
		if (!ShardingUtil.hadSharding()) {
			return _createTable(entityClass, isDropExistTable); // 不用分片走的分支
		} else {
			if (HoneyContext.getSqlIndexLocal() == null) { // 分片,主线程
				return new MongodbShardingDdlEngine().asynProcess(entityClass, this, isDropExistTable);
			} else { // 子线程执行
				return _createTable(entityClass, isDropExistTable);
			}
		}
	}

	// sharding  index??  可以通过HoneyContext.setTabSuffix(String suffix) 设置
	private <T> boolean _createTable(Class<T> entityClass, boolean isDropExistTable) {
		String tableName = _toTableNameByClass(entityClass); // 到这里,已是lib执行,获取的是当前线程的tab
		DatabaseClientConnection conn = null;
		boolean f = false;
		try {
			conn = getConn();
			MongoDatabase mdb = getMongoDatabase(conn);
			if (isDropExistTable) {
				_log("Mongodb::drop collection(table): " + tableName);
				ClientSession session = getClientSession();
				if (session == null)
					mdb.getCollection(tableName).drop();
				else
					mdb.getCollection(tableName).drop(session);
			}
			_log("Mongodb::create collection(table): " + tableName);
			mdb.createCollection(tableName);
			f = true;
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}
		return f;
	}
	
	
	// create index
	@Override
	public String index(String collectionName, String fieldName, IndexType indexType) {
		checkShardingSupport();
		DatabaseClientConnection conn = null;
		try {
			conn = getConn();
			MongoDatabase mdb = getMongoDatabase(conn);
			fieldName = _toColumnName(fieldName);
			Bson bson = _getKeyBson(fieldName, indexType);

			ClientSession session = getClientSession();
			collectionName = _toTableName2(collectionName); // fixed bug 2.1

			_log("Mongodb::index collection(table): " + collectionName + ", fieldName: " + fieldName + ", indexType: "
					+ indexType.getIndexType());

			String re;
			if (session == null) {
				re = mdb.getCollection(collectionName).createIndex(bson);
				// 用原生语句也ok。
//				Bson commandBson = index(collectionName, fieldName);
//				Document result = runByCommand(commandBson);//操作驱动API
//				re=result.toJson();
			} else {
				re = mdb.getCollection(collectionName).createIndex(session, bson);
			}
			return re;
		} finally {
			close(conn);
		}
	}
	
	//ok
//	private Bson index(String tableName, String fieldName) {
//		BasicDBObject doc = new BasicDBObject();
//		doc.append("createIndexes", tableName);
//
//		BasicDBObject index = new BasicDBObject();
//		BasicDBObject index2Dsphere = new BasicDBObject();
//		index2Dsphere.put(fieldName, "2dsphere");
//
//		index.put("key", index2Dsphere);
//		index.append("name", fieldName + "_" + "2dsphere");
//
//		doc.append("indexes", Arrays.asList(index));
//
//		return doc;
//	}


	@Override
	public String unique(String collectionName, String fieldName, IndexType indexType) {
		checkShardingSupport();
		DatabaseClientConnection conn = null;
		try {
			conn = getConn();
			MongoDatabase mdb = getMongoDatabase(conn);
			Bson bson = _getKeyBson(_toColumnName(fieldName), indexType);
			IndexOptions indexOptions = new IndexOptions().unique(true); // unique

			ClientSession session = getClientSession();
			collectionName = _toTableName2(collectionName);

			_log("Mongodb::unique collection(table): " + collectionName + ", fieldName: " + fieldName + ", indexType: "
					+ indexType.getIndexType());

			String re;
			if (session == null)
				re = mdb.getCollection(collectionName).createIndex(bson, indexOptions);
			else
				re = mdb.getCollection(collectionName).createIndex(session, bson, indexOptions);

			return re;
		} finally {
			close(conn);
		}
	}

	@Override
	public List<String> indexes(String collectionName, List<IndexPair> indexes) {

		if (indexes == null || indexes.size() <= 0) return null;
		checkShardingSupport();
		List<IndexModel> list = new ArrayList<>(indexes.size());
		Bson bson = null;
		IndexOptions indexOptions = null;
		for (IndexPair indexPair : indexes) {
			bson = _getKeyBson(_toColumnName(indexPair.getFieldName()), indexPair.getIndexType());
			indexOptions = indexPair.getIndexOptions();
			if (indexOptions == null) indexOptions = new IndexOptions();
			list.add(new IndexModel(bson, indexOptions));
		}

		DatabaseClientConnection conn = null;
		try {
			conn = getConn();
			MongoDatabase mdb = getMongoDatabase(conn);

			ClientSession session = getClientSession();
			collectionName=_toTableName2(collectionName);
			List<String> re;
			if (session == null)
				re = mdb.getCollection(collectionName).createIndexes(list);
			else
				re = mdb.getCollection(collectionName).createIndexes(session, list);

			return re;
		} finally {
			close(conn);
		}
	}

	private Bson _getKeyBson(String fieldName, IndexType indexType) {
		Bson bson = null;

		switch (indexType) {
			case asc:
				bson = Indexes.ascending(fieldName);
				break;
			case desc:
				bson = Indexes.descending(fieldName);
				break;
			case text:
				bson = Indexes.text(fieldName);
				break;
			case geo2dsphere:
				bson = Indexes.geo2dsphere(fieldName);
				break;
			case geo2d:
				bson = Indexes.geo2d(fieldName);
				break;
			case hashed:
				bson = Indexes.hashed(fieldName);
				break;
			default:
				break;
		}

		return bson;
	}

	private String _toTableName(Object entity) {
		return NameTranslateHandle.toTableName(NameUtil.getClassFullName(entity));
	}
	
	private String _toTableName2(String entityName) {//fixed bug 2.1
		return NameTranslateHandle.toTableName(entityName);
	}
	
	private String _toColumnName(String fieldName) {
		return NameTranslateHandle.toColumnName(fieldName);
	}
	
	private String _removeComment(String str) {
		return StringParser.removeComment(str);
	}
	
	@Override
	public int modify(String commandStr) {
		checkShardingSupport();
		commandStr=_removeComment(commandStr);
		CommandEngine cEngine = new CommandEngine();
		
		String tableAndType[]=cEngine.getTableAndType(commandStr);
		checkIsSelectCommandException(tableAndType[1]);
		String tableName = tableAndType[0];
		String type = tableAndType[1];
		
		_addInContextForCache(commandStr, tableName);
		initRoute(SuidType.MODIFY, null, commandStr);
		
		Integer num = 0;
		try {
			Bson commandBson = cEngine.parseSuidCommand(commandStr, tableAndType);//解析及重组
			Document result = runByCommand(commandBson);//操作驱动API
			num = TransformResultForCommand.transformResult(type, result);//自动装配结果
			
			clearInCache(commandStr, "int", SuidType.MODIFY, num);
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} 
		return num;
	}
	
	private Document runByCommand(Bson commandBson) {
		DatabaseClientConnection conn = null;
		Document result = null;
		try {
			conn = getConn();
			ClientSession session = getClientSession();

			if (session == null)
				result = getMongoDatabase(conn).runCommand(commandBson);
			else
				result = getMongoDatabase(conn).runCommand(session, commandBson);

//			Logger.debug(result.toJson());
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}finally {
			close(conn);
		}
		return result;
	}
	
	@Override
	public String selectJson(String commandStr) {
		checkShardingSupport();
		commandStr = _removeComment(commandStr);
		CommandEngine cEngine = new CommandEngine();
		String tableAndType[] = cEngine.getTableAndType(commandStr);
		String tableName = tableAndType[0];
		_addInContextForCache(commandStr, tableName); //为了操作缓存，将相关信息添加到上下文

		boolean isReg = updateInfoInCache(commandStr, "StringJson", SuidType.SELECT, null);
		if (isReg) {
			initRoute(SuidType.SELECT, null, commandStr);//初始化路由
			Object cacheObj = getCache().get(commandStr); //检测缓存是否有数据
			if (cacheObj != null) {
				clearContext(commandStr); //清除上下文
				return (String) cacheObj; //返回找到的缓存结果
			}
		}
		String json = "";
		try {
			Bson commandBson = cEngine.parseSuidCommand(commandStr, tableAndType);//解析及重组
			Document result = runByCommand(commandBson); //操作驱动API
			json=TransformResultForCommand.transformResult(result); //自动装配结果
			
			addInCache(commandStr, json, -1); // 添加数据到缓存；没有作最大结果集判断
//			Logger.debug(json);
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
		return json;
	}
	
	private boolean isSelectType(String type) {
		return "find".equals(type) || "findOne".equals(type) ;
	}
	
	private void checkIsNotSelectCommandException(String type) {
		if(! isSelectType(type)) throw new BeeIllegalBusinessException("The command type is not find/findone!");
	}
	
	private void checkIsSelectCommandException(String type) {
		if(isSelectType(type)) throw new BeeIllegalBusinessException("The command type is find/findone, but it should modify type!");
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> select(String commandStr, Class<T> returnTypeClass) {
		checkShardingSupport();
		commandStr=_removeComment(commandStr);
		CommandEngine cEngine = new CommandEngine();
		String tableAndType[]=cEngine.getTableAndType(commandStr);
		checkIsNotSelectCommandException(tableAndType[1]);
		String tableName = tableAndType[0];
		_addInContextForCache(commandStr, tableName);
		
		boolean isReg = updateInfoInCache(commandStr, "List<T>", SuidType.SELECT, returnTypeClass);
		if (isReg) {
			initRoute(SuidType.SELECT, returnTypeClass, commandStr);
			Object cacheObj = getCache().get(commandStr); 
			if (cacheObj != null) {
				clearContext(commandStr);
				List<T> list = (List<T>) cacheObj;
				logSelectRows(list.size());
				return list;
			}
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.

		List<T> rsList = null;
		try {
			Bson commandBson = cEngine.parseSuidCommand(commandStr, tableAndType);//解析及重组
			Document result = runByCommand(commandBson);
			rsList=TransformResultForCommand.transformResultForListT(result, returnTypeClass);
			
			addInCache(commandStr, rsList, rsList.size());
			logSelectRows(rsList.size());
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
		return rsList;
	}
	
	@Override
	public List<Map<String, Object>> selectMapList(String commandStr) {
		checkShardingSupport();
		commandStr=_removeComment(commandStr);
		CommandEngine cEngine = new CommandEngine();
		String tableAndType[] = cEngine.getTableAndType(commandStr);
		checkIsNotSelectCommandException(tableAndType[1]);
		String tableName = tableAndType[0];
		_addInContextForCache(commandStr, tableName);

		boolean isReg = updateInfoInCache(commandStr, "List<Map<String, Object>>", SuidType.SELECT, null);
		if (isReg) {
			initRoute(SuidType.SELECT, null, commandStr);
			Object cacheObj = getCache().get(commandStr);
			if (cacheObj != null) {
				clearContext(commandStr);
				List<Map<String, Object>> list = (List<Map<String, Object>>) cacheObj;
				logSelectRows(list.size());
				return list;
			}
		}
		if (isShardingMain()) return null; // sharding时,主线程没有缓存就返回.

		List<Map<String, Object>> rsList = null;
		try {
			Bson commandBson = cEngine.parseSuidCommand(commandStr, tableAndType);//解析及重组
			Document result = runByCommand(commandBson);
			rsList=TransformResultForCommand.transformResultForListMap(result); //自动装配结果
			
			addInCache(commandStr, rsList, rsList.size());
			logSelectRows(rsList.size());
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
		return rsList;
	}
	



	@Override
	public void dropIndexes(String collectionName) {
		checkShardingSupport();
		DatabaseClientConnection conn = null;
		try {
			conn = getConn();
			MongoDatabase mdb = getMongoDatabase(conn);
			ClientSession session = getClientSession();
			collectionName=_toTableName2(collectionName);
			_log("Mongodb::drop indexes for " + collectionName);
			if (session == null)
				mdb.getCollection(collectionName).dropIndexes();
			else
				mdb.getCollection(collectionName).dropIndexes(session);
		} finally {
			close(conn);
		}
	}

	private static final String Timeout_MSG = "Can not connect the Mongodb server. Maybe you did not start the Mongodb server!";
	
	
	private GridFSBucket getGridFSBucket(MongoDatabase database) {
//		MongoDatabase database = SingleMongodbFactory.getMongoDb(); // 单个数据源时,
		return GridFSBuckets.create(database);
	}

	@Override
	public String uploadFile(String filename, InputStream source) {
		return uploadFile(filename, source, null);
	}

	@Override
	public String uploadFile(String filename, InputStream fileStream,
			Map<String, Object> metadataMap) {
		checkShardingSupport(); //TODO
		DatabaseClientConnection conn = null;
		conn = getConn();
		MongoDatabase database = getMongoDatabase(conn);
		String stringId = "";

		try {
			stringId = _uploadFile(filename, fileStream, metadataMap, database);
		} finally {
			close(conn);
		}
		return stringId;
	}
	
	private String _uploadFile(String filename, InputStream fileStream,
			Map<String, Object> metadataMap, MongoDatabase database) {

		String stringId = "";
		try {

			GridFSBucket gridFSBucket = getGridFSBucket(database);

			GridFSUploadOptions options = null;
			if (metadataMap != null && metadataMap.size() > 0) {
				options = new GridFSUploadOptions();
				options.metadata(new Document(metadataMap));
			}
			ObjectId fileId;
			ClientSession session = getClientSession();
			if(session==null) {
				if (options != null) {
					// 同一个名字，可以重复保存，但ObjectId fileId不一样。
					fileId = gridFSBucket.uploadFromStream(filename, fileStream, options);// options 不能为null
				} else {
					fileId = gridFSBucket.uploadFromStream(filename, fileStream);
				}
			} else {
				if (options != null) {
					// 同一个名字，可以重复保存，但ObjectId fileId不一样。
					fileId = gridFSBucket.uploadFromStream(session, filename, fileStream, options);// options 不能为null
				} else {
					fileId = gridFSBucket.uploadFromStream(session, filename, fileStream);
				}
			}
			
			stringId = fileId.toString();

		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		}
		return stringId;
	}

	@Override
	public List<GridFsFile> selectFiles(GridFsFile gridFsFile, Condition condition) {
		checkShardingSupport();//TODO
		
		//属性不转换,保留原样.
		
		if (gridFsFile.getMetadata() != null && gridFsFile.getMetadata().size() == 0)
			gridFsFile.setMetadata(null);

		MongoSqlStruct struct = parseMongoSqlStruct(gridFsFile, condition, "List<GridFsFile>");
		struct.setTableName("fs.files");
		_log("Mongodb::selectFiles: " + struct.getSql());

		GridFSFindIterable iterable = gridFSFindIterable(struct);
		MongoCursor<GridFSFile> cursor = iterable.iterator();

		List<GridFsFile> list = new ArrayList<>();

		while (cursor.hasNext()) {
			GridFSFile fs = cursor.next();
			list.add(new GridFsFile(fs.getId().asObjectId().getValue().toString(),
					fs.getFilename(), fs.getLength(), fs.getChunkSize(), fs.getUploadDate(),
					fs.getMetadata()));
		}

		return list;
	}

	private GridFSFindIterable gridFSFindIterable(MongoSqlStruct struct) {
//		String tableName = struct.getTableName();
		
		Bson filter = (Bson) struct.getFilter();
		Bson sortBson = (Bson) struct.getSortBson();
		Integer size = struct.getSize();
		Integer start = struct.getStart();

		DatabaseClientConnection conn = null;
		conn = getConn();
		MongoDatabase database = getMongoDatabase(conn);
		GridFSBucket gridFSBucket = getGridFSBucket(database);

		GridFSFindIterable iterable;

		try {
			ClientSession session = getClientSession();
			if (session == null) {
				if (filter != null)
					iterable = gridFSBucket.find(filter);
				else
					iterable = gridFSBucket.find();
			} else {
				if (filter != null)
					iterable = gridFSBucket.find(session, filter);
				else
					iterable = gridFSBucket.find(session);
			}

			if (sortBson != null) iterable = iterable.sort(sortBson);

			if (size != null && size > 0) {
				if (start == null || start < 0) start = 0;
				iterable = iterable.skip(start).limit(size);
			}

		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}

		return iterable;
	}
	
	@Override
	public byte[] getFileByName(String fileName) {
		logSQL("getFileByName, fileName: "+fileName);
		return _getFileByKey(fileName);
	}
	
	@Override
	public byte[] getFileById(String fileId) {
		logSQL("getFileById, fileId: "+fileId);
		return _getFileByKey(new ObjectId(fileId));
	}

	private byte[] _getFileByKey(Object key) {
		DatabaseClientConnection conn = null;
		conn = getConn();
		MongoDatabase database = getMongoDatabase(conn);
		GridFSBucket gridFSBucket = getGridFSBucket(database);

		byte[] returnBytes = null;
		GridFSDownloadStream downloadStream = null;
		ByteArrayOutputStream bos = null;
		try { // 返回的是files里面的id,查询时也是里面的id
			
			ClientSession session = getClientSession();
			if (session == null) {
				if (key instanceof ObjectId)
					downloadStream = gridFSBucket.openDownloadStream((ObjectId) key);
				else
					downloadStream = gridFSBucket.openDownloadStream((String) key);
			} else {
				if (key instanceof ObjectId)
					downloadStream = gridFSBucket.openDownloadStream(session, (ObjectId) key);
				else
					downloadStream = gridFSBucket.openDownloadStream(session, (String) key);
			}

			int fileLength = (int) downloadStream.getGridFSFile().getLength();

			bos = new ByteArrayOutputStream(fileLength);
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = downloadStream.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			returnBytes = bos.toByteArray();

		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			
			boolean isMongoGridFSException=false;
			if (e instanceof MongoGridFSException) {
				Logger.warn(e.getMessage());
				isMongoGridFSException=true;
			}
			if(! isMongoGridFSException) throw ExceptionHelper.convert(e);
		} finally {
			try {
				if (bos != null) bos.close();
			} catch (Exception e2) {
				// ignore
			}
			try {
				if (downloadStream != null) downloadStream.close();
			} catch (Exception e3) {
				// ignore
			}
			close(conn);
		}
		return returnBytes;
	}

	@Override
	public void renameFile(String fileId, String newName) {
		DatabaseClientConnection conn = null;
		conn = getConn();
		MongoDatabase database = getMongoDatabase(conn);
		GridFSBucket gridFSBucket = getGridFSBucket(database);

		try {
			ClientSession session = getClientSession();
			if (session == null) {
				gridFSBucket.rename(new ObjectId(fileId), newName);
			} else {
				gridFSBucket.rename(session, new ObjectId(fileId), newName);
			}
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}
	}

	@Override
	public void deleteFile(String fileId) {
		DatabaseClientConnection conn = null;
		conn = getConn();
		MongoDatabase database = getMongoDatabase(conn);
		GridFSBucket gridFSBucket = getGridFSBucket(database);
		try {
			ClientSession session = getClientSession();
			if (session == null) {
				gridFSBucket.delete(new ObjectId(fileId));
			} else {
				gridFSBucket.delete(session, new ObjectId(fileId));
			}
		} catch (Exception e) {
			if (e instanceof MongoTimeoutException) Logger.warn(Timeout_MSG);
			throw ExceptionHelper.convert(e);
		} finally {
			close(conn);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Document newDoc(Map map) {
		return new Document(map);
	}
	
	@SuppressWarnings({ "rawtypes" })
	private BasicDBObject newDBObject(Map map) {
		return new BasicDBObject(map);
	}
	
	private ClientSession getClientSession() {
//		ClientSession session = MongoContext.getCurrentClientSession();
		return MongoContext.getCurrentClientSession();
	}
	
	
	//----------------------GEO-----------------------start-----------------------------
	
	@Override
	public <T> List<T> geoFind(T entity, Bson geoBson,Condition condition) { // near,nearSphere

		if (entity == null) return Collections.emptyList();

		MongoSqlStruct struct = parseMongoSqlStruct(entity, condition, "List<T>");

		if (geoBson != null) {
			BasicDBObject filter = (BasicDBObject) struct.getFilter();
			if (filter == null) {
				struct.setFilter(geoBson);
			} else {
				struct.setFilter(Filters.and(filter, geoBson));
			}
		}

		Class<T> entityClass = toClassT(entity);
		return select(struct, entityClass);
	};

	//----------------------GEO-----------------------end-----------------------------
	
	private void _log(String str) {
		logSQL(str);
	}
	
	//主线程才打印（不需要分片或不是子线程）
	private void logSQLForMain(String hardStr) {
		if (!ShardingUtil.hadSharding() || HoneyContext.getSqlIndexLocal() == null)
			logSQL(hardStr);
	}
	
	private void log(MongoSqlStruct struct) {
		// db.users.find({ "gender" : true , "age" : { "$gte" : 20}},{ "name" : 1 , "age" : 1 , "address" : 1}).sort({ "age" : -1}).limit(2).skip(0)

		String table = struct.getTableName();
//		table = table.replace(StringConst.ShardingTableIndexStr, "");

		StringBuffer sql = new StringBuffer();
		sql.append("db.");
		sql.append(table);
		sql.append(".find(");

		if (struct.getFilter() != null) {
			String filter="";
			try {
				filter=((BasicDBObject)struct.getFilter()).toString();
			} catch (Exception e) {
				filter=struct.getFilter().toString();
				_log("This is an inaccurate raw statement!");
			}
			sql.append(filter);
			tranferCommandLog(sql);
			
			//_id加 ObjectId( )   已完成
//			db.collection.find({"_id" :ObjectId("56063f17ade2f21f36b03133")})
//			db.Noid0.find({"$or": [{"_id": {"$oid": "643fcd4b81c72a273cdebdf7"}}, {"_id": {"$oid": "643fcd57eb4c0000c9002626"}}, {"_id": "ewewewewew"}]})
//			db.Noid0.find({"$or": [{"_id": ObjectId("643fcd4b81c72a273cdebdf7")}, {"_id": ObjectId("643fcd57eb4c0000c9002626")}, {"_id": "ewewewewew"}]})
		}
		
		boolean isSelectAllFields=HoneyContext.isTrueInSysCommStrLocal(StringConst.MongoDB_SelectAllFields);
		boolean showMongoSelectAllFields=HoneyConfig.getHoneyConfig().showMongoSelectAllFields;
		
		if (showMongoSelectAllFields || ! isSelectAllFields) { //假如查全部,不需要显示这些
			String[] selectFields = struct.getSelectFields();
			boolean hasId = struct.isHasId();
			if (selectFields != null) {
				Map<String, Integer> map = new LinkedHashMap<>();
				for (String s : selectFields) {
					map.put(s, 1);
				}
				if (!hasId) map.put("_id", -1);

				if (struct.getFilter() == null) { // fixed 2.4.0
					sql.append("{}");
				}
						
				BasicDBObject projection = new BasicDBObject(map);
				sql.append(", ");
				sql.append(projection.toString());
			}

		}

		sql.append(")");
		if (struct.getSortBson() != null) {
			sql.append(".sort(");
			sql.append(((BasicDBObject) struct.getSortBson()).toString());
			sql.append(")");
		}
		if (struct.getSize() != null) {
			sql.append(".limit(");
			sql.append(struct.getSize());
			sql.append(")");
			if (struct.getStart() != null) {
				sql.append(".skip(");
				sql.append(struct.getStart());
				sql.append(")");
			}
		}

		_log(sql.toString());
	}
	
	private void logInsertOne(MongoSqlStruct struct) {
		logInsert(struct, false);
	}
	
	private void logInsertMany(MongoSqlStruct struct) {
		//转成mongo shell能执行的json后，像数字的类型可能会有偏差.
		logInsert(struct, true);
	}
	
	private void logInsert(MongoSqlStruct struct,boolean insertMany) {
		String insertType="One";
		if(insertMany) insertType="Many";
		String table = struct.getTableName();
//		table = table.replace(StringConst.ShardingTableIndexStr, "");
		if (struct.getUpdateSetOrInsertOrFunOrOther() != null) {
				StringBuffer sql = new StringBuffer();
				sql.append("db.");
				sql.append(table);
				sql.append(".insert");
				sql.append(insertType);
				sql.append("(");
				
				if(insertMany) sql.append(struct.getUpdateSetOrInsertOrFunOrOther().toString());
				else sql.append(((BasicDBObject)struct.getUpdateSetOrInsertOrFunOrOther()).toString());
				
				tranferCommandLog(sql);
				
				sql.append(")");
				
				_log(sql.toString());
			}else {
				_log("insert value is empty!");
			}
	}
	
	private void logUpdate(MongoSqlStruct struct) {
		log1Obj2Str(struct, "updateMany");
	}
	
	private void logGroup(MongoSqlStruct struct) {
		if (struct.getUpdateSetOrInsertOrFunOrOther() != null) {
			_log("This is an inaccurate raw statement!");
		}
		log1Obj2Str(struct, "aggregate");
	}

	private void log1Obj2Str(MongoSqlStruct struct, String opType) {
		String table = struct.getTableName();
//		table = table.replace(StringConst.ShardingTableIndexStr, "");
		
		StringBuffer sql = new StringBuffer();
		sql.append("db.");
		sql.append(table);
		sql.append(".");
		sql.append(opType);
		sql.append("(");
		
		boolean hasFiltre=false;

		if (struct.getFilter() != null) { 
			String filter=((BasicDBObject)struct.getFilter()).toString();
			sql.append(filter);
			hasFiltre=true;
			
			tranferCommandLog(sql);
		}
		
		if (struct.getUpdateSetOrInsertOrFunOrOther() != null) {
			if(hasFiltre) sql.append(",");
			sql.append(struct.getUpdateSetOrInsertOrFunOrOther().toString()); //notice
			
			tranferCommandLog(sql);
		}
		sql.append(")");
		
		_log(sql.toString());
	}
	
	
//	private void logDeleteMany(MongoSqlStruct struct) {
//		logDelete(struct, false);
//	}
	
	private void logDelete(MongoSqlStruct struct,boolean isDeleteOne) {
			String deleteType="deleteMany";
			if(isDeleteOne) deleteType="deleteOne";
			logWithFilter(struct, deleteType);
	}
	
	private void logCount(MongoSqlStruct struct) {
		logWithFilter(struct, "count");
	}
	
	private void logWithFilter(MongoSqlStruct struct, String opType) {
		String table = struct.getTableName();
//		table = table.replace(StringConst.ShardingTableIndexStr, "");
		
		StringBuffer sql = new StringBuffer();
		sql.append("db.");
		sql.append(table);
		sql.append(".");
		sql.append(opType);
		sql.append("(");
		
		if (struct.getFilter() != null) {
			String filter=((BasicDBObject)struct.getFilter()).toString();
			sql.append(filter);
			tranferCommandLog(sql);
		}
		
		sql.append(")");
		
		_log(sql.toString());
	}
	
	
	private void tranferCommandLog(StringBuffer sb) {
		_tranferDate(sb);
		_tranferOid(sb);
	}
	
	private void _tranferDate(StringBuffer sb) {	
		String target = ": {\"$oid\": ";
		String newStr = ": ObjectId(";
		boolean found=_tranferCommandLog(sb, target, newStr);
		if(found) _tranferDate(sb);
	}
	
	private void _tranferOid(StringBuffer sb) {
		String target = ": {\"$date\": ";
		String newStr = ": ISODate(";
		boolean found=_tranferCommandLog(sb, target, newStr);
		if(found) _tranferOid(sb);
	}
	
	private boolean _tranferCommandLog(StringBuffer sb,String target,String newStr) {
		String filter = sb.toString();
		boolean found=false;
//		String target = ": {\"$date\": ";
		int a = StringParser.getKeyPosition(filter, target);
		if (a > 0) {
			int b = StringParser.getKeyPosition(filter.substring(a), "}");
			if (b > 0) {
				sb.replace(a + b, a + b + 1, ")");
				sb.replace(a, a + target.length(), newStr);
				found=true;
			}
		}
		return found;
	}
	
	private void checkShardingSupport() {
		if (ShardingUtil.hadSharding()) {
			Logger.warn("Please notice this method do not support Sharding funtion by default! But you can use HintManager set the sharding table and dataSource name.");
		}
	}
}
