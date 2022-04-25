/*
 * Copyright 2016-2021 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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
