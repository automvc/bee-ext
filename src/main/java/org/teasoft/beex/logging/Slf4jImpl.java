/*
 * Copyright 2016-2020 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

import org.teasoft.bee.logging.Log;

/**
 * @author Kingstar
 * @since  1.8
 */
public class Slf4jImpl implements Log {

	private Log log;

	public Slf4jImpl() {

		String callerFQCN = org.teasoft.honey.osql.core.Logger.class.getName();
		Logger logger = LoggerFactory.getLogger(callerFQCN);

		if (logger instanceof LocationAwareLogger) {
			try {
				logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
				log = new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger, true);
				return;
			} catch (SecurityException | NoSuchMethodException e) {
			}
		}

		log = new Slf4jLoggerImpl(logger);
	}

	public Slf4jImpl(String clazz) {
		Logger logger = LoggerFactory.getLogger(clazz);

		if (logger instanceof LocationAwareLogger) {
			try {
				// check for slf4j version >= 1.6
				logger.getClass().getMethod("log", Marker.class, String.class, int.class, String.class, Object[].class, Throwable.class);
				log = new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger, false);
				return;
			} catch (SecurityException | NoSuchMethodException e) {
			}
		}

		// Logger is not LocationAwareLogger or slf4j version < 1.6
		log = new Slf4jLoggerImpl(logger);
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
	public void info(String msg) {
		log.info(msg);
	}

	@Override
	public void warn(String msg) {
		log.warn(msg);
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
