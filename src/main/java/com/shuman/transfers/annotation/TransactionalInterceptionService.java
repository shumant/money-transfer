package com.shuman.transfers.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Slf4j
public final class TransactionalInterceptionService implements InterceptionService {

    private final List<MethodInterceptor> METHOD_INTERCEPTORS;

    @Inject
    public TransactionalInterceptionService(ServiceLocator locator) {
        TransactionalInterceptor interceptor = new TransactionalInterceptor(locator);
        locator.inject(interceptor);
        METHOD_INTERCEPTORS = Collections.singletonList(interceptor);
    }

    @Override
    public Filter getDescriptorFilter() {
        return descriptor -> {
            if(descriptor.getDescriptorType() == DescriptorType.CLASS) {
                try {
                    return Class.forName(descriptor.getImplementation()).isAnnotationPresent(Intercepted.class);
                } catch (ClassNotFoundException e) {
                    log.error("Can not filter descriptor", e);
                }
            }
            return false;
        };
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.isAnnotationPresent(Transactional.class)) {
            return METHOD_INTERCEPTORS;
        }
        return null;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}