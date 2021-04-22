/*
 * Copyright 2016-2021 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kingstar
 * @since  1.9
 */
public class CnNum {
	
	private static String uint[]= {"","十","百","千","万","十万","百万","千万","亿","十亿","百亿","千亿"};
	private static String upperUint[]= {"","拾","佰","仟","万","拾万","百万","仟万","亿","拾亿","百亿","仟亿"};//"","拾","佰","仟"
	private static String upperDecimalUint[]= {"角","分","厘","","",""};
	
	private static Map<String,String> numMap=new HashMap<>();
	private static Map<String,String> numUpperMap=new HashMap<>();
	static{
		numMap.put("0", "零");
		numMap.put("1", "一");
		numMap.put("2", "二");
		numMap.put("3", "三");
		numMap.put("4", "四");
		numMap.put("5", "五");
		numMap.put("6", "六");
		numMap.put("7", "七");
		numMap.put("8", "八");
		numMap.put("9", "九");
		
		numUpperMap.put("0", "零");
		numUpperMap.put("1", "壹");
		numUpperMap.put("2", "贰");
		numUpperMap.put("3", "叁");
		numUpperMap.put("4", "肆");
		numUpperMap.put("5", "伍");
		numUpperMap.put("6", "陆");
		numUpperMap.put("7", "柒");
		numUpperMap.put("8", "捌");
		numUpperMap.put("9", "玖");
	}
	
	public static String tran(long num) {
		return tran(num+"",false);
	}
	
	public static String tranToUpper(long num) {
		return tran(num+"",true);
	}
	
	public static String tran(double num) {
		return tranToUpper(num,false);
	}
	
	public static String tranToUpper(double num) {
		return tranToUpper(num,true);
	}
	
	private static String tranToUpper(double num,boolean isUpperNum) {
		String d1=num+"";
		System.err.println(d1);
		int index=d1.indexOf(".");
		String s1;
		String s2;
		if(index>-1) {
			s1=d1.substring(0,index);
			String integerPart= tran(s1,isUpperNum);
			s2=d1.substring(index+1,d1.length());
			String decimalPart=tranDecimal(s2, isUpperNum);
			if(isUpperNum) {
				return integerPart+"圆"+decimalPart;
			}else {
				return integerPart+"点"+decimalPart;
			}
		}else {
			return tran(d1,true);
		}
		
	}
	
	private static String tran(String longNumText,boolean isUpperNum) {
		
		char ch[]=longNumText.toCharArray();
		StringBuffer sbu=new StringBuffer();
//		sbu.append(ch[0]);
//		sbu.append(numMap.get(ch[0]+""));
		if (isUpperNum) {
			sbu.append(numUpperMap.get(ch[0] + ""));
			sbu.append(upperUint[ch.length-1]);
		}else {
			sbu.append(numMap.get(ch[0] + ""));
			sbu.append(uint[ch.length-1]);
		}
		
		boolean hasZero=false;
		for (int i = 1; i < ch.length; i++) {
			if(ch[i]=='0') {
				hasZero=true;
			}else {
				if(hasZero) {
					sbu.append("零");
					hasZero=false;
				}
//				sbu.append(ch[i]);
				if (isUpperNum) {
					sbu.append(numUpperMap.get(ch[i] + ""));
					sbu.append(upperUint[ch.length-1-i]);
				}else {
					sbu.append(numMap.get(ch[i] + ""));
					sbu.append(uint[ch.length-1-i]);
				}
				
			}
		}
		
		return sbu.toString();
	}
	
	private static String tranDecimal(String longNumText,boolean isUpperNum) {
		char ch[]=longNumText.toCharArray();
		StringBuffer sbu=new StringBuffer();
		if (isUpperNum) {
			sbu.append(numUpperMap.get(ch[0] + ""));
			sbu.append(upperDecimalUint[0]);
		}else {
			sbu.append(numMap.get(ch[0] + ""));
//			sbu.append(uint[ch.length-1]);
		}
		
		boolean hasZero=false;
		for (int i = 1; i < ch.length; i++) {
			if(ch[i]=='0') {
				hasZero=true;
			}else {
				if(hasZero) {
					sbu.append("零");
					hasZero=false;
				}
				if (isUpperNum) {
					sbu.append(numUpperMap.get(ch[i] + ""));
//					sbu.append(upperUint[ch.length-1-i]);
					sbu.append(upperDecimalUint[i]);
				}else {
					sbu.append(numMap.get(ch[i] + ""));
//					sbu.append(uint[ch.length-1-i]);
				}
				
			}
		}
		
		return sbu.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(tran(1100));
		System.out.println(tran(1001));
		System.out.println(tran(20030));
		System.out.println(tran(500005));
		System.out.println(tran(500305));
		
		
		System.out.println(tranToUpper(1100));
		System.out.println(tranToUpper(1001));
		System.out.println(tranToUpper(20030));
		System.out.println(tranToUpper(500005));
		System.out.println(tranToUpper(500305));
		
		System.out.println("======================");
		
		System.out.println(tranToUpper(500305D));
		System.out.println(tranToUpper(500305.23D));
		System.out.println(tranToUpper(500305.123D));
		
		System.out.println(tran(500305.23D));
		System.out.println(tran(5003050.234D));
		
		System.out.println("======================");
		System.out.println(tran(1004));
		System.out.println(tran(2046));
		System.out.println(tran(2932));
		System.out.println(tran(9040));
		System.out.println(tran(1001));
		System.out.println(tran(327));
		System.out.println(tran(8052));
		System.out.println(tran(1503));
		System.out.println(tran(1100));
		System.out.println(tran(2500));
		System.out.println(tran(970));
		System.out.println(tran(1900));
		
	}
	
	
	

}
