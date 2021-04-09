package org.teasoft.beex.poi;
/*
 * Copyright 2016-2021 the original author.All rights reserved.
 * Kingstar(aiteasoft@126.com)
 * The license,see the LICENSE file.
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.util.StringUtils;

/**
 * 读取Excel,支持xls,xlsx.Read the Excel, support xls,xlsx.
 * <br>Excel行号从0开始.the Excel line number start 0.
 * @author Kingstar
 */
public class ExcelReader {

	/**
	 * 返回首个Excel sheet的所有行.Returns all rows of the first Excel sheet.
	 * @param inputStream InputStream of the Excel file 
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(InputStream inputStream) throws Exception {
		Sheet sheet = getSheet(inputStream);
		return getListBySheet(sheet);
	}

	/**
	 * 返回首个Excel sheet的所有行.Returns all rows of the first Excel sheet.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(String fullPath) throws Exception {
		return readExcel(new FileInputStream(fullPath));
	}

	/**
	 * 返回名称为sheetName的Excel sheet的所有行.Returns all rows of Excel sheet with sheetname.
	 * @param inputStream InputStream of the Excel file. 
	 * @param sheetName  sheet name
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(InputStream inputStream, String sheetName) throws Exception {
		Sheet sheet = getSheet(inputStream, sheetName);
		return getListBySheet(sheet);
	}

	/**
	 * 返回名称为sheetName的Excel sheet的所有行.Returns all rows of Excel sheet with sheetname.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param sheetName sheet name
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(String fullPath, String sheetName) throws Exception {
		return readExcel(new FileInputStream(fullPath), sheetName);
	}
	
	/**
	 * 返回首个Excel sheet中从开始行到结束行的记录.
	 * <br>Returns the records from the beginning line to the end line in the first Excel sheet.
	 * @param inputStream InputStream of the Excel file 
	 * @param startRow 开始行(首行为0).start row(0,1,...)
	 * @param endRow 结束行.end row.
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(InputStream inputStream,int startRow, int endRow) throws Exception {
		Sheet sheet = getSheet(inputStream);
		return getListBySheet(sheet,startRow,endRow);
	}
	
	/**
	 * 返回首个Excel sheet中从开始行到结束行的记录.
	 * <br>Returns the records from the beginning line to the end line in the first Excel sheet.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param startRow 开始行(首行为0).start row(0,1,...)
	 * @param endRow 结束行.end row.
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(String fullPath,int startRow, int endRow) throws Exception {
		return readExcel(new FileInputStream(fullPath),startRow,endRow);
	}
	
	/**
	 * 返回首个Excel sheet中从开始行到结束行的记录.
	 * <br>Returns the records from the beginning line to the end line in Excel sheet with sheetname.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param sheetName sheet name
	 * @param startRow 开始行(首行为0).start row(0,1,...)
	 * @param endRow 结束行.end row.
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(String fullPath, String sheetName,int startRow, int endRow) throws Exception {
		return readExcel(new FileInputStream(fullPath), sheetName,startRow,endRow);
	}
	
	/**
     * 返回首个Excel sheet中从开始行到结束行的记录.
     * <br>Returns the records from the beginning line to the end line in Excel sheet with sheetname.
	 * @param inputStream InputStream of the Excel file 
	 * @param sheetName sheet name
	 * @param startRow 开始行(首行为0).start row(0,1,...)
	 * @param endRow 结束行.end row.
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> readExcel(InputStream inputStream, String sheetName,int startRow, int endRow) throws Exception {
		Sheet sheet = getSheet(inputStream, sheetName);
		return getListBySheet(sheet,startRow,endRow);
	}
	
	/**
	 * 检测首行标题行并返回首个sheet的所有记录.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param hopeTitleArray 期望的标题数组.Expected title array.
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> checkAndReadExcel(String fullPath, String hopeTitleArray[]) throws Exception {
		return checkAndReadExcel(fullPath, hopeTitleArray, 0); //默认标题在第0行.
	}
	
	/**
	 * 检测指定标题行并返回首个sheet的所有记录.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param hopeTitles 期望的标题(用逗号隔开).Expected title (separated by commas).
	 * @param titleRow 标题所在行(首行为0). line number of title row(start from 0)
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> checkAndReadExcel(String fullPath, String hopeTitles, int titleRow) throws Exception {
		String hopeTitleArray[] = hopeTitles.split(",");
		return checkAndReadExcel(new FileInputStream(fullPath), hopeTitleArray, titleRow);
	}

	/**
	 * 检测指定标题行并返回首个sheet的所有记录.
	 * @param fullPath 完整的Excel文件路径(包含文件名).Full Excel file path (including file name)
	 * @param hopeTitleArray 期望的标题数组.Expected title array.
	 * @param titleRow 标题所在行(首行为0). line number of title row(start from 0)
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> checkAndReadExcel(String fullPath, String hopeTitleArray[], int titleRow) throws Exception {
		return checkAndReadExcel(new FileInputStream(fullPath), hopeTitleArray, titleRow);
	}
	
	/**
	 * 检测指定标题行并返回首个sheet的所有记录.
	 * @param inputStream InputStream of the Excel file 
	 * @param hopeTitles 期望的标题(用逗号隔开).Expected title (separated by commas).
	 * @param titleRow 标题所在行(首行为0). line number of title row(start from 0)
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> checkAndReadExcel(InputStream inputStream, String hopeTitles, int titleRow) throws Exception {
		String hopeTitleArray[] = hopeTitles.split(",");
		return checkAndReadExcel(inputStream, hopeTitleArray, titleRow);
	}

	/**
	 * 检测指定标题行并返回首个sheet的所有记录.
	 * @param inputStream InputStream of the Excel file 
	 * @param hopeTitleArray 期望的标题数组.Expected title array.
	 * @param titleRow 标题所在行(首行为0). line number of title row(start from 0)
	 * @return 可包含多个String数组结构的多行记录的list. list can contain more than one record with String array struct.
	 * @throws Exception
	 */
	public static List<String[]> checkAndReadExcel(InputStream inputStream, String hopeTitleArray[], int titleRow)
			throws Exception {
		Sheet sheet = getSheet(inputStream);

		List<String[]> list = getListBySheet(sheet, 0, titleRow);
		
		if(list!=null) {
			if(titleRow>(list.size()-1)) {
				Logger.warn("The title line number is greater than the maximum data line number!");
				return null;
			}
		}
		
		String msg = checkTitle(hopeTitleArray, list.get(titleRow));

		if (StringUtils.isNotEmpty(msg)) { //检测标题没通过.
			if (msg.startsWith("Warn:")) {
				Logger.warn(msg);
			} else {
				Logger.warn("Title wrong line: "+msg);
			}

			return null;
		}

		return getListBySheet(sheet);
	}

	private static Sheet getSheet(InputStream inputStream) throws Exception {
		return getSheet(inputStream, 0);
	}

	private static Sheet getSheet(InputStream inputStream, int sheetIndex) throws Exception {
		Workbook workbook;
		Sheet sheet = null;
		workbook = WorkbookFactory.create(inputStream);
		sheet = workbook.getSheetAt(sheetIndex);
		return sheet;
	}

	private static Sheet getSheet(InputStream inputStream, String sheetName) throws Exception {
		Workbook workbook;
		Sheet sheet = null;
		workbook = WorkbookFactory.create(inputStream);
		sheet = workbook.getSheet(sheetName); //根据sheet名称获取
		return sheet;
	}

	private static List<String[]> getListBySheet(Sheet sheet) throws Exception{
		return getListBySheet(sheet, 0, -1);
	}

	/**
	 * 
	 * @param sheet
	 * @param startRow 开始行,从0开始
	 * @param endRow 结束行(包括)
	 * @return
	 */
	private static List<String[]> getListBySheet(Sheet sheet, int startRow, int endRow) throws Exception{
		List<String[]> list = new ArrayList<>();
		int rows = sheet.getLastRowNum(); //最后的行号,不是总行数.     如何判断是无数据的空行???  TODO
//		int rows = sheet.getPhysicalNumberOfRows();
		int columns = 0;
		String[] colStr = null;

		if (endRow < 0) endRow = rows; //最后的行号,不是总行数.
		if(startRow>endRow) {
			throw new Exception("endRow need less than startRow!");
		}
		
		for (int r = startRow; r <= endRow; r++) { // 循环遍历表格的行
			Row row = sheet.getRow(r); // 获取单元格中指定的行对象
			if (row != null) {
				columns = row.getLastCellNum();
				colStr = new String[columns];
				for (int c = 0; c < columns; c++) { // 循环遍历行中的单元格
					Cell cell = row.getCell(c);
					colStr[c] = trim(getValue(cell));
				}
				list.add(colStr);
			}
		}
		return list;
	}
	
	private static String getValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		 String result = new String();  
//	        switch (cell.getCellType()) {  
	        switch (cell.getCellTypeEnum()) {  
	        case NUMERIC :// 数字类型  
	        	short formatType=cell.getCellStyle().getDataFormat();
//	        	System.out.println(formatType);
//	        	System.out.println(cell.getCellStyle().getDataFormatString());
//	        	System.out.println(cell.getStringCellValue());
					
				if (formatType == 14 || formatType == 31 || formatType == 57 || formatType == 58 || formatType == 20
						|| formatType == 32) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
					SimpleDateFormat sdf = null;
					if (formatType == 14)
//						sdf = new SimpleDateFormat("yyyy-MM-dd");
					    sdf = new SimpleDateFormat("yyyy/M/dd");
					else if (formatType == 31)
						sdf = new SimpleDateFormat("yyyy年MM月dd日");
					else if (formatType == 57)
						sdf = new SimpleDateFormat("yyyy年MM月");
					else if (formatType == 58)
						sdf = new SimpleDateFormat("M月d日");
					else if (formatType == 20)
						sdf = new SimpleDateFormat("HH:mm");
					else if (formatType == 32)
						sdf = new SimpleDateFormat("h时mm分");
					else
						sdf = new SimpleDateFormat("yyyy-MM-dd");
//	                yyyy年m月d日--- 31
//	                yyyy年m月------- 57
//	                m月d日  ---------- 58
//	                HH:mm----------- 20
//	                h时mm分  ------- 32
					
	                double value = cell.getNumericCellValue();
	                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);  
	                result = sdf.format(date);  
	                
				} else if (formatType == 177 ) {
					SimpleDateFormat sdf = null;
					sdf = new SimpleDateFormat("yyyy/M/d");
					Date date = cell.getDateCellValue();
					result = sdf.format(date);   
	                
				} else if (formatType == 178 ) {
					SimpleDateFormat sdf = null;
					sdf = new SimpleDateFormat("d-MMM-yy");
					Date date = cell.getDateCellValue();
					result = sdf.format(date);
					
				} else if ( formatType == 179) {
					SimpleDateFormat sdf = null;
					sdf = new SimpleDateFormat("MM/dd/yy");
					Date date = cell.getDateCellValue();
					result = sdf.format(date);
				} else if ( formatType == 180) {
					SimpleDateFormat sdf = null;
					sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = cell.getDateCellValue();
					result = sdf.format(date);
				} else if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
	                SimpleDateFormat sdf = null;  
	                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
	                    sdf = new SimpleDateFormat("HH:mm");  
	                } else {// 日期  
	                    sdf = new SimpleDateFormat("yyyy/MM/dd");  
	                }  
	                Date date = cell.getDateCellValue();  
	                result = sdf.format(date);  
				} else {
	    			double cur = cell.getNumericCellValue();
	    			long longVal = Math.round(cur);
	    			Object inputValue = null;
	    			if (Double.parseDouble(longVal + ".0") == cur)
	    				inputValue = longVal;
	    			else
	    				inputValue = cur;
	    			result = String.valueOf(inputValue);
	            }  
	            break;  
	        case STRING:// String类型  
	            result = cell.getRichStringCellValue().toString();  
	            break;  
	        case BLANK:  
	            result = "";  
	        case BOOLEAN:
				result = String.valueOf(cell.getBooleanCellValue());
	        default:  
//	            result = "";  
				result = cell.getStringCellValue();
	            break;  
	        }  
	        return result;  
	}

	private static String trim(String str) {
		if (str == null) return null;
		return str.trim();
	}

	public static String checkTitle(String hopeTitleArray[], String excelTitle[]) {
		if (StringUtils.isEmpty(hopeTitleArray) || StringUtils.isEmpty(excelTitle))
			return "Warn: hopeTitleArray or excelTitle is empty!";
		if (hopeTitleArray.length != excelTitle.length) {
			return "Warn: the length of hopeTitleArray and excelTitle are diffenent!";
		}
		String msg = "";
		for (int i = 0; i < hopeTitleArray.length; i++) {
			if (!hopeTitleArray[i].trim().equals(excelTitle[i].trim())) {
				msg += i + " ,";
			}
		}
		return msg;
	}

}
