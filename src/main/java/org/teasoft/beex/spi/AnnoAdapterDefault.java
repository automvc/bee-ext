/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.spi;

import java.lang.reflect.Field;

import org.teasoft.bee.osql.annotation.Column;
import org.teasoft.bee.osql.annotation.PrimaryKey;
import org.teasoft.bee.osql.annotation.Table;
import org.teasoft.bee.spi.AnnoAdapter;

/**
 * @author Kingstar
 * @since  1.17
 */
public class AnnoAdapterDefault implements AnnoAdapter {

	@Override
	public boolean isPrimaryKey(Field field) {
		return field.isAnnotationPresent(PrimaryKey.class)
				|| field.isAnnotationPresent(javax.persistence.Id.class);
	}

	@Override
	public boolean isTable(Class<?> clazz) {
		return clazz.isAnnotationPresent(Table.class)
				|| clazz.isAnnotationPresent(javax.persistence.Table.class);
	}

	@Override
	public boolean isColumn(Field field) {
		return field.isAnnotationPresent(Column.class)
				|| field.isAnnotationPresent(javax.persistence.Column.class);
	}

	@Override
	public String getValue(Field field) {

		if (field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			String defineColumn = column.value();
			return defineColumn;
		}

		if (field.isAnnotationPresent(javax.persistence.Column.class)) {
			javax.persistence.Column column = field
					.getAnnotation(javax.persistence.Column.class);
			String defineColumn = column.name();
			return defineColumn;
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
