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

	private void initConfig() {

		if (StringUtils.isBlank(url)) {
			return;
		} else {
			url = url.trim();
			if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

			int index0 = -1;

			int index1 = -1;
			int end;
			int newIndex0=url.lastIndexOf("//");
			
//			.println(url.substring(newIndex0+2,url.length()));
			String url2=url.substring(newIndex0+2,url.length());
			index0 = url2.lastIndexOf('?');
			
			if (index0 > 0) {
				index1 = url2.substring(0, index0).lastIndexOf('/');
				end = index0;
			} else {
				index1 = url2.lastIndexOf('/');
//				System.out.println(index1);
				end = url2.length();
			}
			if (index1 == -1) {
				
				databaseName = "";
			}else {
				databaseName = url2.substring(index1 + 1, end);
			}

//		    uri = url.substring(0, index1);
			StringBuffer s = new StringBuffer(url);
//			s.delete(index1 + 1, end); //databaseName?  不能少,连接还是要带数据库名
			if (StringUtils.isNotBlank(username)) {
				s.insert(10, username + ":" + password + "@");
			}
			if (s.charAt(s.length() - 1) == '/') s.delete(s.length() - 1, s.length());

			uri = s.toString();
		}

	}

	public MongoClient getMongoClient() {
		MongoClient mongoClient = MongoClients.create(uri);
		return mongoClient;
	}


	
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
//		System.out.println("databaseName: "+databaseName);
		return databaseName;
	}
	
/*	public static void main(String[] args) {
//      uri = "mongodb://localhost:27017/bee";
//		url = "mongodb://username:test123456@localhost:27017/bee?tls=false
		MongodbManager m=new MongodbManager("mongodb://localhost:27017/","","");
		System.out.println(m.getDatabaseName());
		System.out.println(m.uri);
		
//		MongodbManager3 m3=new MongodbManager3("mongodb://127.0.0.1:28011/testa,127.0.0.1:28012/testa,127.0.0.1:28013/testa","","");
//		System.out.println(m3.getDatabaseName());
//		
		MongodbManager m2=new MongodbManager("mongodb://localhost:27017/beeaa?tls=false","username","test123456");
		System.out.println(m2.getDatabaseName());
		System.out.println(m2.uri);
		
		m2=new MongodbManager("mongodb://localhost:27017/beeaa","username","test123456");
		System.out.println(m2.getDatabaseName());
		System.out.println(m2.uri);
	}*/
	
}
