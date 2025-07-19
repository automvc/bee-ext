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

package org.teasoft.beex.transaction;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.teasoft.bee.osql.transaction.Tran;
import org.teasoft.bee.osql.transaction.Transaction;
import org.teasoft.bee.osql.transaction.TransactionIsolationLevel;
import org.teasoft.honey.logging.Logger;
import org.teasoft.honey.osql.core.SessionFactory;

/**
 * Tran Handler for @Tran.
 * <br>The transaction will use same connection,
 * <br>it mean that all operations of same transaction will do in same dataSource.
 * @author Kingstar
 * @since  1.17
 */
@Aspect
public class TranHandler {

	private static final String MSG = "[Bee] Tran annotation intercept in TranHandler,";

	@Around("@within(org.teasoft.bee.osql.transaction.Tran) || @annotation(org.teasoft.bee.osql.transaction.Tran)")
	public Object tranAround(ProceedingJoinPoint joinPoint) throws Throwable {

		Logger.info(MSG + " start...");

		Tran annotation = getTargetAnnotation(joinPoint);

		String readOnly = annotation.readOnly();
		TransactionIsolationLevel isolation = annotation.isolation();

		Object[] args = joinPoint.getArgs();
		Object returnValue = null;

		Transaction transaction = SessionFactory.getTransaction();
		try {
			transaction.begin();

			if (readOnly != null && !"".equals(readOnly))
				transaction.setReadOnly(Boolean.parseBoolean(readOnly));
			if (isolation != null && isolation.getLevel() != -1)
				transaction.setTransactionIsolation(isolation);

			returnValue = joinPoint.proceed(args);

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
//			Logger.warn(e.getMessage(), e);
			Logger.warn("Catch Exception in TranHandler.tranAround: "+e.getMessage());
			Logger.info(MSG + " end.");
			throw e;  //fixed  2.4.0.8
		}

		Logger.info(MSG + " end.");
		return returnValue;
	}

	private Tran getTargetAnnotation(ProceedingJoinPoint joinPoint)
			throws NoSuchMethodException {
		Tran annotation = joinPoint.getTarget().getClass().getAnnotation(Tran.class);
		if (annotation == null) {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
			annotation = method.getAnnotation(Tran.class);
		}
		return annotation;
	}

}
