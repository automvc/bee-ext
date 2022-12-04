/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.teasoft.honey.util.StringUtils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongodbManager {

	private String url;
	private String username;
	private String password;

	private String uri;
	private String databaseName;

	public MongodbManager() {}

	public MongodbManager(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		
		initConfig();
	}

//	{
//		initConfig();
//	}

	private void initConfig() {
//		HoneyConfig config = HoneyConfig.getHoneyConfig();
//		url = config.getUrl();
//		username = config.getUsername();
//		password = config.getPassword();
		if (url != null) url = url.trim();

		if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
		
		int index0=url.lastIndexOf('?');
		int index1 = -1;
		int end;
		if(index0>0) {
			index1=url.substring(0,index0).lastIndexOf('/');
			end=index0;
		}else {
			index1=url.lastIndexOf('/');
			end=url.length();
		}
		databaseName = url.substring(index1 + 1,end);
		
//		uri = url.substring(0, index1);
		StringBuffer s=new StringBuffer(url);
//		s.delete(index1 + 1,end);
		s.delete(index1,end);
		if(StringUtils.isNotBlank(username)) {
			s.insert(10, username+":"+password+"@");
		}
		uri=s.toString();
		
		System.out.println(uri);
	}

	public MongoClient getMongoClient() {
		MongoClient mongoClient = MongoClients.create(uri);
		return mongoClient;
	}

//	public MongoDatabase getMongoDb() {
//		MongoClient mongoClient = getMongoClient();
//		return mongoClient.getDatabase(databaseName);
//	}
//
//	public MongoCollection<Document> getCollection(String name) {
//		MongoDatabase mongoDatabase = getMongoDb();
//		MongoCollection<Document> collection = mongoDatabase.getCollection(name);
//		return collection;
//	}

	// get,set
	public String getUrl() {
		return url;
	}

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

	public String getDatabaseName() {
		return databaseName;
	}
	
	
	public static void main(String[] args) {
//      uri = "mongodb://localhost:27017/bee";
//		url = "mongodb://username:test123456@localhost:27017/bee?tls=false
		MongodbManager m=new MongodbManager("mongodb://localhost:27017/bee","","");
		System.out.println(m.getDatabaseName());
		
		MongodbManager m2=new MongodbManager("mongodb://localhost:27017/beeaa?tls=false","username","test123456");
		System.out.println(m2.getDatabaseName());
	}

}
