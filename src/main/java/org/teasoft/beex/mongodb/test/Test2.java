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
import org.teasoft.bee.osql.Suid;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.mongodb.MongodbBeeSql;
import org.teasoft.honey.osql.core.MongodbObjSQL;
import org.teasoft.honey.osql.shortcut.BF;
import org.teasoft.honey.util.Printer;

/**
 * @author AiTeaSoft
 * @since  2.0
 */
public class Test2 {
	
	public static void main(String[] args) {
//		MongodbObjSuid suid=new MongodbObjSuid();
		
//		MongodbBeeSql suid=new MongodbSqlLib();
		
		MongodbBeeSql mongodbBeeSql=new MongodbSqlLib();
		MongodbObjSQL mongodbObjSQL=new MongodbObjSQL();
		mongodbObjSQL.setMongodbBeeSql(mongodbBeeSql);
//		Suid suid =new MongodbObjSuid();
		Suid suid =mongodbObjSQL;
		Orders orders=new Orders();
		
		for (int i = 15; i < 25; i++) {
			orders.setAbc("test bee ");
			orders.setName("mongodb" + i);
			orders.setId(10000L + i);

//			suid.insert(orders);  //TODO
		}
		
		Condition condtion=BF.getCondition();
//		condtion.op("id", Op.ge, 10006L);
		
		
		List idList=new ArrayList<>();
		idList.add(10006L);
		idList.add(10008L);
		
//		idList.add("10006");  //不对类型，查不出来
//		idList.add("10009");
		
		Set set=new HashSet();
		set.add(10006L);
		set.add(10008L);
		
//		condtion.op("id", Op.in, new String[] { "10006,10007" });
//		condtion.op("id", Op.in, new Long[] { 10006L,10008L });  //数组不行.     set, list可以
//		condtion.op("id", Op.in, idList);
		condtion.op("id", Op.in, set);
		orders=new Orders();
		List<Orders> list=suid.select(orders,condtion);
		Printer.printList(list);
		
//		orders.setRemark("被修改了");
//		orders.setAbc("test bee , update!");
//		int updateNum=suid.update(orders);
//		System.out.println("updateNum: "+updateNum);
//		
//		
//		List<Orders> list2=suid.select(new Orders());
//		Printer.printList(list2);
		
		
//		Orders ordersDel=new Orders();
////		ordersDel.setId(10003L);
////		ordersDel.setName("mongodb");
//		ordersDel.setName("mongodb211");
//		int delNum=suid.delete(ordersDel);
//		System.out.println("delNum: "+delNum);
		//手动输入的id值, 是10011,  debug却看到10011.0
		
		System.out.println("==============================================");
		System.out.println();
		Condition condtion6=BF.getCondition();
//		condtion6.op("abc", Op.likeLeft, "test2");
//		condtion6.op("abc", Op.likeRight, "test");
//		condtion6.op("abc", Op.like, "bee");
//		condtion6.op("abc", Op.like, "");
		condtion6.op("abc", Op.like, "*");
//		condtion6.op("abc", Op.like, "?");
//		condtion6.op("abc", Op.like, "+");
//		condtion6.op("abc", Op.like, "$");
//		condtion6.op("abc", Op.like, ".");
//		condtion6.op("abc", Op.like, "|");
//		condtion6.op("abc", Op.like, "[");
//		condtion6.op("abc", Op.like, "(");
//		condtion6.op("abc", Op.like, "{");
//		condtion6.op("abc", Op.like, "[]");
//		condtion6.op("abc", Op.like, "\\");
//		condtion6.op("abc", Op.like, "\\u002A"); unicode编码,还不能判断    \u002A是*
//		condtion6.op("abc", Op.likeLeft, "\\u002a");
//		condtion6.op("abc", Op.likeRight, "\u002a"); //  一个 \可以过滤
//		condtion6.op("abc", Op.like, "%");
		List<Orders> list6=suid.select(new Orders(),condtion6);
		Printer.printList(list6);
		
		System.out.println("finished!");
	}

}
