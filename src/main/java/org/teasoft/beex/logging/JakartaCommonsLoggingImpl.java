/*
 * Copyright 2016-2020 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.logging;

/**
 * @author Kingstar
 * @since  1.8
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JakartaCommonsLoggingImpl implements org.teasoft.bee.logging.Log {

	private Log log;

	public JakartaCommonsLoggingImpl(String loggerName) {
		log = LogFactory.getLog(loggerName);
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
