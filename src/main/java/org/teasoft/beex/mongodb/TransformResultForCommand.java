/*
 * Copyright 2016-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.teasoft.beex.json.JsonUtil;

/**
 * @author Kingstar
 * @since  2.1
 */
public class TransformResultForCommand {
	
	public static int transformResult(String type, Document result) {
//	    update.n
//	    The number of documents selected for update. 
//	    update.nModified
//	    The number of documents updated. If the update operation results in no change to the document,
//	    such as setting the value of the field to its current value, 
//	    nModified can be less than n.
		Integer num = 0;
		if ("updateOne".equals(type) || "updateMany".equals(type) || "update".equals(type)
				|| "replaceOne".equals(type) || "save".equals(type)) {
			num = (Integer) result.get("nModified"); //更新时,是获取这个得到更新的行数
			if (num == null) num = (Integer) result.get("n");
		} else {
			num = (Integer) result.get("n");
		}
		return num;
	}
	
	public static String transformResult(Document result) {
		String json = "";
		if (result != null) {
			Document c = (Document) result.get("cursor");
			if (c != null) {
				Object obj = c.get("firstBatch"); // java.util.ArrayList
				if (obj != null) json = JsonUtil.toJson(obj);
			}
		}
		return json;
	}
	
	
	public static List<Map<String, Object>> transformResultForListMap(Document result) {
		List<Map<String, Object>> rsList = null;
		if (result != null) {
			Document c = (Document) result.get("cursor");
			if (c != null) {
				Object obj = c.get("firstBatch"); // java.util.ArrayList
				List<Document> list = (List<Document>) obj;
				if (list != null && list.size() > 0) {
					rsList = new ArrayList<>(list.size());
					for (Document doc : list) {
						rsList.add(TransformResult.doc2Map(doc));
					}
				}
			}
		}
		if (rsList == null) rsList = new ArrayList<>();
		
		return rsList;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> transformResultForListT(Document result,Class<T> returnTypeClass) {
		List<T> rsList = null;
		if (result != null) {
			Document c = (Document) result.get("cursor");
			
			if (c != null) {
				Object obj = c.get("firstBatch"); //java.util.ArrayList
				rsList = TransformResult.toListEntity2((List)obj, returnTypeClass);
			}
		}
		if (rsList == null) rsList = new ArrayList<>();
		
		return rsList;
	}

}
