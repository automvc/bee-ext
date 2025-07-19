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

package org.teasoft.beex.mongodb;

import org.teasoft.bee.osql.BeeException;
import org.teasoft.bee.osql.BeeSQLException;
import org.teasoft.beex.mongodb.ds.MongoContext;
import org.teasoft.beex.transaction.EmptyTransaction;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.osql.core.ExceptionHelper;

import com.mongodb.client.ClientSession;

/**
 * 用于Mongodb的事务.
 * @author Kingstar
 * @since  2.1
 */
public class MongodbTransaction extends EmptyTransaction {

	private boolean isBegin = false;

	@Override
	public void begin() {
		super.MSG = "method in MongodbTransaction.";
		Logger.info("[Bee] MongodbTransaction begin. ");
		try {
			// 在MongoContext设置
//			clientSession = client.startSession();
//			clientSession.startTransaction();

			MongoContext.setCurrentBeginFirst(true);
			isBegin = true;
		} catch (BeeException e) {
			throw ExceptionHelper.convert(e);
		}
	}

	@Override
	public void commit() {
		Logger.info("[Bee] MongodbTransaction commit. ");
		if (!isBegin) throw new BeeSQLException("The MongodbTransaction did not to begin!");

		try {
			ClientSession clientSession = MongoContext.getCurrentClientSession();
			if (clientSession != null) clientSession.commitTransaction();
		} catch (BeeException e) {
			throw ExceptionHelper.convert(e);
		} finally {
			_close();
			isBegin = false;
		}
	}

	@Override
	public void rollback() {

		Logger.info("[Bee] MongodbTransaction rollback. ");
		try {
			ClientSession clientSession = MongoContext.getCurrentClientSession();
			if (clientSession != null) clientSession.abortTransaction();
		} catch (BeeException e) {
			throw ExceptionHelper.convert(e);
		} finally {
			_close();
			isBegin = false;
		}
	}

	private void _close() {
		ClientSession clientSession = MongoContext.getCurrentClientSession();
		if (clientSession != null) {
			try {
				clientSession.close();
			} catch (BeeSQLException e) {
				throw ExceptionHelper.convert(e);
			} finally {
				MongoContext.removeClientSession();
				MongoContext.removeMongoClient();
			}
		}
	}

}
