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

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.teasoft.bee.ds.DataSourceBuilder;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.util.Converter;

/**
 * @author Kingstar
 * @since  2.1
 */
public class Dbcp2DataSourceBuilder implements DataSourceBuilder {

	@Override
	public DataSource build(Map<String, String> propertiesMap) {

		DataSource ds = null;
		try {
			ds = BasicDataSourceFactory.createDataSource(Converter.map2Prop(propertiesMap));
			Logger.info("[Bee] Using Dbcp2DataSourceBuilder...");
		} catch (Exception e) {
			Logger.debug(e.getMessage(), e);
		}
		return ds;
	}

}

//public class getDataSource {
//    @Bean(name="dataSource")
//    public static DataSource getDataSource(){
//        Properties props = new Properties();
//         属性名不对
//        props.setProperty("driver","org.postgresql.Driver");
//        props.setProperty("url","jdbc:postgresql://127.0.0.1:5432/postgres");
//        props.setProperty("user","postgres");
//        props.setProperty("password ","1");
//        DataSource dataSource = null;
//        try {
//            dataSource = BasicDataSourceFactory.createDataSource(props);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dataSource;
//    }
//}
