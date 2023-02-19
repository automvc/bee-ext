/*
 * Copyright 2020-2023 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
//import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author Kingstar
 * @since  2.0
 */
public class FieldNameUtil {

	@FunctionalInterface
	public interface SerialFunction<T, R> extends Function<T, R>, Serializable {

	}

	@SafeVarargs
	public static <T> String[] getFieldNames(SerialFunction<T, ?>... fns) {
		String strs[] = new String[fns.length];
		int i = 0;
		for (SerialFunction<T, ?> fn : fns) {
			strs[i++] = getFieldName(fn);
		}
		return strs;
	}

	public static <T> String getFieldName(SerialFunction<T, ?> fn) {

		SerializedLambda serializedLambda = getSerializedLambda(fn);
		String fieldName = "";
		String methodName = serializedLambda.getImplMethodName();
		if (methodName.startsWith("get")) {
			fieldName = methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			fieldName = methodName.substring(2);
		} else if (methodName.startsWith("set")) {
			fieldName = methodName.substring(2);
		} else {
			fieldName = methodName;
		}

		fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "",
				(fieldName.charAt(0) + "").toLowerCase());
		return fieldName;
	}

	private static <T> SerializedLambda getSerializedLambda(SerialFunction<T, ?> fn) {
		Method method;
		try {
			method = fn.getClass().getDeclaredMethod("writeReplace");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		// 从序列化方法取出序列化的lambda信息
		boolean isAccessible = method.isAccessible();
		method.setAccessible(true);
		SerializedLambda serializedLambda;
		try {
			serializedLambda = (SerializedLambda) method.invoke(fn);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		method.setAccessible(isAccessible);
		return serializedLambda;
	}
}