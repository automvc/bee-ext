/*
 * Copyright 2016-2022 the original author.All rights reserved.
 * Kingstar(honeysoft@126.com)
 * The license,see the LICENSE file.
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
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.core.SessionFactory;

/**
 * @author Kingstar
 * @since 1.17
 */
@Aspect
public class TranHandler {
	
	private static final String MSG = "[Bee] Tran annotation intercept in TranHandler,";

	@Around("@annotation(org.teasoft.bee.osql.transaction.Tran)")
	public Object tranAround(ProceedingJoinPoint joinPoint) throws Throwable {

		Logger.info(MSG + " start......");
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		Tran annotation = method.getAnnotation(Tran.class);
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
			Logger.warn(e.getMessage(), e);
		}

		Logger.info(MSG + " end......");
		return returnValue;
	}

}
