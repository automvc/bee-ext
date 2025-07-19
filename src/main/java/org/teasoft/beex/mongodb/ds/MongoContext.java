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

import org.teasoft.honey.logging.Logger;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;

/**
 * Context for Mongodb.
 * @author Kingstar
 * @since  2.0
 * support session
 * @since 2.1
 */
public class MongoContext {

	private static ThreadLocal<MongoClient> currentMongoClient;
	private static ThreadLocal<ClientSession> currentClientSession; // 2.1
	private static ThreadLocal<Boolean> currentBeginFirst;// 2.1

	static {
		currentMongoClient = new InheritableThreadLocal<>();
		currentClientSession = new InheritableThreadLocal<>();
		if (currentBeginFirst != null) currentBeginFirst.remove();
		currentBeginFirst = new InheritableThreadLocal<>();
	}
	
	private MongoContext() {}

	public static MongoClient getCurrentMongoClient() {
		return currentMongoClient.get();
	}

	public static void setCurrentMongoClient(MongoClient client) {
		currentMongoClient.set(client);
	}

	public static void removeMongoClient() {
		currentMongoClient.remove();
	}

	public static ClientSession getCurrentClientSession() {

		initSession();

		return currentClientSession.get();
	}

	private static void initSession() {
		if (Boolean.TRUE.equals(currentBeginFirst.get())) {
			setCurrentBeginFirst(false);
			MongoClient client = getCurrentMongoClient();
			if (client == null) {
				Logger.warn("Need set CurrentMongoClient first before getCurrentClientSession!");
			} else {
				ClientSession clientSession = getCurrentMongoClient().startSession();
				clientSession.startTransaction();
				setCurrentClientSession(clientSession);
			}
		}
	}

	public static void setCurrentClientSession(ClientSession clientSession) {
		currentClientSession.set(clientSession);
	}

	public static void removeClientSession() {
		currentClientSession.remove();
	}

	public static void setCurrentBeginFirst(Boolean flag) {
		currentBeginFirst.set(flag);
	}

	public static Boolean getCurrentBeginFirst() {
		return currentBeginFirst.get();
	}
	
	public static boolean inTransaction() {
		return (Boolean.TRUE.equals(currentBeginFirst.get()) || getCurrentClientSession()!=null);
	}
}
