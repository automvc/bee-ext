/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import org.teasoft.honey.osql.mongodb.MongodbBeeSqlRegister;
import org.teasoft.honey.osql.mongodb.MongodbCommRegister;

/**
 * @author Kingstar
 * @since  2.1
 */
public class MongodbRegHandler {
	static {
		init();
	}

	public static void init() {
		MongodbBeeSqlRegister.register(new MongodbSqlLib()); 
		MongodbCommRegister.register(new MongodbCommImpl());
	}
}
