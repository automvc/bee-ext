package org.teasoft.beex.mongodb;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.teasoft.bee.osql.exception.BeeErrorGrammarException;
import org.teasoft.bee.osql.exception.BeeIllegalBusinessException;
import org.teasoft.bee.spi.JsonTransform;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.spi.SpiInstanceFactory;
import org.teasoft.honey.util.StringParser;
import org.teasoft.honey.util.StringUtils;

/**
 * Mongo shell Command Engine
 * @author AiTeaSoft
 * @since 2.1
 */
public class CommandEngine {
	public Bson parseSuidCommand(String str) {
		return parseSuidCommand(str, null);
	}

	// eg:"db.movies.find()";
	public Bson parseSuidCommand(String str, String tableAndType[]) {

		if (tableAndType == null) tableAndType = getTableAndType(str);

		String tableName = tableAndType[0];
		String type = tableAndType[1];
		
		str = str.trim();
		if (str.endsWith(";")) str = str.substring(0, str.length() - 1).trim();

		int index3 = str.indexOf('(', 3);
		String inputJson = str.substring(index3, str.length()).trim();

		inputJson = inputJson.trim();
		Bson r = null;// None
		if ("find".equals(type) || "findOne".equals(type)) {
			r = select(tableName, inputJson);
		} else if ("insertOne".equals(type) || "insertMany".equals(type)
				|| "insert".equals(type)) {
			r = insert(tableName, inputJson);
		} else if ("deleteOne".equals(type)) {
			r = delete(tableName, inputJson, true);
		} else if ("deleteMany".equals(type) || "remove".equals(type)) {
			r = delete(tableName, inputJson, false);
		} else if ("updateOne".equals(type)) {
			r = update(tableName, inputJson, false);
		} else if ("updateMany".equals(type) || "update".equals(type)) {
			r = update(tableName, inputJson, true);
		} else if ("replaceOne".equals(type) || "save".equals(type)) {
			r = update(tableName, inputJson, true, true);
		}
		return r;
	}

	public Bson select(String tableName, String inputJson) {

		int pos = StringParser.getEndPosition(inputJson); // 查找({开头，} )结束的结束下标位置
		Document doc = new Document();
		doc.append("find", tableName);

		if (pos > 0) {
			String findJson = inputJson.substring(1, pos).trim();
			String selectColumn = "";
			int filterEndIndex = StringParser.getKeyEndPositionByStartEnd(findJson, '{', '}');
			String filterJson;
			if (findJson.length() > filterEndIndex + 1) {
				String newFilter = findJson.substring(0, filterEndIndex + 1);
				selectColumn = findJson.substring(filterEndIndex + 1).trim().substring(1)
						.trim();
				filterJson = newFilter;
			} else {
				filterJson = findJson;
			}

			String others = inputJson.substring(pos + 1, inputJson.length());
			String limitPara = "";
			String sortPara = "";
			String skipPara = "";

			if (StringUtils.isNotBlank(others)) {
//		       .limit(1).sort({"runtime":1}).skip(1)
				int limitIndex = StringParser.getKeyPosition(others, "limit(");
				int sortIndex = StringParser.getKeyPosition(others, "sort(");
				int skipIndex = StringParser.getKeyPosition(others, "skip(");

				if (limitIndex > 0) {
					String limitOthers = others.substring(limitIndex + 6);
					limitPara = limitOthers.substring(0,
							StringParser.getKeyPosition(limitOthers, ")"));
				}

				if (sortIndex > 0) {
					String sortOthers = others.substring(sortIndex + 5);
					sortPara = sortOthers.substring(0,
							StringParser.getKeyPosition(sortOthers, ")"));
				}

				if (skipIndex > 0) {
					String skipOthers = others.substring(skipIndex + 5);
					skipPara = skipOthers.substring(0,
							StringParser.getKeyPosition(skipOthers, ")"));
				}
			}

			if ("".equals(inputJson)) {
				// empty
			} else {
				Document filter = Document.parse(filterJson);
				doc.append("filter", filter);

				if (StringUtils.isNotBlank(selectColumn)) {
					Document projection = Document.parse(selectColumn);
					doc.append("projection", projection);
				}

//			          执行的顺序是先 sort(), 然后是 skip()，最后是显示的 limit()。
				if (StringUtils.isNotBlank(sortPara)) {
					Document sortDoc = Document.parse(sortPara);
					doc.append("sort", sortDoc);
				}

				if (StringUtils.isNotBlank(skipPara)) {
					doc.put("skip", Integer.parseInt(skipPara.trim()));
				}

				if (StringUtils.isNotBlank(limitPara)) {
					doc.put("limit", Integer.parseInt(limitPara.trim()));
				}
			}
		}
//		else { //不需要判断。 因为,find支持：find()，即如：db.movies.find()
//			throw new BeeErrorGrammarException("The query command can not find end String \"})\"");
//		}
		return doc;
	}

	public Bson insert(String tableName, String inputJson) {
		Document doc = new Document();
		doc.append("insert", tableName);
		String para = inputJson.substring(1, inputJson.length() - 1);
//		List<Document> list = JSONObject.parseArray(para, Document.class);// 把字符串转换成List<>    ok
//		List<Document> list = (List<Document>)JsonUtil.toEntity(para, List.class, Document.class);// 把字符串转换成List<>   字段名没有双引号会报错;单个实体会报错;
		JsonTransform jsonTransform = SpiInstanceFactory.getJsonTransform();
		List<Document> list = (List<Document>)jsonTransform.toEntityList(para, Document.class);
		doc.append("documents", list);

		return doc;
	}

	public Bson delete(String tableName, String inputJson, boolean isDelOne) {
		Document doc = new Document();
		doc.append("delete", tableName);

		String para = inputJson.substring(1, inputJson.length() - 1);

		if ("{}".equals(para.replace(" ", ""))) {
			boolean notDeleteWholeRecords = HoneyConfig.getHoneyConfig().notDeleteWholeRecords;
			if (notDeleteWholeRecords) {
				throw new BeeIllegalBusinessException(
						"BeeIllegalBusinessException: It is not allowed delete whole documents(records) in one collection(table).If need, you can change the config in bee.osql.notDeleteWholeRecords !");
			}
		}

		Document filter = new Document();
		Document queryDoc = Document.parse(para);
		filter.append("q", queryDoc);
		int delFlag = isDelOne ? 1 : 0;
		filter.append("limit", delFlag);
		doc.append("deletes", Arrays.asList(filter));

		return doc;
	}

	public Bson update(String tableName, String inputJson, boolean isMulit) {
		return update(tableName, inputJson, isMulit, false);
	}

	public Bson update(String tableName, String inputJson, boolean isMulit,
			boolean isUpdateOrInsert) {
		Document doc = new Document();
		doc.append("update", tableName);

		int pos = StringParser.getEndPosition(inputJson); // 查找({开头，} )结束的结束下标位置
		if (pos > 0) {
			String findJson = inputJson.substring(1, pos).trim(); //去掉括号
			String setPart = "";
			int filterEndIndex = StringParser.getKeyEndPositionByStartEnd(findJson, '{', '}');
			String filterJson = "";
			if (filterEndIndex + 3 < findJson.length() - 1) { //+3， set至少有: ,{}
				String newFilter = findJson.substring(0, filterEndIndex + 1);
				setPart = findJson.substring(filterEndIndex + 1).trim().substring(1).trim();
				filterJson = newFilter;
			} else {
				throw new BeeErrorGrammarException("The update set part is empty!");
			}

			Document oneUpdate = new Document();
			oneUpdate.append("q", Document.parse(filterJson));
			oneUpdate.append("u", Document.parse(setPart));
			oneUpdate.append("multi", isMulit);
			oneUpdate.append("upsert", isUpdateOrInsert); //
			doc.append("updates", Arrays.asList(oneUpdate));
		} else {
			throw new BeeErrorGrammarException("The update command can not find end String \"})\"");
		}

		return doc;
	}

	public String[] getTableAndType(String str) {

		if (str == null) return null; 
			
		str = str.trim();
		// 检测
		if (str != null && !str.startsWith("db.")) {
			throw new BeeErrorGrammarException("The mongo command must start with 'db.'!");
		}
		
		if (str == null) return null; //for sonarqube's bug
			
		if (str.endsWith(";")) str = str.substring(0, str.length() - 1).trim();

		int index1 = str.indexOf('.');
		int index3 = str.indexOf('(', index1 + 1);

		String tableNameAndType = str.substring(index1 + 1, index3).trim();
		int index2 = tableNameAndType.lastIndexOf('.'); // 表名和类型的分隔
		String tableName = "";
		String type = "";
		if (index2 > 0) {
			tableName = tableNameAndType.substring(0, index2).trim();
			type = tableNameAndType.substring(index2 + 1, tableNameAndType.length()).trim();
		} else { // 处理这种语法: db.getCollection('movies'),   //暂不支持这种:db.getSiblingDB("examples")
			if (!"getCollection".equals(tableNameAndType)) {
				throw new BeeErrorGrammarException("Do not support this mongo command grammar!");
			} else {
				int index4 = str.indexOf('(', index3 + 1);
				tableNameAndType = str.substring(index3 + 1, index4).trim();
//				.println(tableNameAndType);  //'orders').find

				int index20 = tableNameAndType.indexOf(')');
				String t_tableName = tableNameAndType.substring(1, index20).trim();
				tableName = t_tableName.substring(0, t_tableName.length() - 1);

				int index21 = tableNameAndType.indexOf('.', index20);
				type = tableNameAndType.substring(index21 + 1).trim();
			}
		}
		
		return new String[] { tableName, type };
      }
}
