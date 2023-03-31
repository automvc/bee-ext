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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.teasoft.beex.mongodb.ds.SingleMongodbFactory;
import org.teasoft.honey.util.StringUtils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

/**
 * Util for Mongodb
 * @author Kingstar
 * @since  2.0
 */
public class MongodbUtil {

	@SuppressWarnings("unchecked")
	public static Set<Map.Entry<String, Object>> getCollectStrcut(String collectionName) {

		MongoDatabase mongoDatabase = SingleMongodbFactory.getMongoDb(); // 单个数据源时,
		
		MongoCollection<Document> cols = mongoDatabase.getCollection(collectionName); // Collection 相当于表 ;Document相当于行.
		FindIterable<Document> findIterable = cols.find().limit(1);
		MongoCursor<Document> mongoCursor = findIterable.iterator();

		if (mongoCursor.hasNext()) { // 必须要有数据，不然不行。 没数据， navicat上也看不到结构。
			Document d = mongoCursor.next();
			return d.entrySet();
		}

		return Collections.EMPTY_SET;
	}
	
	public static String[] getAllCollectionNames() {
		MongoDatabase mongoDatabase = SingleMongodbFactory.getMongoDb(); // 单个数据源时,

		MongoIterable<String> itera = mongoDatabase.listCollectionNames();
		List<String> list = new ArrayList<>();
		for (String s : itera) {
			list.add(s);
		}

		return StringUtils.listToArray(list);
	}
	
	public static boolean isMongodbId(final String hexString) {
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
}
