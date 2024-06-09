/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
