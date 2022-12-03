/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongodbFactory {
	private static MongoDatabase mongoDatabase;
	private static String host;
	private static int port;
	private static String dbname;

	static {
		host = "localhost";
		port = 27017;
		dbname = "bee";
	}

	public static com.mongodb.client.MongoClient getMongoClient() {
		String addr = "mongodb://" + host + ":" + port;
		return MongoClients.create(addr);
	}

	public static MongoDatabase getDb() {
		MongoClient mongoClient = getMongoClient();
		mongoDatabase = mongoClient.getDatabase(dbname);
		return mongoDatabase;
	}

	public static MongoCollection<Document> getCollection(String name) {
		mongoDatabase = MongodbFactory.getDb();
		MongoCollection<Document> collection = mongoDatabase.getCollection(name);
		return collection;
	}

}
