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
public class Test4_count {
	
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
		
		int c=suidRich.count(orders);
		System.out.println("count="+c);
		
		int c2=suidRich.count(orders,null);
		System.out.println("count="+c2);
		
		Condition condition=BF.getCondition();
		int c3=suidRich.count(orders,condition);
		System.out.println("count="+c3);
		
		
		Condition condition2=BF.getCondition();
		condition2.op("name", Op.eq, "mongodb2");
		int c4=suidRich.count(orders,condition2);
		System.out.println("count="+c4);
		
		orders.setAbc("[");
		Condition condition3=BF.getCondition();
		int c5=suidRich.count(orders,condition3);
		System.out.println("count="+c5);
		
		System.out.println("finished!");
	}

}
