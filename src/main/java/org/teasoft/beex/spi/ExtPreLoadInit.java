/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.spi;

import org.teasoft.bee.spi.PreLoad;
import org.teasoft.beex.mongodb.MongodbCommImpl;
import org.teasoft.beex.mongodb.MongodbSqlLib;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.mongodb.MongodbBeeSqlRegister;
import org.teasoft.honey.osql.mongodb.MongodbCommRegister;

/**
 * Bee-Ext提前预加载初始化
 * @author Kingstar
 * @since  2.0
 */
public class ExtPreLoadInit implements PreLoad{
	
	static {
		Logger.info("[Bee] ========= Preload class ExtPreLoadInit, load...");
		init();
	}
	
	private static void init() {
		MongodbBeeSqlRegister.register(new MongodbSqlLib());   //获取时,是null,时才生成,就不用在这加载了.  TODO
		MongodbCommRegister.register(new MongodbCommImpl());
	}
}
