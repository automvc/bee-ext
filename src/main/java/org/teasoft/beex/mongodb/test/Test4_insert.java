/*
getMongodbBeeSql() * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.Suid;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQL;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;
import org.teasoft.honey.osql.shortcut.BF;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test4_insert {
	
	public static void main(String[] args) {
//		MongodbObjSuid suid=new MongodbObjSuid();
		
//		MongodbBeeSql suid=new MongodbSqlLib();
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQLRich mongodbObjSQLRich=new MongodbObjSQLRich();
		mongodbObjSQLRich.setMongodbBeeSql(mongodbBeeSql);
//		Suid suid =new MongodbObjSuid();
		SuidRich suidRich =mongodbObjSQLRich;
//		Suid suid =new MongodbObjSuid();
//		Orders orders=new Orders();
//		
//		List<Orders> insertList=new ArrayList<>();
//		
//		for (int i = 60; i < 70; i++) {
//			orders=new Orders();
//			orders.setAbc("test bee ");
//			orders.setName("mongodb" + i);
//			orders.setId(10100L + i);
//			
//			insertList.add(orders);
//		}
		
//		int insertNum=suidRich.insert(insertList);
//		int insertNum=suidRich.insert(insertList,3);
//		int insertNum=suidRich.insert(insertList,5);
		
//		System.out.println("insertNum="+insertNum);
		
		
		
//		orders=new Orders();
		Noid0 noid0=new Noid0();
//		orders.setAbc("test bee ");
		noid0.setName("mongodb" + 102);
		noid0.setNum(102);
//		orders.setId(10100L + 98);
//		long returnId=suidRich.insertAndReturnId(orders);
		int a=suidRich.insert(noid0);
		System.out.println("a:  "+a);
		
		System.out.println("finished!");
	}

}
