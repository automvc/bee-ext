/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.autogen;

import java.io.FileNotFoundException;
import java.util.List;

import org.teasoft.bee.osql.PreparedSql;
import org.teasoft.beex.poi.ExcelReader;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.HoneyConfig;
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
	private static PreparedSql preparedSql = BeeFactoryHelper.getPreparedSql();
	
	/**
	 * 
	 * @param excelFullPath
	 * @param sheetNames
	 * @param checkTitle  order:  0:column name; 1: type; 2:comment
	 */                
	public static void createTable(String excelFullPath, String sheetNames[],
			String checkTitle) {

		//	String sheetName0="stock-库存"; //表名与中文名用"-"分开
		//	String sheetName1="out_stock-出库";
		//	String sheetName2="customs_list-报关清单";
		//	String sheetNames[]= {sheetName0,sheetName1,sheetName2};
		//		String checkTitle="字段名,类型,中文注解,英文注解";

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
			for (int i = 0; i < NUM; i++) {
				String tableName = getTableNameBySheetName(sheetNames[i]);
				String tableComment = getTableCommentBySheetName(sheetNames[i]);
				list = ExcelReader.readExcel(excelFullPath, sheetNames[i], 1, -1); // by sheet name, 获取从第1行开始所有的行
				String create_sql = toCreateTableSQLForMySQL(list, tableName);
				
				create_sql = addTableComment(create_sql, tableComment);
				
				boolean old=HoneyConfig.getHoneyConfig().showSql_showExecutableSql;
				if(old) HoneyConfig.getHoneyConfig().showSql_showExecutableSql=false;
				preparedSql.modify(create_sql);
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
	
	public static void createTable(String excelFullPath, String sheetNames[], String checkTitle,
			boolean isDropExistTable) {

		if (sheetNames == null || StringUtils.isBlank(excelFullPath)) {
			Logger.warn("sheetNames or excelFullPath is null or empty !");
			return;
		}

		int NUM = sheetNames.length;

		// drop table
		if (isDropExistTable) {
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
	 * 
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
//				sqlBuffer.append("  ");
				isFirst=false;
			}else {
				sqlBuffer.append(",  ");
				sqlBuffer.append(LINE_SEPARATOR);
			}
			
			//0:column name; 1: type; 2:comment
			sqlBuffer.append(col[0]).append("  ");
			if ("id".equalsIgnoreCase(col[0])) {
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
			if(StringUtils.isNotBlank(col[2])) {
				sqlBuffer.append(" COMMENT '");
				sqlBuffer.append(col[2]);
				sqlBuffer.append("'");
			}
			
		}
		sqlBuffer.append(LINE_SEPARATOR);
		sqlBuffer.append(" )");
		return sqlBuffer.toString();

	}

}
