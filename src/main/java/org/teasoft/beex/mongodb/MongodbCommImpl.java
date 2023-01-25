/*
 * Copyright 2020-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import java.util.Map.Entry;
import java.util.Set;

import org.teasoft.honey.osql.mongodb.MongodbComm;

/**
 * @author Kingstar
 * @since  2.0
 */
public class MongodbCommImpl implements MongodbComm {

	@Override
	public Set<Entry<String, Object>> getCollectStrcut(String collectionName) {
		return MongodbUtil.getCollectStrcut(collectionName);
	}

	@Override
	public String[] getAllCollectionNames() {
		return MongodbUtil.getAllCollectionNames();
	}

}
