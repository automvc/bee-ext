/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.test;

import java.util.List;

import org.teasoft.bee.osql.SuidRich;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test3_ById {
	
	public static void main(String[] args) {
//		MongodbObjSuid suid=new MongodbObjSuid();
		
//		MongodbBeeSql suid=new MongodbSqlLib();
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQLRich mongodbObjSQLRich=new MongodbObjSQLRich();
		mongodbObjSQLRich.setMongodbBeeSql(mongodbBeeSql);
//		Suid suid =new MongodbObjSuid();
		SuidRich suidRich =mongodbObjSQLRich;
		
//		Orders orders=new Orders();
//		orders.setAbc("test bee ");
//		orders.setName("mongodb");
//		orders.setId(10009L);
//		
//		suidRich.insert(orders);
		
		Orders orders=suidRich.selectById(new Orders(),10020L);
		System.out.println("selectById orders:"+orders.toString());
		System.out.println("----------------------------");
		
		orders=suidRich.selectById(new Orders(),"10021");
		System.out.println("selectById orders:"+orders.toString());
		System.out.println("----------------------------");
		
		List<Orders> list=suidRich.selectByIds(new Orders(),"10020,10021");
		Printer.printList(list);
		
		int delNum=suidRich.deleteById(Orders.class, 10017L);
		System.out.println("delNum:"+delNum);
		
		delNum=suidRich.deleteById(Orders.class, "10090,10091,10092,10093");
		System.out.println("delNum:"+delNum);
		
		System.out.println("finished!");
	}

}
