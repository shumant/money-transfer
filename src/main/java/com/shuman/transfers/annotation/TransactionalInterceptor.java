package com.shuman.transfers.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.ServiceLocator;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author shuman
 * @since 20/08/2018
 */
@Slf4j
public class TransactionalInterceptor implements MethodInterceptor {
    private final ServiceLocator locator;

    TransactionalInterceptor(ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Session session = locator.getService(Session.class);
        Transaction transaction = session.beginTransaction();
        boolean success = false;
        try {
            Object result = methodInvocation.proceed();
            success = true;
            return result;
        } finally {
            if (transaction.isActive()) {
                if (success && !transaction.getRollbackOnly()) {
                    transaction.commit();
                } else {
                    try {
                        transaction.rollback();
                    } catch (Exception e) {
                        log.error("Exception while transaction rollback!", e);
                    }
                }
            }
        }
    }
}
