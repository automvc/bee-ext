/*
 * Copyright 2016-2020 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.logging;

import org.slf4j.Logger;
import org.teasoft.bee.logging.Log;

/**
 * @author Kingstar
 * @since  1.8
 */
class Slf4jLoggerImpl implements Log {

	private final Logger log;

	public Slf4jLoggerImpl(Logger logger) {
		log = logger;
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
	public void trace(String msg) {
		log.trace(msg);
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
	public void warn(String msg, Throwable t) {
		log.warn(msg, t);
	}

	@Override
	public void debug(String msg, Throwable t) {
		log.debug(msg, t);
	}

	@Override
	public void error(String msg, Throwable t) {
		log.error(msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}
}
