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

/**
 * 兼容JakartaCommonsLogging的实现类.Implements class compatible for JakartaCommonsLogging.
 * @author Kingstar
 * @since  1.8
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JakartaCommonsLoggingImpl implements org.teasoft.bee.logging.Log {

	private Log log;

	public JakartaCommonsLoggingImpl(String name) {
		log = LogFactory.getLog(name);
	}

	@Override
	public void error(String msg) {
		log.error(msg);
	}

	@Override
	public void debug(String msg) {
		log.debug(msg);
	}

	@Override
	public void warn(String msg) {
		log.warn(msg);
	}

	@Override
	public void info(String msg) {
		log.info(msg);
	}
	
	@Override
	public void trace(String msg) {
		log.trace(msg);
	}
	
	@Override
	public void error(String msg, Throwable e) {
		log.error(msg, e);
	}
	
	@Override
	public void debug(String msg, Throwable e) {
		log.debug(msg, e);
	}

	@Override
	public void warn(String msg, Throwable e) {
		log.warn(msg, e);
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}
	
	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
	
	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

}
