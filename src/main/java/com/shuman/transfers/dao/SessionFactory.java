package com.shuman.transfers.dao;

import com.shuman.transfers.model.Account;
import org.glassfish.hk2.api.Factory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

/**
 * @author shuman
 * @since 20/08/2018
 */
public class SessionFactory implements Factory<Session> {
    private static final org.hibernate.SessionFactory hibernateSessionFactory;

    static {
        Properties prop = new Properties();
//        prop.setProperty("hibernate.connection.url", "jdbc:h2:mem:money-transfers");
        prop.setProperty("hibernate.connection.url", "jdbc:h2:~/test;AUTO_SERVER=TRUE");
        prop.setProperty("hibernate.connection.username", "sa");
        prop.setProperty("hibernate.connection.password", "");
        prop.setProperty("hibernate.hbm2ddl.auto", "create");
        prop.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        hibernateSessionFactory = new Configuration()
                .addPackage("com.shuman.model")
                .addProperties(prop)
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();
    }

    @Override
    public Session provide() {
        return hibernateSessionFactory.openSession();
    }

    @Override
    public void dispose(Session instance) {
        Transaction transaction = instance.getTransaction();
        if (transaction != null && transaction.isActive()) {
            transaction.commit();
        }
        instance.close();
    }
}