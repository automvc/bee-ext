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

package org.teasoft.beex.osql.mongodb;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;
import org.teasoft.bee.mongodb.BoxPara;
import org.teasoft.bee.mongodb.CenterPara;
import org.teasoft.bee.mongodb.GridFsFile;
import org.teasoft.bee.mongodb.NearPara;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidType;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.exception.BeeIllegalBusinessException;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.beex.osql.FieldNameUtil;
import org.teasoft.beex.osql.FieldNameUtil.SerialFunction;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;
import org.teasoft.honey.osql.core.NameTranslateHandle;
import org.teasoft.honey.osql.name.OriginalName;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

/**
 * @author Kingstar
 * @since  2.1
 */
@SuppressWarnings("unchecked")
public class MongodbObjSQLRichExt extends MongodbObjSQLRich implements MongodbSuidRichExt {
	
	private static final long serialVersionUID = 1596710362269L;

	@Override
	public <T> List<T> select(T entity, SerialFunction<T, ?>... selectFields) {
		return select(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> List<T> select(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields) {
		return select(entity, start, size, getFieldNames(selectFields));
	}

	@Override
	public <T> List<String[]> selectString(T entity, SerialFunction<T, ?>... selectFields) {
		return selectString(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> int update(T entity, SerialFunction<T, ?>... updateFields) {
		return update(entity, getFieldNames(updateFields));
	}

	@Override
	public <T> int update(T entity, IncludeType includeType,
			SerialFunction<T, ?>... updateFields) {
		return update(entity, includeType, getFieldNames(updateFields));
	}

	@Override
	public <T> String selectJson(T entity, SerialFunction<T, ?>... selectFields) {
		return selectJson(entity, getFieldNames(selectFields));
	}

	@Override
	public <T> String selectJson(T entity, int start, int size,
			SerialFunction<T, ?>... selectFields) {
		return selectJson(entity, start, size, getFieldNames(selectFields));
	}

	@Override
	public <T> int updateBy(T entity, SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, getFieldNames(whereFields));
	}

	@Override
	public <T> int updateBy(T entity, IncludeType includeType,
			SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, includeType, getFieldNames(whereFields));
	}

	@Override
	public <T> int updateBy(T entity, Condition condition,
			SerialFunction<T, ?>... whereFields) {
		return updateBy(entity, condition, getFieldNames(whereFields));
	}

	@Override
	public <T> int update(T entity, Condition condition, SerialFunction<T, ?>... updateFields) {
		return update(entity, condition, getFieldNames(updateFields));
	}

	private <T> String[] getFieldNames(SerialFunction<T, ?>... fns) {
		String[] fieldNames = FieldNameUtil.getFieldNames(fns);
		return fieldNames;
	}

	
	/////////////// operate file,eg: GridFS
	
	@Override
	public String uploadFile(String filename, InputStream fileStream) {
		return getMongodbBeeSql().uploadFile(filename, fileStream);
	}

	@Override
	public String uploadFile(String filename, InputStream fileStream,
			Map<String, Object> metadataMap) {
		return getMongodbBeeSql().uploadFile(filename, fileStream, metadataMap);
	}

	@Override
	public List<GridFsFile> selectFiles(GridFsFile gridFsFile, Condition condition) {
		setNameTranslateOneTime(new OriginalName());
		doBeforePasreEntity(gridFsFile, SuidType.SELECT);
		List<GridFsFile> list = getMongodbBeeSql().selectFiles(gridFsFile, condition);
		doBeforeReturn(list);
		return list;
	}

	@Override
	public byte[] getFileByName(String fileName) {
		return getMongodbBeeSql().getFileByName(fileName);
	}

	@Override
	public byte[] getFileById(String fileId) {
		return getMongodbBeeSql().getFileById(fileId);
	}

	@Override
	public void renameFile(String fileId, String newName) {
		getMongodbBeeSql().renameFile(fileId, newName);
	}

	@Override
	public void deleteFile(String fileId) {
		getMongodbBeeSql().deleteFile(fileId);
	}

	//------------------------------------create index-------------------------------
	@Override
	public String index(String collectionName, String fieldName, IndexType indexType) {
		return ((MongodbSqlLib) getMongodbBeeSql()).index(collectionName, fieldName, indexType);
	}

	@Override
	public String unique(String collectionName, String fieldName, IndexType indexType) {
		return ((MongodbSqlLib) getMongodbBeeSql()).unique(collectionName, fieldName, indexType);
	}

	@Override
	public List<String> indexes(String collectionName, List<IndexPair> indexes) {
		return ((MongodbSqlLib) getMongodbBeeSql()).indexes(collectionName, indexes);
	}

	@Override
	public void dropIndexes(String collectionName) {
		((MongodbSqlLib) getMongodbBeeSql()).dropIndexes(collectionName);
	}

	
	//----------------------GEO-----------------------start-----------------------------
	
	private static int type_near=1;
	private static int type_nearSphere=2;
	private static int type_geoWithinCenter=3;
	private static int type_geoWithinCenterSphere=4;
	
	private Bson getGeoBson(NearPara nearPara, int type) {
		Bson geoBson = null;
		Point refPoint = new Point(new Position(nearPara.getX(), nearPara.getY()));
		Double max = nearPara.getMaxDistance();
		Double min = nearPara.getMinDistance();
		if (max < min) throw new BeeIllegalBusinessException("The maximum value must not be less than the minimum value!");

		if (type == type_near) {
			geoBson = Filters.near(_toColumnName(nearPara.getGeoFieldName()), refPoint, max, min);
		} else if (type == type_nearSphere) 
			geoBson = Filters.nearSphere(_toColumnName(nearPara.getGeoFieldName()), refPoint, max, min);

		return geoBson;
	}
	
	private Bson getGeoBson(CenterPara centerPara, int type) {
		Bson geoBson = null;
//		Point refPoint = new Point(new Position(centerPara.getX(), centerPara.getY()));
		if (type == type_geoWithinCenter) {
			geoBson = Filters.geoWithinCenter(_toColumnName(centerPara.getGeoFieldName()),
					centerPara.getX(), centerPara.getY(), centerPara.getRadius());
		} else if (type == type_geoWithinCenterSphere)
			geoBson = Filters.geoWithinCenterSphere(_toColumnName(centerPara.getGeoFieldName()),
					centerPara.getX(), centerPara.getY(), centerPara.getRadius());
		return geoBson;
	}
	
	private Bson getGeoBson2(BoxPara boxPara) {
		Bson geoBson = null;
		geoBson = Filters.geoWithinBox(boxPara.getGeoFieldName(), boxPara.getLowerLeftX(),
				boxPara.getLowerLeftY(), boxPara.getUpperRightX(), boxPara.getUpperRightY());
		return geoBson;
	}
	
	private Bson getGeoBson3(String fieldName, List<List<Double>> points) {
		Bson geoBson = null;
		geoBson = Filters.geoWithinPolygon(fieldName, points);
		return geoBson;
	}
	
	
	private <T> List<T> _near(T entity, NearPara nearPara,Condition condition, int type) { // near,nearSphere
		Bson geoBson=getGeoBson(nearPara, type);
		return _geoFind(entity, geoBson, condition);
	}
	
	private  <T> List<T> _geoWithinCenter(T entity, CenterPara centerPara ,Condition condition, int type) {
		// geoWithinCenterSphere, geoWithinCenter
		Bson geoBson=getGeoBson(centerPara, type);
		return _geoFind(entity, geoBson, condition);
	}
	
	@Override
	public <T> List<T> near(T entity, NearPara nearPara, Condition condition) {
		return _near(entity, nearPara, condition, type_near);
	}

	@Override
	public <T> List<T> nearSphere(T entity, NearPara nearPara, Condition condition) {
		return _near(entity, nearPara, condition, type_nearSphere);
	}

	@Override
	public <T> List<T> geoWithinCenter(T entity, CenterPara centerPara, Condition condition) {
		return _geoWithinCenter(entity, centerPara, condition, type_geoWithinCenter);
	}

	@Override
	public <T> List<T> geoWithinCenterSphere(T entity, CenterPara centerPara,
			Condition condition) {
		return _geoWithinCenter(entity, centerPara, condition, type_geoWithinCenterSphere);
	}

	@Override
	public <T> List<T> geoWithinBox(T entity, BoxPara boxPara, Condition condition) {
		Bson geoBson=getGeoBson2(boxPara);
		return _geoFind(entity, geoBson, condition);
	}
	
	@Override
	public <T> List<T> geoWithinPolygon(T entity, String fieldName, List<List<Double>> points,
			Condition condition) {
		fieldName=_toColumnName(fieldName);
		Bson geoBson=getGeoBson3(fieldName, points);
		return _geoFind(entity, geoBson, condition);
	}
	
	private String _toColumnName(String fieldName) {
		return NameTranslateHandle.toColumnName(fieldName);
	}
	
	
	//----------------------GEO-----------------------...-----------------------------
	
	@Override
	public <T> List<T> near(T entity, String fieldName, double x, double y, Double maxDistance,
			Double minDistance) {
		fieldName=_toColumnName(fieldName);
		return near(entity, new NearPara(fieldName, x, y, maxDistance, minDistance), null);
	}

	@Override
	public <T> List<T> nearSphere(T entity, String fieldName, double x, double y,
			Double maxDistance, Double minDistance) {
		fieldName=_toColumnName(fieldName);
		return nearSphere(entity, new NearPara(fieldName, x, y, maxDistance, minDistance), null);
	}

	@Override
	public <T> List<T> geoWithinCenter(T entity, String fieldName, double x, double y,
			double radius) {
		fieldName=_toColumnName(fieldName);
		return geoWithinCenter(entity, new CenterPara(fieldName, x, y, radius), null);
	}

	@Override
	public <T> List<T> geoWithinCenterSphere(T entity, String fieldName, double x, double y,
			double radius) {
		fieldName=_toColumnName(fieldName);
		return geoWithinCenterSphere(entity, new CenterPara(fieldName, x, y, radius), null);
	}

	@Override
	public <T> List<T> geoWithinBox(T entity, String fieldName, double lowerLeftX,
			double lowerLeftY, double upperRightX, double upperRightY) {
		fieldName=_toColumnName(fieldName);
		return geoWithinBox(entity,new BoxPara(fieldName, lowerLeftX, lowerLeftY, upperRightX, upperRightY), null);
	}

	//-------------------------------------------------

	@Override
	public <T> List<T> geoWithin(T entity, String fieldName, Geometry geometry) {
		fieldName=_toColumnName(fieldName);
		Bson geoBson= Filters.geoWithin(fieldName,geometry);
		return _geoFind(entity, geoBson, null);
	}
	@Override
	public <T> List<T> geoWithin(T entity, String fieldName, Bson geometry) {
		fieldName=_toColumnName(fieldName);
		Bson geoBson= Filters.geoWithin(fieldName,geometry);
		return _geoFind(entity, geoBson, null);
	}
	@Override
	public <T> List<T> geoIntersects(T entity, String fieldName, Bson geometry) {
		fieldName=_toColumnName(fieldName);
		Bson geoBson= Filters.geoIntersects(fieldName,geometry);
		return _geoFind(entity, geoBson, null);
	}
	@Override
	public <T> List<T> geoIntersects(T entity, String fieldName, Geometry geometry) {
		fieldName=_toColumnName(fieldName);
		Bson geoBson= Filters.geoIntersects(fieldName,geometry);
		return _geoFind(entity, geoBson, null);
	}
	
	
	
	
	//重要
	private <T> List<T> _geoFind(T entity, Bson geoBson, Condition condition) {
		if (entity == null)
			return null;
		regCondition(condition); 
		doBeforePasreEntity(entity, SuidType.SELECT);
		if (condition != null)
			condition.setSuidType(SuidType.SELECT);

		List<T> list = ((MongodbSqlLib) getMongodbBeeSql()).geoFind(entity, geoBson, condition);

		doBeforeReturn(list);
		return list;
	}
	
	//----------------------GEO-----------------------end-----------------------------
	
}
