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
			fieldName = methodName.substring(3); //2.2
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
		method.setAccessible(true); // NOSONAR
		SerializedLambda serializedLambda;
		try {
			serializedLambda = (SerializedLambda) method.invoke(fn);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		method.setAccessible(isAccessible); // NOSONAR
		return serializedLambda;
	}
}
