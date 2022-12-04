/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.teasoft.bee.mongodb.MongodbBeeSql;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.FunctionType;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.ObjSQLException;
import org.teasoft.bee.osql.OrderType;
import org.teasoft.honey.osql.core.ConditionImpl;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.JsonResultWrap;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.mongodb.MongoConditionHelper;
import org.teasoft.honey.osql.name.NameUtil;
import org.teasoft.honey.osql.shortcut.BF;
import org.teasoft.honey.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;

/**
 * @author Jade
 * @since  2.0
 */
public class MongodbSqlLib implements MongodbBeeSql {
	
	private static final String IDKEY = "_id";

	private static String _toTableName(Object entity){
		return NameTranslateHandle.toTableName(NameUtil.getClassFullName(entity));
	}
	
	private MongoDatabase getMongoDatabase() {
		System.err.println("multiDS_enable:"+HoneyConfig.getHoneyConfig().multiDS_enable);
		if (!HoneyConfig.getHoneyConfig().multiDS_enable) {
			return SingleMongodbFactory.getMongoDb();
		} else {
			MongoClient client = (MongoClient) HoneyContext.getDatabaseClient();
//			client.getDatabase(databaseName);
//			return client.getDatabase("bee");
			
			MongoDatabase db=client.getDatabase("bee");
			return db;
		}
	}
	
	@Override
	public <T> List<T> select(T entity) {

		String tableName = _toTableName(entity); // 1 TODO 分片时,看下是否带下标
//		MongoUtils.getCollection(tableName)   //2. TODO 分片时 要使用 动态获取的ds拿db.

		Document doc =toDocument(entity);
		FindIterable<Document> docIterable = null;
		if (doc != null)
			docIterable = getMongoDatabase().getCollection(tableName).find(doc);
		else
			docIterable = getMongoDatabase().getCollection(tableName).find();

		return TransformResult.toListEntity(docIterable, entity);
	}
	
	
//	@Override
//	public <T> List<T> select(T entity, int start, int size) {
//		String tableName = _toTableName(entity); 
//
//		Document doc =toDocument(entity);
//		FindIterable<Document> docIterable = null;
//		if (doc != null)
//			docIterable = MongoUtils.getCollection(tableName).find(doc).skip(start).limit(size);
//		else
//			docIterable = MongoUtils.getCollection(tableName).find().skip(start).limit(size);
//
//		return ConvertUtil.toListEntity(docIterable, entity);
//	}
	
	@Override
	public <T> List<T> selectOrderBy(T entity, String orderFields, OrderType[] orderTypes) {
		String tableName = _toTableName(entity); 

		Document doc =toDocument(entity);
		FindIterable<Document> docIterable = null;
		Bson sortBson=ParaConvertUtil.toSortBson(orderFields.split(","), orderTypes);
		if (doc != null)
			docIterable = getMongoDatabase().getCollection(tableName).find(doc);
		else
			docIterable = getMongoDatabase().getCollection(tableName).find();
		
		if(sortBson!=null)  docIterable=docIterable.sort(sortBson);

		return TransformResult.toListEntity(docIterable, entity);
	}

	public <T> String selectJson(T entity) {

//		String tableName = _toTableName(entity); // 1 TODO 分片时,看下是否带下标
////		MongoUtils.getCollection(tableName)   //2. TODO 分片时 要使用 动态获取的ds拿db.
//
//		Document doc =toDocument(entity);
//		FindIterable<Document> docIterable = null;
//		if (doc != null)
//			docIterable = MongoUtils.getCollection(tableName).find(doc);
//		else
//			docIterable = MongoUtils.getCollection(tableName).find();
//		
//		return "";
//        //TODO 要转成String.
////		return ConvertUtil.toListEntity(docIterable, entity);
		
		return  selectJson(entity, null);
	}

	@Override
	public <T> int update(T entity) {
		String tableName = _toTableName(entity);
		Document doc = null;
		try {
			
			String pkName=HoneyUtil.getPkFieldName(entity);
			if ("".equals(pkName)) pkName="id";
			String pks[]=pkName.split(",");
			
			if(pks.length<1)
			    throw new ObjSQLException("ObjSQLException: in the update(T entity) or update(T entity,IncludeType includeType), the id field is missing !");
			
			
//			doc = ConvertUtil.toBson(entity);
			Map<String,Object> map = ParaConvertUtil.toMap(entity);
			
			Document filter=new Document();
			String column="";
			for (int i = 0; i < pks.length; i++) {
				column=pks[i];
//				System.out.println("column:    "+column);
				if ("id".equalsIgnoreCase(column)) {// 替换id为_id
					column = IDKEY;
                }
				filter.append(column, map.get(column));
				map.remove(column);
			}
			
			doc=new Document(map);
			Document updateDocument=new Document("$set",doc);
			
			UpdateResult rs=getMongoDatabase().getCollection(tableName).updateMany(filter, updateDocument);
			return (int)rs.getModifiedCount();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public <T> int insert(T entity) {
		String tableName = _toTableName(entity);
		Document doc = null;
		try {
//			doc = ConvertUtil.toBson(entity);
			Map<String,Object> map = ParaConvertUtil.toMap(entity);
			doc=new Document(map);
			getMongoDatabase().getCollection(tableName).insertOne(doc);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	@Override
	public <T> int delete(T entity) {
		String tableName = _toTableName(entity);
		Document doc = null;
		try {
			Map<String,Object> map = ParaConvertUtil.toMap(entity);
			doc=new Document(map);
			DeleteResult rs=getMongoDatabase().getCollection(tableName).deleteMany(doc);
			return (int)rs.getDeletedCount();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	int getIncludeType(Condition condition) {
		if(condition==null) return -1;
		return condition.getIncludeType() == null ? -1 : condition.getIncludeType().getValue();
	}
	
	private <T> Document toDocument(T entity, Condition condition) {
		Document doc = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity, getIncludeType(condition));
			Map<String, Object> map2 = MongoConditionHelper.processCondition(condition);
			if (ObjectUtils.isNotEmpty(map) && ObjectUtils.isNotEmpty(map2))
				map.putAll(map2); //map的值,会被map2中有同样key的值覆盖.
			else if (ObjectUtils.isEmpty(map)) map = map2;

			if (ObjectUtils.isNotEmpty(map)) doc = new Document(map);

		} catch (Exception e) {
//			e.printStackTrace();
			throw ExceptionHelper.convert(e);
		}
		
		return doc;
	}
	
	@Override
	public <T> List<T> select(T entity, Condition condition) {
////		String tableName = _toTableName(entity); // 1 TODO 分片时,看下是否带下标
////		MongoUtils.getCollection(tableName)   //2. TODO 分片时 要使用 动态获取的ds拿db.
//
////		Document doc = null;
////		try {
////			Map<String, Object> map = ConvertUtil.toMap(entity, getIncludeType(condition));
////			Map<String, Object> map2 = ConvertUtil.processCondition(condition);
////			if (ObjectUtils.isNotEmpty(map) && ObjectUtils.isNotEmpty(map2))
////				map.putAll(map2);
////			else if (ObjectUtils.isEmpty(map)) map = map2;
////
////			if (ObjectUtils.isNotEmpty(map)) doc = new Document(map);
////
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//		
//		String tableName = _toTableName(entity);
//		Document doc=toDocument(entity, condition);
//
//		FindIterable<Document> docIterable = null;
//		if (doc != null)
//			docIterable = MongoUtils.getCollection(tableName).find(doc);
//		else
//			docIterable = MongoUtils.getCollection(tableName).find();
//		
//		
//		ConditionImpl conditionImpl = (ConditionImpl) condition;
//		
//		Integer size = conditionImpl.getSize();
//		Integer start=conditionImpl.getStart();
//		if(size!=null && size>0) {
//			if(start== null || start<0)  start=0;
//			docIterable=docIterable.skip(start).limit(size);
//		}
//
//		String[] selectFields=conditionImpl.getSelectField();
//		if(selectFields!=null) {
//			docIterable=docIterable.projection(fields(include(selectFields)));
//		}
//		
////				 cols.find().projection(fields(include("username","pwd"),excludeId()));//返回username与pwd字段且不返回_id字段
//		
//		Bson sortBson=ConvertUtil.toSortBson(condition);
//		if(sortBson!=null)  docIterable=docIterable.sort(sortBson);
		
		FindIterable<Document> docIterable = findIterableDocument(entity, condition);

		return TransformResult.toListEntity(docIterable, entity);
	}
	
	
	
	
	
	@Override
	public <T> String selectJson(T entity, Condition condition) {
		FindIterable<Document> docIterable = findIterableDocument(entity, condition);
		
		JsonResultWrap wrap= TransformResult.toJson(docIterable.iterator(), entity);
		return wrap.getResultJson();

//		StringBuffer json = new StringBuffer("");
//		try {
//			MongoCursor<Document> cursor = docIterable.iterator();
//			while (cursor.hasNext()) {
//				json.append(",");
//				Document document = cursor.next();
//				//https://blog.csdn.net/weixin_43744732/article/details/124400357
//				JsonWriterSettings settings = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
//				 json.append(document.toJson(settings));
////				 [{"_id": 10003, "name": "mongodb", "abc": ""},    //字段名转换. TODO
////				 https://www.likecs.com/ask-1223565.html
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (json.length() > 0) {
//			json.deleteCharAt(0);
//		}
//		json.insert(0, "[");
//		json.append("]");
//
//        return json.toString();
        
//        [{"_id": {"$numberLong": "10003"}, "name": "mongodb", "abc": ""},
	}

	public <T> FindIterable<Document> findIterableDocument(T entity, Condition condition) {
		
		String tableName = _toTableName(entity);
		Document doc=toDocument(entity, condition);

		FindIterable<Document> docIterable = null;
		if (doc != null)
			docIterable = getMongoDatabase().getCollection(tableName).find(doc);
		else
			docIterable = getMongoDatabase().getCollection(tableName).find();
		
		if (condition == null) return docIterable;
		
		ConditionImpl conditionImpl = (ConditionImpl) condition;
		
		Integer size = conditionImpl.getSize();
		Integer start=conditionImpl.getStart();
		if(size!=null && size>0) {
			if(start== null || start<0)  start=0;
			docIterable=docIterable.skip(start).limit(size);
		}

		String[] selectFields=conditionImpl.getSelectField();
		if(selectFields!=null) {
			if(selectFields.length==1) selectFields=selectFields[0].split(",");
			boolean hasId=false;
			for (int i = 0; i < selectFields.length; i++) {
				if("id".equalsIgnoreCase(selectFields[i])) {
					selectFields[i]=IDKEY;
					hasId=true;
					break;
				}
			}
			
			if (hasId)
				docIterable = docIterable.projection(fields(include(selectFields)));
			else
				docIterable = docIterable.projection(fields(include(selectFields), excludeId()));
		}
		
//				 cols.find().projection(fields(include("username","pwd"),excludeId()));//返回username与pwd字段且不返回_id字段
		
		Bson sortBson=ParaConvertUtil.toSortBson(condition);
		if(sortBson!=null)  docIterable=docIterable.sort(sortBson);

		return docIterable;
	}
	
	@Override
	public <T> int delete(T entity, Condition condition) {
		String tableName = _toTableName(entity);
		
		Document filter=toDocument(entity, condition);
		
//		FindIterable<Document> docIterable = null;
		DeleteResult rs=null;
		if (filter != null)
			 rs = getMongoDatabase().getCollection(tableName).deleteMany(filter);
		else
			 rs = getMongoDatabase().getCollection(tableName).deleteMany(null);  //TODO
		
		return (int)rs.getDeletedCount();
	}
	
	
	@Override
	public <T> int update(T oldEntity, T newEntity) {
		String tableName = _toTableName(oldEntity);
		try {
			Map<String, Object> oldMap = ParaConvertUtil.toMap(oldEntity);
			Map<String, Object> newMap = ParaConvertUtil.toMap(newEntity);
			return update(oldMap, newMap, tableName);
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
			return -1;
		}
	}
	
	@Override
	public <T> int update(T entity, String setFields, Condition condition) {
		String tableName = _toTableName(entity);
		Map<String, Object> reMap[] = toMapForUpdateSet(entity, setFields, condition);
		return update(reMap[0], reMap[1], tableName);
	}

	@Override
	public <T> int updateBy(T entity, String whereFields, Condition condition) {
		String tableName = _toTableName(entity);
		Map<String, Object> reMap[] = toMapForUpdateBy(entity, whereFields, condition);
		return update(reMap[0], reMap[1], tableName);
	}
	
	private <T> int update(Map<String, Object> filterMap, Map<String, Object> newMap, String tableName) {
//		String tableName = _toTableName(oldEntity);
		Document oldDoc = null;
		Document newDoc = null;
		try {
//			Map<String, Object> oldMap = ConvertUtil.toMap(oldEntity);
//			Map<String, Object> newMap = ConvertUtil.toMap(newEntity);

			oldDoc = new Document(filterMap); // filter
			newDoc = new Document(newMap);
			Document updateDocument = new Document("$set", newDoc);

			UpdateResult rs = getMongoDatabase().getCollection(tableName).updateMany(oldDoc,
					updateDocument);
			return (int) rs.getModifiedCount();
		} catch (Exception e) {
			Logger.warn(e.getMessage(), e);
			return -1;
		}
	}

	
	private <T> Map<String, Object>[] toMapForUpdateBy(T entity , String specialFields, Condition condition) {
		return toMapForUpdate(entity, condition, specialFields, true);
	}
	
	private <T> Map<String, Object>[] toMapForUpdateSet(T entity, String specialFields, Condition condition ) {
		return toMapForUpdate(entity, condition, specialFields, false);
	}
	
//	没指定为whereFields的字段,作为set部分(默认只处理非空,非null的字段)
//	condition中op,between,notBetween方法设置的字段,不受includeType的值影响
	private <T> Map<String, Object>[] toMapForUpdate(T entity, Condition condition, String specialFields,
			boolean isFilterField) {
		Map<String, Object> reMap[] = new Map[2];
		try {
			Map<String, Object> emap = ParaConvertUtil.toMap(entity, getIncludeType(condition));

			Map<String, Object> filterMapFromC = MongoConditionHelper.processCondition(condition);
			Map<String, Object> setMapFromC = MongoConditionHelper.processCondition(condition); // TODO 获取set的部分

			String fields[] = specialFields.split(",");
			// 转换字段???? TODO
			Map<String, Object> specialMap = new LinkedHashMap<String, Object>();
			for (int i = 0; i < fields.length; i++) {
				if (emap.containsKey(fields[i])) {
//					一个字段既在指定的updateFields,也用在了Condition.set(arg1,arg2)等方法设置,entity里相应的字段会按规则转化到where部分.(V1.9.8)
					if (!isFilterField && setMapFromC != null && setMapFromC.containsKey(fields[i])) continue;

					specialMap.put(fields[i], emap.get(fields[i]));
					emap.remove(fields[i]);
				}
			}

			Map<String, Object> filterMap;
			Map<String, Object> setMap;
			if (isFilterField) {
				filterMap = specialMap;
				setMap = emap;
			} else {
				filterMap = emap;
				setMap = specialMap;
			}

			// 再根据specialFields, update, updateBy 排除不需要的字段 TODO
			if (ObjectUtils.isNotEmpty(filterMapFromC)) filterMap.putAll(filterMapFromC);
			if (ObjectUtils.isNotEmpty(setMapFromC)) setMap.putAll(setMapFromC);

			reMap[0] = filterMap;
			reMap[1] = setMap;

		} catch (Exception e) {
			throw ExceptionHelper.convert(e);
		}

		return reMap;
	}

	@Override
	public <T> int insert(T entity[], int batchSize, String excludeFields) {
		String tableName = _toTableName(entity[0]);
		int len = entity.length;
		List<Document> list = null;
		if (len> 0) list = new ArrayList<>();
		int count = 0;
		for (int i = 1; i <= len; i++) {  //i   1..len
			Document doc = toDocumentExcludeSome(entity[i-1], excludeFields);
			list.add(doc);
			if (i % batchSize == 0 || i == len) {
				InsertManyResult irs=getMongoDatabase().getCollection(tableName).insertMany(list);
				System.out.println(irs.getInsertedIds());
				count+=irs.getInsertedIds().size();
//				MongoUtils.getCollection(tableName).bulkWrite(list);
				if (i != len) list = new ArrayList<>();
			}
		}
//		count = len; // TODO 获取记录数???

		return count;
	}
	
	
	private <T> Document toDocument(T entity) {
		Document doc = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMap(entity);
			if (ObjectUtils.isNotEmpty(map)) doc = new Document(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	
	private <T> Document toDocumentExcludeSome(T entity,String excludeFields) {
		Document doc = null;
		try {
			Map<String, Object> map = ParaConvertUtil.toMapExcludeSome(entity,excludeFields);
			doc = new Document(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	
	@SuppressWarnings("rawtypes")
	private <T> Object[] processId(Class clazz, Object id) {
		
		Object[] obj=new Object[2];
		Document one = new Document();
		Bson moreFilter=null;

		if (id instanceof String) {
			String ids[] = ((String) id).split(",");
			String idType = getIdType(clazz, getPkName(clazz));
			if (ids.length > 1) {
				Document idFilters[] = new Document[ids.length];
				int k=0;
				for (String idValue : ids) {
					if("String".equals(idType) && isMongodbId(idValue)) 
						idFilters[k++]=new Document(IDKEY,new ObjectId(idValue));  //改为in 也可以
					else 	idFilters[k++]=new Document(IDKEY,tranIdObject(idType, idValue));  //改为in 也可以
					
				}
				moreFilter=Filters.or(idFilters);
			} else {
				System.out.println("idType: "+idType);
				if ("String".equals(idType) && isMongodbId(ids[0]))
					one.put(IDKEY, new ObjectId(ids[0]));
				else
					one.put(IDKEY, tranIdObject(idType, ids[0]));
				 
			}
		} else {
			one.put(IDKEY, id);
		}
		
		obj[0]=one;
		obj[1]=moreFilter;
		
		return obj;
    }
	
	private static boolean isMongodbId(final String hexString) {
		if (hexString == null) {
			return false;
		}

		int len = hexString.length();
		if (len != 24) {
			return false;
		}

		for (int i = 0; i < len; i++) {
			char c = hexString.charAt(i);
			if (c >= '0' && c <= '9') {
				continue;
			}
			if (c >= 'a' && c <= 'f') {
				continue;
			}
			if (c >= 'A' && c <= 'F') {
				continue;
			}
			return false;
		}
		return true;
	}
	

	@Override
	public <T> List<T> selectById(T entity, Object id) {

		String tableName = _toTableName(entity);

		Object[] obj = processId(entity.getClass(), id);
		Document one = (Document) obj[0];
		Bson moreFilter = (Bson) obj[1];

		FindIterable<Document> docIterable = null;
		if (moreFilter != null)
			docIterable = getMongoDatabase().getCollection(tableName).find(moreFilter);
		else
			docIterable = getMongoDatabase().getCollection(tableName).find(one);

		return TransformResult.toListEntity(docIterable, entity);
	}
	
	@SuppressWarnings("rawtypes")
	private String getIdType(Class clazz,String pkName) {
		Field field = null;
		String type=null;
		try {
			field = clazz.getDeclaredField(pkName);
			type=field.getType().getSimpleName();
		} catch (Exception e) {
			//ignore
		}
		
		return type;
	}
	
	@SuppressWarnings("rawtypes")
	private String getPkName(Class c) {
		try {
			c.getDeclaredField("id");  //V1.11 因主键可以不是默认id,多了此步检测
			return "id";
		} catch (NoSuchFieldException e) {
			String pkName = HoneyUtil.getPkFieldNameByClass(c);
			if ("".equals(pkName))
				throw new ObjSQLException("No primary key in " + c.getName());
			if (pkName.contains(",")) throw new ObjSQLException(
					"method of selectById just need one primary key, but more than one primary key in "+ c.getName());
			
			return pkName;
		}
	}
	
	public Object tranIdObject(String idType, String idValue) {
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
		String tableName=_toTableNameByClass(c);
		
		Object[] obj = processId(c, id);
		Document one = (Document) obj[0];
		Bson moreFilter = (Bson) obj[1];

		DeleteResult rs = null;
		if (moreFilter != null)
			rs = getMongoDatabase().getCollection(tableName).deleteMany(moreFilter);
		else
			rs = getMongoDatabase().getCollection(tableName).deleteOne(one);
		
		return (int)rs.getDeletedCount();
	}
	
	
	@Override
	public <T> int count(T entity, Condition condition) {
		String tableName = _toTableName(entity);

		Document filter = toDocument(entity, condition);
		int c;
		if (filter != null)
			c = (int) getMongoDatabase().getCollection(tableName).countDocuments(filter);
		else
			c = (int) getMongoDatabase().getCollection(tableName).countDocuments();

		return c;
	}
	
	@Override
	public <T> long insertAndReturnId(T entity, IncludeType includeType) {
		
		String tableName = _toTableName(entity);
		
		Condition condition=null;
		if(includeType!=null)
			condition=BF.getCondition().setIncludeType(includeType);
		Document doc=toDocument(entity, condition);
		
		return getMongoDatabase().getCollection(tableName).insertOne(doc).getInsertedId().asInt64().longValue();
	}
	
	
	@Override
	public <T> String selectWithFun(T entity, FunctionType functionType, String fieldForFun,
			Condition condition) {

		String tableName = _toTableName(entity);

		Document filter = toDocument(entity, condition); // 加过滤条件. TODO

//		Map map=new HashMap();
//		map.put("$name", "mongodb99");
//		Document filter=new Document(map);  //过滤条件,要放在match里

		MongoCollection<Document> collection = getMongoDatabase().getCollection(tableName);

		List<Bson> listBson = new ArrayList<>();
		Bson funBson = null;
		if ("id".equalsIgnoreCase(fieldForFun)) fieldForFun = IDKEY;
		if (filter != null) listBson.add(Aggregates.match(filter)); // 过滤条件,要放在match里

		if (FunctionType.MAX == functionType) {
//			fun=Arrays.asList(Aggregates.match(filter), group(null, max("_fun", "$"+fieldForFun)) );
			funBson = group(null, max("_fun", "$" + fieldForFun));
		} else if (FunctionType.MIN == functionType) {
			funBson = group(null, min("_fun", "$" + fieldForFun));
		} else if (FunctionType.AVG == functionType) {
			funBson = group(null, avg("_fun", "$" + fieldForFun));
		} else if (FunctionType.SUM == functionType) {
			funBson = group(null, sum("_fun", "$" + fieldForFun));   //统计的值为null时, sum: 0
		}
		listBson.add(funBson);

		Document rs = collection.aggregate(listBson).first();

//		System.err.println(rs.toJson());

		Map<String, Object> jsonMap = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			jsonMap = objectMapper.readValue(rs.toJson(),
					new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		if (jsonMap != null && jsonMap.get("_fun") != null)
			return jsonMap.get("_fun").toString();
		else
			return "";
	}
	
	@Override
	public <T> List<String[]> selectString(T entity, Condition condition) {
		FindIterable<Document> docIterable = findIterableDocument(entity, condition);
		return TransformResult.toListString(docIterable.iterator(), entity,condition.getSelectField());
	}

	@SuppressWarnings("rawtypes")
	private String _toTableNameByClass(Class c){
		return NameTranslateHandle.toTableName(c.getName());
	}
}
