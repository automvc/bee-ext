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

import org.teasoft.bee.ds.DataSourceBuilderFactory;

/**
 * @author Kingstar
 * @since  2.1
 */
public class DataSourceToolRegHandler {
	
	static {
		init();
	}

//	#bee.db.dbs[i].type   value :Hikari,Druid,c3p0,dbcp2,Tomcat,BeeMongo,BeeSimpleDs  default is : Hikari
	public static void init() {
		DataSourceBuilderFactory.register("Hikari", new HikariDataSourceBuilder());
		DataSourceBuilderFactory.register("Druid", new DruidDataSourceBuilder());
		DataSourceBuilderFactory.register("Dbcp2", new Dbcp2DataSourceBuilder());
		DataSourceBuilderFactory.register("C3p0", new C3p0DataSourceBuilder());
		DataSourceBuilderFactory.register("Tomcat", new TomcatDataSourceBuilder());
		DataSourceBuilderFactory.register("BeeSimpleDs", new BeeSimpleDataSourceBuilder()); //V2.1.8
//		DataSourceBuilderFactory.register("BeeSingleThreadDs", new BeeSingleThreadDataSourceBuilder()); //V2.1.8
		
//		DataSourceBuilderFactory.register("BeeMongo", new BeeMongodbSimpleDataSourceBuilder());
	}

}
