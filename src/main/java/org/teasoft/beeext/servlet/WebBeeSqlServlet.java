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

package org.teasoft.beeext.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.teasoft.beeext.sqlexecuter.WebSqlService;

/**
 * @author Kingstar
 * @since  1.8
 */
public class WebBeeSqlServlet extends HttpServlet {

	private static final long serialVersionUID = -7023954482343289603L;
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contextPath = request.getContextPath(); //  /bee-ext
		String servletPath = request.getServletPath(); //  /show
		String requestURI = request.getRequestURI(); //  /bee-ext/show/index.html

		response.setCharacterEncoding("utf-8");

		if (contextPath == null) { // root context
			contextPath = "";
		}
		String uri = contextPath + servletPath;
		String fileOrPathName = requestURI.substring(contextPath.length() + servletPath.length());

		if (requestURI.endsWith("/select")) {

//			String sqlStr = request.getParameter("sqlStr");
			String sqlStr =new String(request.getParameter("sqlStr").getBytes("ISO-8859-1"),"UTF-8");
			
			sqlStr=WebSqlService.filterSql(sqlStr);

			System.err.println(sqlStr);
			String pageStr = request.getParameter("page");
			String rowsStr = request.getParameter("rows");
			int page, rows;

			if (pageStr == null)
				page = 1;
			else
				page = Integer.parseInt(pageStr);

			if (rowsStr == null)
				rows = 10;
			else
				rows = Integer.parseInt(rowsStr);

			String json = WebSqlService.select(sqlStr, page, rows);
			response.getWriter().write(json); //获取json
			return;
		}

		returnResourceFile(fileOrPathName, uri, response);
	}

	protected void returnResourceFile(String fileOrPathName, String uri, HttpServletResponse response) throws ServletException, IOException {

		String filePath = "beeext/html" + fileOrPathName;

		if (filePath.endsWith(".html")) {
			response.setContentType("text/html; charset=utf-8");
		} else if (fileOrPathName.endsWith(".css")) {
			response.setContentType("text/css;charset=utf-8");
		} else if (fileOrPathName.endsWith(".js")) {
			response.setContentType("text/javascript;charset=utf-8");
		}

		String text = readFromResource(filePath);
		if (text == null) {
			response.sendRedirect(uri + "/index.html");
			return;
		}

		if (filePath.endsWith("/index.html")) {
			text = text.replace("url:\"${path}", "url:\"" + uri);
		} else if (filePath.endsWith("/list.html")) {
			text = text.replace("${path}", uri);
		} else if (filePath.endsWith("/list1.html")) {
			text = text.replace("${path}", uri);
		}

		response.getWriter().write(text);
	}

	public static String readFromResource(String resource) throws IOException {
		InputStream in = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
			if (in == null) {
				in = WebBeeSqlServlet.class.getResourceAsStream(resource);
			}

			if (in == null) {
				return null;
			}

			String text = read(in);
			return text;
		} finally {
			try {
				if (in != null) in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String read(InputStream in) {
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(in, "UTF-8");
			StringWriter writer = new StringWriter();
			char[] buffer = new char[64];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}

			return writer.toString();

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IOException ex) {
			throw new IllegalStateException("Read Error", ex);
		}
	}
}