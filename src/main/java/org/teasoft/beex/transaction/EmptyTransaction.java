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

package org.teasoft.beex.transaction;

import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.bee.osql.transaction.TransactionIsolationLevel;
import org.teasoft.honey.logging.Logger;

/**
 * Empty abstract transaction class.
 * @author Kingstar
 * @since  2.1
 */
public abstract class EmptyTransaction implements Transaction{
	
	protected String MSG="method !";
	
//	@Override
//	public void begin() {
//		
//	}
//
//	@Override
//	public void commit() {
//		
//	}
//
//	@Override
//	public void rollback() {
//		
//	}

	//will ignore following methods
	@Override
	public int getTransactionIsolation() {
		//will ignore this method
		Logger.debug("No need getTransactionIsolation() "+MSG);
		return 4;
	}

	@Override
	public boolean isReadOnly() {
		//will ignore this method
		Logger.debug("No need isReadOnly() "+MSG);
		return false;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		//will ignore this method
		Logger.debug("No need readOnly() "+MSG);
	}

	@Override
	public void setTimeout(int seconds) {
		Logger.debug("Donot support setTimeout(int seconds) "+MSG);
	}

	@Override
	public void setTransactionIsolation(TransactionIsolationLevel level) {
		//will ignore this method
		Logger.debug("No need setTransactionIsolation(TransactionIsolationLevel level) "+MSG);
	}
	
}
