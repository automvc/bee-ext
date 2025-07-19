/*
 * Copyright 2016-2022 the original author.All rights reserved.
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

package org.teasoft.beex.android;

import org.teasoft.bee.android.CreateAndUpgrade;
import org.teasoft.bee.android.CreateAndUpgradeRegistry;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyContext;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Bee用于管理SQLiteOpenHelper的一个帮助类.
 * @author Kingstar
 * @since 1.17
 */
public class BeeSQLiteOpenHelper extends SQLiteOpenHelper {

	private static int VERSION;
	private static String DBNAME;
	private static Context context;
	private static BeeSQLiteOpenHelper dbHelper = null;
	static {

		Application app = ApplicationRegistry.getApplication();
		if (app != null) {
			// 默认在bee.properties配置
			DBNAME = HoneyConfig.getHoneyConfig().androidDbName; // 数据库名
			VERSION = HoneyConfig.getHoneyConfig().androidDbVersion; // 数据库版本号
			context = app.getApplicationContext();
			dbHelper = new BeeSQLiteOpenHelper();
			Logger.info("[Bee] ==========Create BeeSQLiteOpenHelper in static block...");
		}
	}

	private BeeSQLiteOpenHelper() {// 定义构造函数 //要传入上下文
		super(context, DBNAME, null, VERSION);
	}

	public BeeSQLiteOpenHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public BeeSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * ApplicationRegistry设置了Application时,才可以使用静态获取
	 * 
	 * @return SQLiteDatabase
	 */
	public static SQLiteDatabase getWritableDB() {
		if (dbHelper == null) return null;
		return dbHelper.getWritableDatabase();
	}

	/**
	 * Create and/or open a database. This will be the same object returned by {@link #getWritableDatabase} unless some problem,
	 * such as a full disk, requires the database to be opened read-only. In that case, a read-only database object will be
	 * returned. If the problem is fixed, a future call to {@link #getWritableDatabase} may succeed, in which case the read-only
	 * database object will be closed and the read/write object will be returned in the future.
	 * ApplicationRegistry设置了Application时,才可以使用静态获取
	 * 
	 * @return SQLiteDatabase
	 */
	public static SQLiteDatabase getReadableDB() {
		if (dbHelper == null) return null;
		return dbHelper.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {// 创建数据库,在安装app时执行; 升级版本也不执行
		// 添加创建数据库业务逻辑
		try {
			CreateAndUpgrade instance = (CreateAndUpgrade) CreateAndUpgradeRegistry
					.getCreateAndUpgrade().newInstance();
			if (instance != null) {
				HoneyContext.setCurrentAppDB(db); // put it in context first , prevent:getDatabase called recursively
				instance.onCreate();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		} finally {
			HoneyContext.removeCurrentAppDB();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)// 数据库版本升级时运行. (降版本会报异常)
	{
		// 添加更新数据库业务逻辑
		try {
			CreateAndUpgrade instance = (CreateAndUpgrade) CreateAndUpgradeRegistry
					.getCreateAndUpgrade().newInstance();
			if (instance != null) {
				HoneyContext.setCurrentAppDB(db);// put it in context first , prevent:getDatabase called recursively
				instance.onUpgrade(oldVersion, newVersion);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		} finally {
			HoneyContext.removeCurrentAppDB();
		}
	}
}
