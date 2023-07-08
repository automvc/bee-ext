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
 */
public class AnnoAdapterDefault implements AnnoAdapter {
	
	private boolean jakarta=false;
	private boolean xjpa=false;
	
	public AnnoAdapterDefault() {
		try {
			Class.forName("jakarta.persistence.Table"); // check
			jakarta = true;
		} catch (Exception e) {
			// ignore
		}

		try {
			Class.forName("javax.persistence.Table"); // check
			xjpa = true;
		} catch (Exception e) {
			// ignore
		}
	}

	@Override
	public boolean isPrimaryKey(Field field) {
		return field.isAnnotationPresent(PrimaryKey.class)
				|| (jakarta && field.isAnnotationPresent(jakarta.persistence.Id.class))
				|| (xjpa && field.isAnnotationPresent(javax.persistence.Id.class));
	}

	@Override
	public boolean isTable(Class<?> clazz) {
		return clazz.isAnnotationPresent(Table.class)
				|| (jakarta && clazz.isAnnotationPresent(jakarta.persistence.Table.class))
				|| (xjpa && clazz.isAnnotationPresent(javax.persistence.Table.class));
	}

	@Override
	public boolean isColumn(Field field) {
		return field.isAnnotationPresent(Column.class)
				|| (jakarta && field.isAnnotationPresent(jakarta.persistence.Column.class))
				|| (xjpa && field.isAnnotationPresent(javax.persistence.Column.class));
	}
	
	@Override
	public boolean isIgnore(Field field) {
		return field.isAnnotationPresent(Ignore.class)
				|| (jakarta && field.isAnnotationPresent(jakarta.persistence.Transient.class))
				|| (xjpa && field.isAnnotationPresent(javax.persistence.Transient.class));
	}

	@Override
	public String getValue(Field field) {

		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			return column.value();
		}

		if (jakarta && field.isAnnotationPresent(jakarta.persistence.Column.class)) {
			jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);
			return column.name();
		}

		if (xjpa && field.isAnnotationPresent(javax.persistence.Column.class)) {
			javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
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
		
		if (jakarta && clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
			jakarta.persistence.Table tab = (jakarta.persistence.Table) clazz.getAnnotation(jakarta.persistence.Table.class);
			return tab.name();
		}

		if (xjpa && clazz.isAnnotationPresent(javax.persistence.Table.class)) {
			javax.persistence.Table tab = (javax.persistence.Table) clazz.getAnnotation(javax.persistence.Table.class);
			return tab.name();
		}

		return "";
	}

}
