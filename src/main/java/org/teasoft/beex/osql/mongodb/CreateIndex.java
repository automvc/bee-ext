/*
 * Copyright 2020-2023 the original author.All rights reserved.
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

package org.teasoft.beex.osql.mongodb;

import java.util.List;

/**
 * Create index and drop indexes.
 * @author Kingstar
 * @since  2.1
 * for Sharding 
 * @since  2.5.0
 */
public interface CreateIndex {

	public String index(String collectionName, String fieldName, IndexType indexType);

	public String unique(String collectionName, String fieldName, IndexType indexType);

	public List<String> indexes(String collectionName, List<IndexPair> indexes);
	
	/**
	 * Drop all the indexes on this collection, except for the default on _id.
	 * Include index,unique.
	 * @param collectionName collection name(table name)
	 */
	public void dropIndexes(String collectionName);  
}