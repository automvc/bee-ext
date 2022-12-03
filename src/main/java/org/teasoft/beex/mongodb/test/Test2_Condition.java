/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;
import org.teasoft.honey.osql.shortcut.BF;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test2_Condition {
	
	public static void main(String[] args) {
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQLRich mongodbObjSQLRich=new MongodbObjSQLRich();
		mongodbObjSQLRich.setMongodbBeeSql(mongodbBeeSql);
		
		SuidRich suidRich =mongodbObjSQLRich;
		Orders orders=new Orders();
		
		
		Condition condtion=BF.getCondition();
		
//		orders=new Orders();
//		List<Orders> list=suidRich.select(orders,condtion);
//		Printer.printList(list);
		
		
		System.out.println("==============================================");
		System.out.println();
		Condition condtion6=BF.getCondition();
		condtion6.op("abc", Op.like, "*");
		condtion6.or();
		condtion6.op("abc", Op.like, "?");
//		List<Orders> list6=suidRich.select(new Orders(),condtion6);
//		Printer.printList(list6);
		
		System.out.println("--------------------------------");
//		condtion6.and();
		condtion6.op("name", Op.eq, "mongodb*");
		List<Orders> list7=suidRich.select(new Orders(),condtion6);
		Printer.printList(list7);
		
		System.out.println("finished!");
	}

}
