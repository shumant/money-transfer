package com.shuman.transfers.config;

import com.shuman.transfers.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;
import java.util.Properties;

@Singleton
@Slf4j
public class AccountDao {
    private SessionFactory sessionFactory;

    @PostConstruct
    private void init() {
        Properties prop = new Properties();
        prop.setProperty("hibernate.connection.url", "jdbc:h2:mem:");
        prop.setProperty("hibernate.connection.username", "sa");
        prop.setProperty("hibernate.connection.password", "");
        prop.setProperty("dialect", "org.hibernate.dialect.H2Dialect");

        sessionFactory = new Configuration()
                .addPackage("com.shuman.model")
                .addProperties(prop)
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();
    }

    public Long save(Account account) {
        return (Long) sessionFactory.getCurrentSession().save(account);
    }

    public Account getWithLock(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Account.class, id, LockMode.PESSIMISTIC_WRITE);
    }

    public void update(Account account) {
        Session session = sessionFactory.getCurrentSession();
        session.update(account);
    }

    public List<Account> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from " + Account.class.getName()).list();
    }

}