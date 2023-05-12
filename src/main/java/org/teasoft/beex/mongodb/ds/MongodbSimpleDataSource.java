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

import java.io.IOException;

import org.teasoft.honey.database.ClientDataSource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Simple DataSource for Mongodb.
 * @author Kingstar
 * @since  2.0
 */
public class MongodbSimpleDataSource extends ClientDataSource {

	private MongodbManager mongodbManager = null;

	private String url;
	private String username;
	private String password;

	private boolean inited = false;
	
	public MongodbSimpleDataSource() {}

	public MongodbSimpleDataSource(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;

		init();
	}

	public String getUrl() {
		return url;
	}

	public void init() {
		if (inited) return;
		mongodbManager = new MongodbManager(getUrl(), getUsername(), getPassword());
		inited = true;
	}
	
	@Override
	public Object getDbConnection() {
		return getMongoDb();
	}
	
	private MongoDatabase getMongoDb() {
		if (Boolean.TRUE.equals(MongoContext.getCurrentBeginFirst())) {// tran 首次
			return _getMongoDb();
		} else if (MongoContext.getCurrentClientSession() != null) { // 同一tran，非首次获取
			return MongoContext.getCurrentMongoClient().getDatabase(mongodbManager.getDatabaseName());
		} else {
			return _getMongoDb();
		}
	}

	private MongoDatabase _getMongoDb() {
		MongoClient client = mongodbManager.getMongoClient();
		MongoContext.setCurrentMongoClient(client);
		return client.getDatabase(mongodbManager.getDatabaseName());
	}

	@Override
	public void close() throws IOException {
		if(! MongoContext.inTransaction()) MongoContext.removeMongoClient();
	}
	
	
	// get,set
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
