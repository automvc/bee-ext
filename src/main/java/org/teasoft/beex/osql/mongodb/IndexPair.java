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

/**
 * @author Kingstar
 * @since  2.1
 */
import com.mongodb.client.model.IndexOptions;

public class IndexPair {

	private String fieldName;
	private IndexType indexType;
	private IndexOptions indexOptions;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public IndexType getIndexType() {
		return indexType;
	}

	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}

	public IndexOptions getIndexOptions() {
		return indexOptions;
	}

	public void setIndexOptions(IndexOptions indexOptions) {
		this.indexOptions = indexOptions;
	}

}
