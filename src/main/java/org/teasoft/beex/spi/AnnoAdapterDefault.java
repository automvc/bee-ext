/*
 * Copyright 2020-2022 the original author.All rights reserved.
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

package org.teasoft.beex.spi;

import java.lang.reflect.Field;

import org.teasoft.bee.osql.annotation.Column;
import org.teasoft.bee.osql.annotation.Ignore;
import org.teasoft.bee.osql.annotation.PrimaryKey;
import org.teasoft.bee.osql.annotation.Table;
import org.teasoft.bee.spi.AnnoAdapter;

/**
 * @author Kingstar
 * @since  1.17
 * @since  1.17.21
 */
public class AnnoAdapterDefault implements AnnoAdapter {

	@Override
	public boolean isPrimaryKey(Field field) {
		return field.isAnnotationPresent(PrimaryKey.class)
				|| field.isAnnotationPresent(jakarta.persistence.Id.class)
				|| field.isAnnotationPresent(javax.persistence.Id.class);
	}

	@Override
	public boolean isTable(Class<?> clazz) {
		return clazz.isAnnotationPresent(Table.class)
				|| clazz.isAnnotationPresent(jakarta.persistence.Table.class)
				|| clazz.isAnnotationPresent(javax.persistence.Table.class);
	}

	@Override
	public boolean isColumn(Field field) {
		return field.isAnnotationPresent(Column.class)
				|| field.isAnnotationPresent(jakarta.persistence.Column.class)
				|| field.isAnnotationPresent(javax.persistence.Column.class);
	}

	@Override
	public boolean isIgnore(Field field) {
		return field.isAnnotationPresent(Ignore.class)
				|| field.isAnnotationPresent(jakarta.persistence.Transient.class)
				|| field.isAnnotationPresent(javax.persistence.Transient.class);
	}

	@Override
	public String getValue(Field field) {

		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			return column.value();
		}

		if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
			jakarta.persistence.Column column = field
					.getAnnotation(jakarta.persistence.Column.class);
			return column.name();
		}

		if (field.isAnnotationPresent(javax.persistence.Column.class)) {
			javax.persistence.Column column = field
					.getAnnotation(javax.persistence.Column.class);
			return column.name();
		}

		return "";
	}

	@Override
	public String getValue(Class<?> clazz) {

		if (clazz.isAnnotationPresent(Table.class)) {
			Table tab = (Table) clazz.getAnnotation(Table.class);
			return tab.value();
		}

		if (clazz.isAnnotationPresent(javax.persistence.Table.class)) {
			javax.persistence.Table tab = (javax.persistence.Table) clazz
					.getAnnotation(javax.persistence.Table.class);
			return tab.name();
		}

		return "";
	}

}
