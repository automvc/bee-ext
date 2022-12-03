/*
getMongodbBeeSql() * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQLRich;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test4_insertList {
	
	public static void main(String[] args) {
//		MongodbObjSuid suid=new MongodbObjSuid();
		
//		MongodbBeeSql suid=new MongodbSqlLib();
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQLRich mongodbObjSQLRich=new MongodbObjSQLRich();
		mongodbObjSQLRich.setMongodbBeeSql(mongodbBeeSql);
//		Suid suid =new MongodbObjSuid();
		SuidRich suidRich =mongodbObjSQLRich;
//		Suid suid =new MongodbObjSuid();
		Orders orders=new Orders();
		
		List<Orders> insertList=new ArrayList<>();
		
		for (int i = 70; i < 80; i++) {
			orders=new Orders();
			orders.setAbc("test bee ");
			orders.setName("mongodb" + i);
			orders.setId(10200L + i);
			orders.setNum(i);
			orders.setTotal(new BigDecimal((i+i*0.01)+""));
			
			insertList.add(orders);
		}
		
		int insertNum=suidRich.insert(insertList);
//		int insertNum=suidRich.insert(insertList,3);
//		int insertNum=suidRich.insert(insertList,5);
		
		System.out.println("insertNum="+insertNum);
		
		
		
		orders=new Orders();
		orders.setAbc("test bee ");
		orders.setName("mongodb" + 99);
		orders.setId(10200L + 99);
//		long returnId=suidRich.insertAndReturnId(orders);
		long returnId=suidRich.insertAndReturnId(orders,IncludeType.EXCLUDE_BOTH);
		System.out.println("returnId:  "+returnId);
		
		System.out.println("finished!");
	}

}
