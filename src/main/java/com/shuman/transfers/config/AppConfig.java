package com.shuman.transfers.config;

import com.shuman.transfers.annotation.TransactionalInterceptionService;
import com.shuman.transfers.dao.AccountDao;
import com.shuman.transfers.dao.SessionFactory;
import com.shuman.transfers.service.AccountService;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.hibernate.Session;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        packages(true, "com.shuman.tranfers");
/*        register(new AbstractContainerLifecycleListener() {
            @Override
            public void onStartup(Container container) {
                final ServiceLocator locator = container.getApplicationHandler().getServiceLocator();
                if (locator.getBestDescriptor(BuilderHelper.createContractFilter(TransactionalInterceptionService.class.getName())) == null) {
                    ServiceLocatorUtilities.addClasses(locator, TransactionalInterceptionService.class);
                }
            }
        });*/
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(AccountService.class);
                bindAsContract(AccountDao.class);

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
    }
}
