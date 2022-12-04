/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.teasoft.honey.database.ClientDataSource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
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
	public MongoClient getDatabaseClient() {
		System.out.println("----------getDatabaseClient----------");
		return mongodbManager.getMongoClient();
	}
	
	public MongoClient getMongoClient() {
		return mongodbManager.getMongoClient();
	}

	public MongoDatabase getMongoDb() {
		MongoClient client=getMongoClient();
		//从资源池中拿, 或放到上下文or当前线程中管理.   TODO
		return client.getDatabase(mongodbManager.getDatabaseName());
	}

//	public MongoCollection<Document> getCollection(String name) {
//		return getMongoDb().getCollection(name);
//	}

//	@Override
//	public Connection getConnection() throws SQLException {
//		return new MongodbConnection();// 为了兼容JDBC
//	}

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
