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

package org.teasoft.beex.mongodb.ds;

import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyConfig;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Single Mongodb Factory for connection.
 * @author Kingstar
 * @since  2.0
 */
public class SingleMongodbFactory {
	
	private static MongodbManager manager = null;
	private static MongoClient mongoClient = null;

	static {
		HoneyConfig config = HoneyConfig.getHoneyConfig();
		manager = new MongodbManager(config.getUrl(), config.getUsername(), config.getPassword());
		mongoClient = manager.getMongoClient();
	}
	
	private SingleMongodbFactory() {}

	public static MongoDatabase getMongoDb() {
		MongoDatabase db = null;
		try {
			if (Boolean.TRUE == MongoContext.getCurrentBeginFirst()) {// tran 首次
				MongoClient mongoClient0 = manager.getMongoClient();
				MongoContext.setCurrentMongoClient(mongoClient0);
				return mongoClient0.getDatabase(manager.getDatabaseName());
			} else if (MongoContext.getCurrentClientSession() != null) { // 同一tran，非首次获取
				return MongoContext.getCurrentMongoClient().getDatabase(manager.getDatabaseName());
			} else {
				db = mongoClient.getDatabase(manager.getDatabaseName());
			}
		} catch (Exception e) {
			throw ExceptionHelper.convert(e);
		}
		return db;
	}
}
