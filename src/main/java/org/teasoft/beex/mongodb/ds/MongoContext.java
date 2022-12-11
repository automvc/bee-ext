/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.ds;

import com.mongodb.client.MongoClient;

/**
 * @author Kingstar
 * @since  2.0
 */
public class MongoContext {

	private static ThreadLocal<MongoClient> currentMongoClient;

	static {
		currentMongoClient = new InheritableThreadLocal<>();
	}

	public static MongoClient getCurrentMongoClient() {
		return currentMongoClient.get();
	}

	public static void setCurrentMongoClient(MongoClient client) {
		currentMongoClient.set(client);
	}

	public static void removeMongoClient() {
		currentMongoClient.remove();
	}
}
