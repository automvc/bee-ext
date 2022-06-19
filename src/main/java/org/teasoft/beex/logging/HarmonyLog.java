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

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * @author Kingstar
 * @since  1.17
 */
public class HarmonyLog implements org.teasoft.bee.logging.Log {
	private static final String TAG = "HarmonyLog";
	private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00003, TAG);

	@Override
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(String msg) {
		HiLog.debug(LABEL_LOG, msg);
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug(String msg) {
		HiLog.debug(LABEL_LOG, msg);
	}

	@Override
	public void debug(String msg, Throwable t) {
		HiLog.debug(LABEL_LOG, msg);
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info(String msg) {
		HiLog.info(LABEL_LOG, msg);
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn(String msg) {
		HiLog.warn(LABEL_LOG, msg);
	}

	@Override
	public void warn(String msg, Throwable t) {
		HiLog.warn(LABEL_LOG, msg);
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error(String msg) {
		HiLog.error(LABEL_LOG, msg);
	}

	@Override
	public void error(String msg, Throwable t) {
		HiLog.error(LABEL_LOG, msg);
	}

}
