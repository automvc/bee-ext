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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Kingstar
 * @since  1.9.8
 */
public class SqlJsonScript {

	public String tableJsonScript(String tableName) throws JsonProcessingException, IOException {

		List<ColumnBean> list = ColumnUtil.getColumnList(tableName);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(list);
		return json;
	}

}
