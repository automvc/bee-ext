/*
 * Copyright 2020-2022 the original author.All rights reserved.
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

package org.teasoft.beex.harmony;

import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.Logger;

import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.rdb.RdbOpenCallback;
import ohos.data.rdb.RdbStore;
import ohos.data.rdb.StoreConfig;

/**
 * Bee use DatabaseHelper 
 * @author Kingstar
 * @since  1.17
 */
public class BeeDatabaseHelper {

	private static String DBNAME;
	private static int VERSION;

	private static Context context = null;
//	private static BeeDatabaseHelper helper = null;
	private static StoreConfig config = null;
	private static RdbOpenCallback rdbOpenCallback = null;
	private static RdbStore rdbStore = null;
	private static boolean isInit=true;
	
	static {
		Context temp = ContextRegistry.getContext();
		if (temp != null) {
			context = temp;
//			context.getApplicationContext();
			DBNAME=HoneyConfig.getHoneyConfig().harmonyDbName;
			VERSION=HoneyConfig.getHoneyConfig().harmonyDbVersion;
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			config = StoreConfig.newDefaultConfig(DBNAME);
			rdbOpenCallback = RdbOpenCallbackRegistry.getRdbOpenCallback();
			try {
			rdbStore = databaseHelper.getRdbStore(config, VERSION, rdbOpenCallback, null);
			isInit=false;
			} catch (Exception e) {
				Logger.info("---------------获取DB对象失败");
				Logger.error(e.getMessage(), e);
			}
		}
	}

	private BeeDatabaseHelper() {}

//	
	public BeeDatabaseHelper(Context context) {}

	public static RdbStore getRdbStore() {
		RdbStore tempDb=rdbStore;
		if(! isInit && (tempDb==null || ! tempDb.isOpen()) ) {//非首次, 要是中途关了,可以重新获取,但callback置为null
			DBNAME=HoneyConfig.getHoneyConfig().harmonyDbName;
			VERSION=HoneyConfig.getHoneyConfig().harmonyDbVersion;
			DatabaseHelper databaseHelper = new DatabaseHelper(context);
			config = StoreConfig.newDefaultConfig(DBNAME);
			rdbStore = databaseHelper.getRdbStore(config, VERSION, null, null); //callback=null
		}
		return rdbStore;
	}

}
