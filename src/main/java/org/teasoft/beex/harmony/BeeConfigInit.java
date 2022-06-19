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

package org.teasoft.beex.harmony;

import java.io.InputStream;

import org.teasoft.honey.osql.core.HoneyConfig;

/**
 * Config init for Harmony with Bee.
 * @author Kingstar
 * @since  1.17
 */
public class BeeConfigInit {

	public static void init() {
		// here is confirm, set first
		HoneyConfig.getHoneyConfig().setLoggerType("harmonyLog");
		HoneyConfig.getHoneyConfig().isHarmony = true;

		String folderPath = "assets/entry/resources/rawfile/";
		String filePath = folderPath + "bee.properties";

		try (InputStream inputStream = BeeConfigInit.class.getClassLoader()
				.getResourceAsStream(filePath);) {
			if (inputStream != null) {
//              HoneyConfig.getHoneyConfig().resetBeeProperties(folderPath); //不行
				HoneyConfig.getHoneyConfig().resetBeeProperties(inputStream);
			}
		} catch (Exception e) {

		}
	}
}
