package com.shuman.transfers.config;

import com.shuman.transfers.annotation.TransactionalInterceptionService;
import com.shuman.transfers.dao.AccountDao;
import com.shuman.transfers.dao.SessionFactory;
import com.shuman.transfers.exception.LogicExceptionMapper;
import com.shuman.transfers.service.AccountService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.hibernate.Session;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages(true, "com.shuman.tranfers");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(AccountService.class)
                        .to(AccountService.class)
                        .in(Singleton.class);
                bind(AccountDao.class)
                        .to(AccountDao.class)
                        .in(Singleton.class);

                bind(TransactionalInterceptionService.class)
                        .to(InterceptionService.class)
                        .in(Singleton.class);

                bindFactory(SessionFactory.class)
                        .proxy(true)
                        .proxyForSameScope(false)
                        .to(Session.class)
                        .in(RequestScoped.class);
            }
        });

        register(JacksonFeature.class);
        register(LogicExceptionMapper.class);
    }
}
