/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.logging;

import android.util.Log;

/**
 * @author Kingstar
 * @since 1.17
 */
public class AndroidLog implements org.teasoft.bee.logging.Log {

//	private static String tag = "Bee-AndroidLog";
	private static final String DEBUG = "AndroidLog(DEBUG)";
	private static final String INFO  = "AndroidLog(INFO)";
	private static final String WARN  = "AndroidLog(WARN)";
	private static final String ERROR = "AndroidLog(ERROR)";

	@Override
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(String msg) {
		Log.d(DEBUG, msg); // todo
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String msg) {
//		System.out.println("---------------AndroidLog--------debug----");
		Log.d(DEBUG, msg);

	}

	@Override
	public void debug(String msg, Throwable t) {
		Log.d(DEBUG, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String msg) {
//		System.out.println("---------------AndroidLog--------info----");
		Log.i(INFO, msg);

	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String msg) {
//		System.out.println("---------------AndroidLog--------warn----");
		Log.w(WARN, msg);
	}

	@Override
	public void warn(String msg, Throwable t) {
		Log.w(WARN, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(String msg) {
		Log.e(ERROR, msg);
	}

	@Override
	public void error(String msg, Throwable t) {
		Log.e(ERROR, msg, t);
	}

}
