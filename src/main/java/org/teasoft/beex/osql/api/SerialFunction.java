/*
 * Copyright 2019-2024 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
 */

package org.teasoft.beex.osql.api;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Kingstar
 * @since  2.0
 */
@FunctionalInterface
public interface SerialFunction<T, R> extends Function<T, R>, Serializable {

}
