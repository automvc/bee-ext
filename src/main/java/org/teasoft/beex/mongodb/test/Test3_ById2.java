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
public class Test3_ById2 {
	
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
		
//		Noid0 noid0=suidRich.selectById(new Noid0(),"63862e06bdcb6d0dac9c02c7");
		Noid0 noid0=suidRich.selectById(new Noid0(),"ewewewewew"); //不是mongodb的string id格式.
		if(noid0!=null)System.out.println("selectById Noid0:"+noid0.toString());
		else {
			System.out.println("it is null !");
		}
		
		List<Noid0> list=suidRich.selectByIds(new Noid0(),"63862e06bdcb6d0dac9c02c7,6386323f3f581a223ab8a10e,ewewewewew");
		Printer.printList(list);
		
		
		List<Noid0> list2=suidRich.select(new Noid0());
		Printer.printList(list2);
		
		
		//还要测_id,不是mongodb的格式的  TODO
		
		//主键不是id,  返回没有正确赋值.  TODO
		
		
//		orders=suidRich.selectById(new Orders(),"10021");
//		System.out.println("selectById orders:"+orders.toString());
//		System.out.println("----------------------------");
//		
//		List<Orders> list=suidRich.selectByIds(new Orders(),"10020,10021");
//		Printer.printList(list);
//		
//		int delNum=suidRich.deleteById(Orders.class, 10017L);
//		System.out.println("delNum:"+delNum);
//		
//		delNum=suidRich.deleteById(Orders.class, "10090,10091,10092,10093");
//		System.out.println("delNum:"+delNum);
		
		System.out.println("finished!");
	}

}
