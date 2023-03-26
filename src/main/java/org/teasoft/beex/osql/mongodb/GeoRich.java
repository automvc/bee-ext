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

import java.util.List;

import org.bson.conversions.Bson;
import org.teasoft.bee.mongodb.Geo;

import com.mongodb.client.model.geojson.Geometry;

/**
 * @author Kingstar
 * @since  2.1
 */
public interface GeoRich extends Geo{
	
	public <T> List<T> near(T entity,String fieldName, final double x, final double y,
			final Double maxDistance, final Double minDistance);
	
	public <T> List<T> nearSphere(T entity,String fieldName, final double x, final double y,
			final Double maxDistance, final Double minDistance);
	
	public <T> List<T> geoWithinCenter(T entity,String fieldName, final double x, final double y,
			final double radius);
	public <T> List<T> geoWithinCenterSphere(T entity,String fieldName, final double x, final double y,
			final double radius);
	
	public <T> List<T> geoWithinBox(T entity,String fieldName, final double lowerLeftX,
			final double lowerLeftY, final double upperRightX, final double upperRightY);
	
	public <T> List<T> geoWithin(T entity,String fieldName, final Geometry geometry);
	public <T> List<T> geoWithin(T entity,String fieldName, final Bson geometry);
	public <T> List<T> geoIntersects(T entity,String fieldName, final Bson geometry);
	public <T> List<T> geoIntersects(T entity,String fieldName, final Geometry geometry);
	
}
