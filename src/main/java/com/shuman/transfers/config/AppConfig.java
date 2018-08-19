package com.shuman.transfers.config;

import com.shuman.transfers.dao.AccountDao;
import com.shuman.transfers.service.AccountService;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages(true, "com.shuman.tranfers");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(AccountService.class);
                bindAsContract(AccountDao.class);
            }
        });
    }
}
