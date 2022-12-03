/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.test;

import java.util.List;

import org.teasoft.bee.osql.Suid;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQL;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test {
	
	public static void main(String[] args) {
//		MongodbObjSuid suid=new MongodbObjSuid();
		
//		MongodbBeeSql suid=new MongodbSqlLib();
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQL mongodbObjSQL=new MongodbObjSQL();
		mongodbObjSQL.setMongodbBeeSql(mongodbBeeSql);
//		Suid suid =new MongodbObjSuid();
		Suid suid =mongodbObjSQL;
		
		Orders orders=new Orders();
		orders.setAbc("test bee ");
		orders.setName("mongodb");
		orders.setId(10109L);
		
//		suid.insert(orders);
		
		
		
		List<Orders> list=suid.select(new Orders());
		Printer.printList(list);
		
		orders.setRemark("被修改了");
		orders.setAbc("test bee , update!");
		int updateNum=suid.update(orders);
		System.out.println("updateNum: "+updateNum);
		
		
		List<Orders> list2=suid.select(new Orders());
		Printer.printList(list2);
		
		
//		Orders ordersDel=new Orders();
////		ordersDel.setId(10003L);
////		ordersDel.setName("mongodb");
//		ordersDel.setName("mongodb");
//		int delNum=suid.delete(ordersDel);
//		System.out.println("delNum: "+delNum);
		
		System.out.println("finished!");
	}

}
