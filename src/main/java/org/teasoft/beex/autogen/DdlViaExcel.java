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

package org.teasoft.beex.autogen;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.teasoft.bee.osql.api.PreparedSql;
import org.teasoft.beex.poi.ExcelReader;
import org.teasoft.honey.osql.autogen.DdlToSql;
import org.teasoft.honey.osql.autogen.Java2DbType;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.HoneyUtil;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.util.StringUtils;

/**
 * 通过excel生成数据库表.
 * 每列数据的顺序,0:column name; 1: type; 2:comment
 * 通过传入sheetName查询Excel的sheet,sheetName={表名}[-表描述],eg:my_users-用户表
 * @author Kingstar
 * @since  1.11
 */
public class DdlViaExcel {

	private static final String CREATE_TABLE = "CREATE TABLE ";
	private static String LINE_SEPARATOR = System.getProperty("line.separator"); // 换行符

	/**
	 * create table via sheet of excel.
	 * @param excelFullPath excel FullPath
	 * @param sheetNames  sheetNames,可以只使用某一个sheet
	 * @param checkTitle  order:  0:column name; 1: type; 2:comment
	 */
	@SuppressWarnings("deprecation")
	public static void createTable(String excelFullPath, String sheetNames[],
			String checkTitle) {
		if (sheetNames == null || StringUtils.isBlank(excelFullPath)) {
			Logger.warn("sheetNames or excelFullPath is null or empty !");
			return;
		}

		int NUM = sheetNames.length;
		try {
			if (StringUtils.isNotBlank(checkTitle)) {
				for (int i = 0; i < NUM; i++) {
					ExcelReader.checkAndReadExcel(excelFullPath, i, checkTitle, 0);
				}
			}
			List<String[]> list =null;
			PreparedSql preparedSql = BeeFactoryHelper.getPreparedSql();
			String create_sql ="";
			for (int i = 0; i < NUM; i++) {
				String tableName = getTableNameBySheetName(sheetNames[i]);
				String tableComment = getTableCommentBySheetName(sheetNames[i]);
				list = ExcelReader.readExcel(excelFullPath, sheetNames[i], 1, -1); // by sheet name, 获取从第1行开始所有的行
				
				// mysql
				if (HoneyUtil.isMysql()) {
					create_sql = toCreateTableSQLForMySQL(list, tableName);
					create_sql = addTableComment(create_sql, tableComment);
				} else {//V2.1.8  其它  不提供添加注释
					create_sql = toCreateTableSQLComm(list, tableName);
				}
				
				boolean old=HoneyConfig.getHoneyConfig().showSql_showExecutableSql;
				if(old) HoneyConfig.getHoneyConfig().showSql_showExecutableSql=false;
				try { //V2.1.7 可以只生成部分表
					preparedSql.modify(create_sql);
				} catch (Exception e) {
					Logger.warn(e.getMessage());
				}
				
				if(old) HoneyConfig.getHoneyConfig().showSql_showExecutableSql=old;
				
			}

		} catch (FileNotFoundException e) {
			Logger.warn(e.getMessage(), e);
		}
	}

	public static String getTableNameBySheetName(String sheetName) {
		int index = sheetName.indexOf('-');
		if (index > 0)
			return sheetName.substring(0, index);
		else
			return sheetName;
	}
	
	public static String getTableCommentBySheetName(String sheetName) {
		int index = sheetName.indexOf('-');
		if (index > 0)
			return sheetName.substring(index+1);
		else
			return "";
	}
	
	public static String addTableComment(String create_sql,String tableComment){
		if(StringUtils.isNotBlank(tableComment)) {
			create_sql+=" COMMENT='"+tableComment+"'";
		}
		return create_sql;
	}
	
	/**
	 * create table via sheet of excel.
	 * @param excelFullPath excel FullPath
	 * @param sheetNames  sheetNames,可以只使用某一个sheet
	 * @param checkTitle  order:  0:column name; 1: type; 2:comment
	 * @param isDropExistTable 是否删除已存在的表
	 */
	@SuppressWarnings("deprecation")
	public static void createTable(String excelFullPath, String sheetNames[], String checkTitle,
			boolean isDropExistTable) {

		if (sheetNames == null || StringUtils.isBlank(excelFullPath)) {
			Logger.warn("sheetNames or excelFullPath is null or empty !");
			return;
		}
		int NUM = sheetNames.length;

		// drop table
		if (isDropExistTable) {
			PreparedSql preparedSql = BeeFactoryHelper.getPreparedSql();
			for (int i = 0; i < NUM; i++) {
				String tableName = getTableNameBySheetName(sheetNames[i]);

				boolean old=HoneyConfig.getHoneyConfig().showSql_showExecutableSql;
				if(old) HoneyConfig.getHoneyConfig().showSql_showExecutableSql=false;
				boolean second = false;
				try {
					String sql0 = "";

					if (HoneyUtil.isOracle() || HoneyUtil.isSqlServer()) {
						sql0 = "DROP TABLE " + tableName;
					} else {
						sql0 = " DROP TABLE IF EXISTS " + tableName;
						second = true;
					}
					preparedSql.modify(sql0);
				} catch (Exception e) {
					if (second) {
						try {
							preparedSql.modify("DROP TABLE " + tableName);
						} catch (Exception e2) {
							Logger.warn(e2.getMessage());
						}
					}
				}
				if(old) HoneyConfig.getHoneyConfig().showSql_showExecutableSql=old;
			} //end for 
		} //end if
		createTable(excelFullPath, sheetNames, checkTitle);
	}

	//MySQL
	/**
	 * 生成创建表语句.Generate Create Table Statement.
	 * @param list element is String[], order: 0:column name; 1: type; 2:comment
	 * @param tableName  table name
	 * @return  Create table SQL string
	 */
	public static String toCreateTableSQLForMySQL(List<String[]> list, String tableName) {

		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append(CREATE_TABLE + tableName + " (").append(LINE_SEPARATOR);

		String col[] = null;
        boolean isFirst=true;		
		for (int i = 0; list != null && i < list.size(); i++) {
			col = list.get(i);
			if(StringUtils.isBlank(col[0])) continue;
			
			if (isFirst) {  //首次不加逗号,当有下一行时,才为上一行加逗号
				isFirst=false;
			}else {
				sqlBuffer.append(",  ");
				sqlBuffer.append(LINE_SEPARATOR);
			}
			
			//0:column name; 1: type; 2:comment
			sqlBuffer.append(col[0]).append("  ");
			if ("id".equalsIgnoreCase(col[0])) {
				String type = col[1];
				if ("int".equals(type)) //V2.1.8
					sqlBuffer.append("int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT");
				else if (StringUtils.isBlank(type)) 
					sqlBuffer.append("varchar(255) PRIMARY KEY NOT NULL");
				else
					sqlBuffer.append("bigint(20) PRIMARY KEY NOT NULL AUTO_INCREMENT");
			} else {
				String type = col[1];
				if (StringUtils.isBlank(type)) {
					type = "varchar(255)";
				}
				sqlBuffer.append(type);
				if ("timestamp".equalsIgnoreCase(type) || "datetime".equalsIgnoreCase(type)) {
					sqlBuffer.append(" DEFAULT CURRENT_TIMESTAMP");
				} else {
					sqlBuffer.append(" DEFAULT NULL");
				}
			}
			
//			COMMENT
			if(col.length>2 && StringUtils.isNotBlank(col[2])) {
				sqlBuffer.append(" COMMENT '");
				sqlBuffer.append(col[2]);
				sqlBuffer.append("'");
			}
			
		}
		sqlBuffer.append(LINE_SEPARATOR);
		sqlBuffer.append(" )");
		return sqlBuffer.toString();
	}
	
	
	/**
	 * 生成创建表语句,但不提供添加注释.Generate a statement to create a table, but do not provide adding comments.
	 * @param list element is String[], order: 0:column name; 1: type; 2:comment
	 * @param tableName  table name
	 * @return  Create table SQL string
	 * @since 2.1.8
	 */
	public static String toCreateTableSQLComm(List<String[]> list, String tableName) {
		
		String databaseName=HoneyConfig.getHoneyConfig().getDbName();
		StringBuilder sqlBuffer = new StringBuilder();
		sqlBuffer.append(CREATE_TABLE + tableName + " (").append(LINE_SEPARATOR);

		String col[] = null;
        boolean isFirst=true;		
		for (int i = 0; list != null && i < list.size(); i++) {
			col = list.get(i);
			if(StringUtils.isBlank(col[0])) continue;
			
			if (isFirst) {  //首次不加逗号,当有下一行时,才为上一行加逗号
				isFirst=false;
			}else {
				sqlBuffer.append(",  ");
				sqlBuffer.append(LINE_SEPARATOR);
			}
			
			//0:column name; 1: type; 2:comment
			sqlBuffer.append(col[0]).append("  ");
			if ("id".equalsIgnoreCase(col[0])) {
				String type = col[1];
				if (!"string".equalsIgnoreCase(type))
					sqlBuffer.append(DdlToSql.getPrimaryKeyStatement(databaseName));// different
				else
					sqlBuffer.append(DdlToSql.getStringPrimaryKeyStatement(databaseName));
			} else {
				String type = col[1];
				
				if (type == null) {
					type = getJava2DbType().get("String");
				}
				sqlBuffer.append(type);
				
				if ("timestamp".equalsIgnoreCase(type) || "datetime".equalsIgnoreCase(type)) {
					sqlBuffer.append(" DEFAULT CURRENT_TIMESTAMP");
				} else {
					sqlBuffer.append(" DEFAULT NULL");
				}
			}
			
//			no COMMENT part 
		}
		sqlBuffer.append(LINE_SEPARATOR);
		sqlBuffer.append(" )");
		return sqlBuffer.toString();
	}
	
	private static Map<String, String> getJava2DbType() {  //可能返回null
		return Java2DbType.getJava2DbType(HoneyContext.getDbDialect());
	}

}
