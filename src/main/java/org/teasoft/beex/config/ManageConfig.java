/*
 * Copyright 2016-2021 the original author.All rights reserved.
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

package org.teasoft.beex.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.teasoft.honey.osql.core.ExceptionHelper;
import org.teasoft.honey.osql.core.HoneyContext;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.name.NameUtil;

/**
 * 收集Spring Boot配置文件设置的属性.
 * Javabean的属性要用包装类型.若为null,表示没有更改,则不收集.
 * @author Kingstar
 * @since  1.9
 */
public class ManageConfig {

	private BeePro beePro;

	private BeeProCache beeProCache;

	private BeeProDb beeProDb;

	private BeeProGenid beeProGenid;

	private BeeProMoreTable beeProMoreTable;

	private BeeProMultiDS beeProMultiDS;

	private BeeProNaming beeProNaming;

	private BeeProPearFlowerId beeProPearFlowerId;

	private BeeProReturnStringList beeProReturnStringList;

	private BeeProSelectJson beeProSelectJson;

	private BeeProShowSql beeProShowSql;

	public void updateConfig() {
		Map<String, Object> newConfigMap = new LinkedHashMap<>();

		newConfigMap.putAll(process(beePro, false));
		newConfigMap.putAll(process(beeProCache));
		newConfigMap.putAll(process(beeProDb, false));

		newConfigMap.putAll(process(beeProGenid));
		newConfigMap.putAll(process(beeProMoreTable));
		newConfigMap.putAll(process(beeProMultiDS));
		newConfigMap.putAll(process(beeProNaming));
		newConfigMap.putAll(process(beeProPearFlowerId));
		newConfigMap.putAll(process(beeProReturnStringList));
		newConfigMap.putAll(process(beeProSelectJson));
		newConfigMap.putAll(process(beeProShowSql));

//		Logger.info("[Bee] new config: "+newConfigMap);
		HoneyContext.updateConfig(newConfigMap);  
		Logger.info("[Bee] new config: "+newConfigMap);  //更新后再用日志,否则用不了新的信息.
	}

	private Map<String, Object> process(Object propObject) {
		return process(propObject, true);

	}

	private Map<String, Object> process(Object propObject, boolean isNeedPrefix) {
		
		Map<String, Object> newConfigMap = new HashMap<>();
		
        if(propObject==null)  {
        	Logger.warn(" propObject is null! ");
        	return newConfigMap;
        }
        
		Field fields[] = propObject.getClass().getDeclaredFields();
		String modeFix = "";
		if (isNeedPrefix) {
			modeFix = NameUtil.firstLetterToLowerCase(propObject.getClass().getSimpleName().substring(6)) + "_";
		}

		int len = fields.length;
		for (int i = 0; i < len; i++) {
			try {
				fields[i].setAccessible(true);

				if (fields[i].get(propObject) == null || fields[i].isSynthetic()) {
					continue;
				} else {
					newConfigMap.put(modeFix + fields[i].getName(), fields[i].get(propObject));
				}

			} catch (IllegalAccessException e) {
				throw ExceptionHelper.convert(e);
			}
		}
		return newConfigMap;
	}

	public BeePro getBeePro() {
		return beePro;
	}

	public void setBeePro(BeePro beePro) {
		this.beePro = beePro;
	}

	public BeeProCache getBeeProCache() {
		return beeProCache;
	}

	public void setBeeProCache(BeeProCache beeProCache) {
		this.beeProCache = beeProCache;
	}

	public BeeProDb getBeeProDb() {
		return beeProDb;
	}

	public void setBeeProDb(BeeProDb beeProDb) {
		this.beeProDb = beeProDb;
	}

	public BeeProGenid getBeeProGenid() {
		return beeProGenid;
	}

	public void setBeeProGenid(BeeProGenid beeProGenid) {
		this.beeProGenid = beeProGenid;
	}

	public BeeProMoreTable getBeeProMoreTable() {
		return beeProMoreTable;
	}

	public void setBeeProMoreTable(BeeProMoreTable beeProMoreTable) {
		this.beeProMoreTable = beeProMoreTable;
	}

	public BeeProMultiDS getBeeProMultiDS() {
		return beeProMultiDS;
	}

	public void setBeeProMultiDS(BeeProMultiDS beeProMultiDS) {
		this.beeProMultiDS = beeProMultiDS;
	}

	public BeeProNaming getBeeProNaming() {
		return beeProNaming;
	}

	public void setBeeProNaming(BeeProNaming beeProNaming) {
		this.beeProNaming = beeProNaming;
	}

	public BeeProPearFlowerId getBeeProPearFlowerId() {
		return beeProPearFlowerId;
	}

	public void setBeeProPearFlowerId(BeeProPearFlowerId beeProPearFlowerId) {
		this.beeProPearFlowerId = beeProPearFlowerId;
	}

	public BeeProReturnStringList getBeeProReturnStringList() {
		return beeProReturnStringList;
	}

	public void setBeeProReturnStringList(BeeProReturnStringList beeProReturnStringList) {
		this.beeProReturnStringList = beeProReturnStringList;
	}

	public BeeProSelectJson getBeeProSelectJson() {
		return beeProSelectJson;
	}

	public void setBeeProSelectJson(BeeProSelectJson beeProSelectJson) {
		this.beeProSelectJson = beeProSelectJson;
	}

	public BeeProShowSql getBeeProShowSql() {
		return beeProShowSql;
	}

	public void setBeeProShowSql(BeeProShowSql beeProShowSql) {
		this.beeProShowSql = beeProShowSql;
	}

}
