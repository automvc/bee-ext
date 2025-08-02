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

package org.teasoft.beex.ds;

import java.util.Map;

import javax.sql.DataSource;

import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.bee.osql.exception.ConfigWrongException;
import org.teasoft.beex.mongodb.ds.MongodbSimpleDataSource;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.util.StringUtils;

/**
 * @author Kingstar
 * @since  2.1
 */
public class BeeMongodbSimpleDataSourceBuilder implements DataSourceBuilder {
	
	@Override
	public DataSource build(Map<String, String> properties) {

		DataSource ds = null;
		try {
			String url = properties.get("url");
			if (StringUtils.isBlank(url)) {
				throw new ConfigWrongException("The url for Mongodb can not be null!");
			}

			String username = properties.getOrDefault("username", "");
			String p = properties.getOrDefault("pass"+"word", "");

			url = processOptionKeys(properties, url);
			
			ds = new MongodbSimpleDataSource(url, username, p);
			
			Logger.info("[Bee] Using BeeMongodbSimpleDataSourceBuilder...");
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}
	
	private String processOptionKeys(Map<String, String> map, String url) {
		StringBuffer n = new StringBuffer();
		boolean has=false;
		//不检测字段名称，交驱动处理
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key=entry.getKey();
			if(isIgnore(key)) continue;
			String v = entry.getValue();
			if (StringUtils.isNotBlank(v)) {
				if(has) n.append("&");
				if(!has) has=true;
				n.append(key);
				n.append("=");
				n.append(v.trim());
			}
		}
		
		if (n.length() > 1) {
			url = url.trim();
			if (url.endsWith("/"))
				url = url.substring(0, url.length() - 1);
			int i = url.indexOf('?');
			if (i == -1)
				n.insert(0,'?');
			else
				n.insert(0,'&');
			url += n.toString();
		}
		return url;
	}
	
	private boolean isIgnore(String key) {
		return "url".equalsIgnoreCase(key) || "username".equalsIgnoreCase(key) || "password".equalsIgnoreCase(key);
	}
	
//	public static void main(String[] args) {
//		Map<String, String> map=new HashMap<>();
//		
//		map.put("url", "mongodb://localhost:27017/db0?tls=false");
//		map.put("username", "aaa");
//		map.put("password", "test123456");
//		map.put("authMechanism", "MONGODB-AWS");
////		map.put("tls", "false");
//		
//		BeeMongodbSimpleDataSourceBuilder b=new BeeMongodbSimpleDataSourceBuilder();
//		b.build(map);
//	}
}
