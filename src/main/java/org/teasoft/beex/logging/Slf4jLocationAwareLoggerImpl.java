/*
 * Copyright 2016-2020 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.teasoft.bee.logging.Log;

/**
 * @author Kingstar
 * @since  1.8
 */
class Slf4jLocationAwareLoggerImpl implements Log {

	private static final Marker MARKER = MarkerFactory.getMarker("Bee");

	private String callerFQCN;

	private final LocationAwareLogger logger;

	Slf4jLocationAwareLoggerImpl(LocationAwareLogger logger, boolean isNoArg) {
		if (isNoArg) {
			callerFQCN = org.teasoft.honey.osql.core.Logger.class.getName();
		} else {
			callerFQCN = Slf4jImpl.class.getName();
		}
		this.logger = logger;
	}

	@Override
	public void error(String msg) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, null);
	}

	@Override
	public void debug(String msg) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.DEBUG_INT, msg, null, null);
	}

	@Override
	public void trace(String msg) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.TRACE_INT, msg, null, null);
	}

	@Override
	public void warn(String msg) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, null);
	}

	@Override
	public void info(String msg) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.INFO_INT, msg, null, null);
	}

	@Override
	public void debug(String msg, Throwable t) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.DEBUG_INT, msg, null, t);
	}

	@Override
	public void warn(String msg, Throwable t) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.WARN_INT, msg, null, t);
	}

	@Override
	public void error(String msg, Throwable t) {
		logger.log(MARKER, callerFQCN, LocationAwareLogger.ERROR_INT, msg, null, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
}
