/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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
