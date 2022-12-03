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
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test3_selectField {
	
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
		
//		Orders orders=suidRich.selectById(new Orders(),10020L);
//		System.out.println("selectById orders:"+orders.toString());
//		
//		orders=suidRich.selectById(new Orders(),"10021");
//		System.out.println("selectById orders:"+orders.toString());
		
		System.out.println("----------------------------");
		
//		List<Orders> list=suidRich.select(new Orders(), "name");
		List<Orders> list=suidRich.select(new Orders(), "name,remark");
		Printer.printList(list);
		System.out.println("----------------------------");
		
		list=suidRich.select(new Orders(), "name,remark",0,3);
		Printer.printList(list);
		
		list=suidRich.select(new Orders(), "id,name,remark",0,3);
		Printer.printList(list);
		
		System.out.println("--------------select(new Orders(), 0,3)--------------");
		
		list=suidRich.select(new Orders(), 0,3);
		Printer.printList(list);
		
		HoneyConfig.getHoneyConfig().selectJson_longToString=false;
		System.out.println("--------json");
		String json=suidRich.selectJson(new Orders());
		System.out.println("json::  "+json);
		
		System.out.println("finished!");
	}

}
