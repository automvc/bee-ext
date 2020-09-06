/*
 * Copyright 2013-2018 the original author.All rights reserved.
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

package org.teasoft.beex.websql.sqlexecuter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.teasoft.bee.osql.PreparedSql;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.Logger;

/**
 * @author Kingstar
 * @since  1.8
 */
public class WebSqlService {

	private static PreparedSql preparedSql = BeeFactory.getHoneyFactory().getPreparedSql();

	public static String select(String sql, int page, int rows) {
		String msg = "";
		try {

			/*		String countSql="select count(*) from ("+sql+") c";
			String count=preparedSql.selectFun(countSql);
			int num=Integer.parseInt(count);
			
			String r;
			if(num>rows){
				Object preValues[]=null;
				r=preparedSql.selectJson(sql,preValues,(page-1)*rows, rows);
			}else{
				r=preparedSql.selectJson(sql);
			}*/
			
			String r;
			Object preValues[] = null;
			if (sql != null) sql = sql.trim();
			if (sql != null && sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
			if (sql != null && sql.contains(" limit "))
				r = preparedSql.selectJson(sql);
			else
				r = preparedSql.selectJson(sql, preValues, (page - 1) * rows, rows);

			String countSql = "select count(*) from (" + sql + ") c";
			String count = "1";
			try {
				count = preparedSql.selectFun(countSql);
			} catch (Exception e) {
				List list = preparedSql.select(sql);
				if (list != null && list.size() > 0) count = list.size() + "";
			}

			Logger.info(r);
			msg = "{\"total\":" + count + ",\"rows\":" + r + "}";

		} catch (Exception e) {
			System.err.println(e.getMessage());
			msg = "{\"error\":" + e.getMessage() + "}";
		}
		return msg;
	}

	public static String filterSql(String sqlStr) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sqlStr.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
		String temp = null;
		StringBuffer sb = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			if (temp.trim().startsWith("-- ") || "".equals(temp)) {
				// do nothing
			} else {
				sb.append(temp);
			}
		}
		br.close();
		sqlStr = sb.toString();
		return sqlStr;
	}

}
