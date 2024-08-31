/*
 * Copyright 2020-2022 the original author.All rights reserved.
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
		return false;
	}

	@Override
	public void trace(String msg) {
		Log.d(DEBUG, msg); 
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String msg) {
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
		Log.i(INFO, msg);

	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String msg) {
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
