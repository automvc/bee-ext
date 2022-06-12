/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.android;

import org.teasoft.bee.osql.Registry;

import android.app.Application;

/**
 * 使用ApplicationRegistry设置Application,以便将Context传递给Bee.
 * @author Kingstar
 * @since 1.17
 */
public class ApplicationRegistry implements Registry {
	private static Application app = null;

	private ApplicationRegistry() {}

	public static void register(Application application) {
		app = application;
	}

	public static Application getApplication() {
		return app;
	}
}
