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
