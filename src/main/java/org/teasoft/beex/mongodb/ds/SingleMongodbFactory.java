/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.ds;

import org.teasoft.honey.osql.core.HoneyConfig;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class SingleMongodbFactory {

	private static MongodbManager manager = null;
	private static MongoClient mongoClient = null;

	static {
		HoneyConfig config = HoneyConfig.getHoneyConfig();
		manager = new MongodbManager(config.getUrl(), config.getUsername(),
				config.getPassword());

		mongoClient = manager.getMongoClient();
	}

	public static MongoDatabase getMongoDb() {
//		mongoClient.getDatabase(manager.getDatabaseName());
		return mongoClient.getDatabase(manager.getDatabaseName());
//		getMongoClient(); //可以一下生成多个 
//		System.err.println("--------getMongoDb----------");
//		return getMongoClient().getDatabase(manager.getDatabaseName());
	}

}