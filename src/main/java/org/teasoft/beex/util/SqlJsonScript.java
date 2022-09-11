/*
 * Copyright 2016-2022 the original author.All rights reserved.
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

import java.io.IOException;
import java.util.List;

import org.teasoft.honey.osql.autogen.ColumnBean;
import org.teasoft.honey.osql.autogen.ColumnUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json格式的Sql脚本.Sql Json Script.
 * @author Kingstar
 * @since  1.9.8
 */
public class SqlJsonScript {

	private SqlJsonScript() {}

	public static String tableJsonScript(String tableName) throws IOException {

		List<ColumnBean> list = ColumnUtil.getColumnList(tableName);

		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(list);
	}

}
