/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.android;

import org.teasoft.bee.osql.BeeSQLException;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.bee.osql.transaction.TransactionIsolationLevel;
import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.Logger;

import android.database.sqlite.SQLiteDatabase;

/**
 * 用于Android SQLite的事务.
 * @author Kingstar
 * @since  1.17
 */
public class SQLiteTransaction implements Transaction {
	
	private SQLiteDatabase db;
	private boolean isBegin = false;

	@Override
	public void begin() {
		Logger.info("[Bee] SQLiteTransaction begin. ");
		
		db=	BeeSQLiteOpenHelper.getWritableDB();	
		//将db放入缓存.  TODO
		db.beginTransaction();
		isBegin = true;
		HoneyContext.setCurrentAppDB(db);
	}

	@Override
	public void commit() {
		Logger.info("[Bee] SQLiteTransaction commit. ");
		if (!isBegin) throw new BeeSQLException("The SQLiteTransaction did not to begin!");
		try {
			db.setTransactionSuccessful();
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
		Logger.info("[Bee] SQLiteTransaction rollback. ");
		db.endTransaction();
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
	
	
	
	//will ignore following methods
	
	@Override
	public int getTransactionIsolation() {
		//will ignore this method
		Logger.debug("No need getTransactionIsolation() method in SQLiteTransaction");
		return 4;
	}

	@Override
	public boolean isReadOnly() {
		//will ignore this method
		Logger.debug("No need isReadOnly() method in SQLiteTransaction");
		return false;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		//will ignore this method
		Logger.debug("No need readOnly() method in SQLiteTransaction");
	}

	@Override
	public void setTimeout(int seconds) {
		Logger.debug("Donot support setTimeout(int seconds) in SQLiteTransaction");
	}

	@Override
	public void setTransactionIsolation(TransactionIsolationLevel level) {
		//will ignore this method
		Logger.debug("No need setTransactionIsolation(TransactionIsolationLevel level) method in SQLiteTransaction");
	}
	

}
