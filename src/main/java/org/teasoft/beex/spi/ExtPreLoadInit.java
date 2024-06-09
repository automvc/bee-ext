/*
 * Copyright 2016-2024 the original author.All rights reserved.
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

package org.teasoft.beex.spi;

import org.teasoft.bee.spi.PreLoad;
import org.teasoft.beex.type.JsonDefaultHandler;
import org.teasoft.honey.osql.core.Logger;

/**
 * Bee-Ext提前预加载初始化
 * @author Kingstar
 * @since  2.0
 */
public class ExtPreLoadInit implements PreLoad{
	
	static {
		Logger.info("[Bee] ========= Preload class ExtPreLoadInit, load...");
		init();
	}
	
	private static void init() {
		JsonDefaultHandler.init();
//		MongodbBeeSqlRegister.register(new MongodbSqlLib()); 
//		MongodbCommRegister.register(new MongodbCommImpl());
	}
}
