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

import org.teasoft.bee.osql.BeeSQLException;
import org.teasoft.beex.transaction.EmptyTransaction;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyContext;

import ohos.data.rdb.RdbStore;

/**
 * 用于HarmonyOS SQLite的事务.
 * @author Kingstar
 * @since  1.17
 */
public class SQLiteTransaction extends EmptyTransaction {
	
	private RdbStore db;
	private boolean isBegin = false;

	@Override
	public void begin() {
		super.MSG = "method in SQLiteTransaction(harmony).";
		Logger.info("[Bee] SQLiteTransaction(harmony) begin. ");
		
		db=	BeeDatabaseHelper.getRdbStore();	
		db.beginTransaction();
		isBegin = true;
		HoneyContext.setCurrentAppDB(db);
	}

	@Override
	public void commit() {
		Logger.info("[Bee] SQLiteTransaction(harmony) commit. ");
		if (!isBegin) throw new BeeSQLException("The SQLiteTransaction did not to begin!");
		try {
			db.markAsCommit();
			db.endTransaction();
		} catch (BeeSQLException e) {
			throw ExceptionHelper.convert(e);
		} finally {
			_close();
			isBegin = false;
		}
	}

	@Override
	public void rollback() {
		Logger.info("[Bee] SQLiteTransaction(harmony) rollback. ");
		try {
			db.endTransaction();
		} finally {
			_close();
			isBegin = false;
		}
	}
	
	private void _close() {
		if (db != null) {
			try {
				db.close();
			} catch (BeeSQLException e) {
				throw ExceptionHelper.convert(e);
			} finally {
				HoneyContext.removeCurrentAppDB(); //事务结束时要删除上下文
			}
		}
	}

}
