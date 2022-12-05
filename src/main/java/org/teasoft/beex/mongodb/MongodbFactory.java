///*
// * Copyright 2016-2023 the original author.All rights reserved.
// * Kingstar(honeysoft@126.com)
// * The license,see the LICENSE file.
// */
// 
//package org.teasoft.beex.mongodb;
//
//import org.bson.Document;
//import org.teasoft.honey.osql.core.HoneyConfig;
//import org.teasoft.honey.util.StringUtils;
//
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//public class MongodbFactory {
//	private static MongoDatabase mongoDatabase;
//	private static String url;
//	private static String username;
//	private static String password;
//
//	static {
//		initConfig();
//	}
//
//	private static void initConfig() {
//		HoneyConfig config = HoneyConfig.getHoneyConfig();
//		url = config.getUrl();
//		username = config.getUsername();
//		password = config.getPassword();
//		if (url != null) url = url.trim();
//	}
//
//	public static MongoCollection<Document> getCollection(String name) {
//		mongoDatabase = getMongoDb("");
//		MongoCollection<Document> collection = mongoDatabase.getCollection(name);
//		return collection;
//	}
//
//	public static MongoClient getMongoClient(String uri) {
//		MongoClient mongoClient = MongoClients.create(uri);
//		return mongoClient;
//	}
//
//	public static MongoDatabase getMongoDb(String uri) {
////      uri = "mongodb://localhost:27017";
//		String databaseName = "";
//		if (StringUtils.isBlank(uri)) {
//			if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
//			int index = url.lastIndexOf('/');
//			uri = url.substring(0, index);
//			databaseName = url.substring(index + 1);
//		}
//
//		MongoClient mongoClient = getMongoClient(uri);
//		MongoDatabase database = null;
//		database = mongoClient.getDatabase(databaseName);
//		return database;
//	}
//
//}
