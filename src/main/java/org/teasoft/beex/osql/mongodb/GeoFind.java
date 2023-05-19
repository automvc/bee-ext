package org.teasoft.beex.osql.mongodb;

import java.util.List;

import org.bson.conversions.Bson;
import org.teasoft.bee.osql.api.Condition;

public interface GeoFind {
	<T> List<T> geoFind(T entity, Bson geoBson, Condition condition);
}
