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

package org.teasoft.beex.util;

import org.teasoft.bee.osql.BeeVersion;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.util.HoneyVersion;

/**
 * BeeExt Version
 * @author Kingstar
 * @since  1.11
 */
public final class BeeExtVersion {
	
	private BeeExtVersion() {}

	public static final String version = "1.17.21";
	public static final String buildId = version + ".6";
	
	static {
		printVersion();
	}
	
	private static void printVersion() {
		Logger.info("[Bee] -------- BeeExt "+version+" -------- ");
		
		Logger.debug("[Bee] ========= Bee    buildId  " + BeeVersion.buildId);
		Logger.debug("[Bee] ========= Honey  buildId  " + HoneyVersion.buildId);
		Logger.debug("[Bee] ========= BeeExt buildId  " + buildId);
	}

}
