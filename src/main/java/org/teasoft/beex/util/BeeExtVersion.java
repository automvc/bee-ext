/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.util;

import org.teasoft.honey.osql.core.Logger;

/**
 * BeeExt Version
 * @author Kingstar
 * @since  1.11
 */
public final class BeeExtVersion {
	
	private BeeExtVersion() {}

	public static final String version = "V1.17";
	public static final String buildId = "V1.17.0.10";
	
	static {
		Logger.info("[Bee] ========= BeeExt Version is: "+version);
		Logger.debug("[Bee] ========= BeeExt buildId is: "+buildId);
	}

}
