/*
 * Copyright 2016-2020 the original author.All rights reserved.
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

package org.teasoft.beex.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.teasoft.bee.logging.Log;

/**
 * @author Kingstar
 * @since  1.8
 */
public class Log4jImpl implements Log {

	private String callerFQCN;

	private Logger log;

	public Log4jImpl() {
		callerFQCN = org.teasoft.honey.osql.core.Logger.class.getName();
		log = Logger.getLogger(callerFQCN);
	}

	public Log4jImpl(String loggerName) {
		callerFQCN = Log4jImpl.class.getName();
		log = Logger.getLogger(loggerName);
	}

	public Logger getLog() {
		return log;
	}

	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	public void error(String msg, Throwable e) {
		log.log(callerFQCN, Level.ERROR, msg, e);
	}

	public void error(String msg) {
		log.log(callerFQCN, Level.ERROR, msg, null);
	}

	public void debug(String msg) {
		log.log(callerFQCN, Level.DEBUG, msg, null);
	}

	public void debug(String msg, Throwable e) {
		log.log(callerFQCN, Level.DEBUG, msg, e);
	}

	public void warn(String msg) {
		log.log(callerFQCN, Level.WARN, msg, null);
	}

	public void warn(String msg, Throwable e) {
		log.log(callerFQCN, Level.WARN, msg, e);
	}

	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	public void info(String msg) {
		log.log(callerFQCN, Level.INFO, msg, null);
	}

	public boolean isWarnEnabled() {
		return log.isEnabledFor(Level.WARN);
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public void trace(String msg) {
		log.log(callerFQCN, Level.TRACE, msg, null);
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isEnabledFor(Level.ERROR);
	}
}
