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

package org.teasoft.beex.android;

import org.teasoft.bee.osql.Registry;

import android.database.sqlite.SQLiteDatabase;

/**
 * 将SQLiteDatabase传递给Bee.
 * 若不使用ApplicationRegistry将Application传递给Bee,可使用这个.
 * @author Kingstar
 * @since 1.17
 */
public class BeeSQLiteDatabaseRegistry implements Registry {
	private static SQLiteDatabase instance = null;

	private BeeSQLiteDatabaseRegistry() {}

	public static void register(SQLiteDatabase database) {
		instance = database;
	}

	public static SQLiteDatabase getSQLiteDatabase() {
		return instance;
	}

}
