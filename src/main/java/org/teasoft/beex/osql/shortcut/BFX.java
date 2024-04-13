/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql.shortcut;

import org.teasoft.beex.osql.ConditionExtImpl;
import org.teasoft.beex.osql.ObjSQLRichExt;
import org.teasoft.beex.osql.api.ConditionExt;
import org.teasoft.beex.osql.api.SuidRichExt;
import org.teasoft.beex.osql.mongodb.MongodbObjSQLRichExt;
import org.teasoft.beex.osql.mongodb.MongodbSuidRichExt;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

/**
 * @author Kingstar
 * @since  2.1
 */
public class BFX extends BeeFactoryHelper {

	private static SuidRichExt suidRichExt;
	private static MongodbSuidRichExt mongodbSuidRichExt;
	
	private static ConditionExt conditionExt;

	public static SuidRichExt getSuidRichExt() {
		if (suidRichExt == null) return new ObjSQLRichExt();
		return suidRichExt;
	}

	public static MongodbSuidRichExt getMongodbSuidRichExt() {
		if (mongodbSuidRichExt == null) return new MongodbObjSQLRichExt();
		return mongodbSuidRichExt;
	}

	public void setSuidRichExt(SuidRichExt suidRichExt) {
		_setSuidRichExt(suidRichExt);
	}

	public void setMongodbSuidRichExt(MongodbSuidRichExt mongodbSuidRichExt) {
		_setMongodbSuidRichExt(mongodbSuidRichExt);
	}

	private void _setSuidRichExt(SuidRichExt suidRichExt) {
		BFX.suidRichExt = suidRichExt;
	}

	private void _setMongodbSuidRichExt(MongodbSuidRichExt mongodbSuidRichExt) {
		BFX.mongodbSuidRichExt = mongodbSuidRichExt;
	}

	public static ConditionExt getConditionExt() {
		if(conditionExt==null) return new ConditionExtImpl();
		return conditionExt;
	}

	public static void setConditionExt(ConditionExt conditionExt) {
		BFX.conditionExt = conditionExt;
	}
	

}
