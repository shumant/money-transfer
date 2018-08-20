package com.shuman.transfers.dao;

import com.shuman.transfers.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Properties;

@Slf4j
public class AccountDao {
    @Inject
    private Session session;

    public Long save(Account account) {
        return (Long) session.save(account);
    }

    public Account getWithLock(Long id) {
        return session.get(Account.class, id, LockMode.PESSIMISTIC_WRITE);
    }

    public void update(Account account) {
        session.update(account);
    }

    public List<Account> findAll() {
        return session.createQuery("from " + Account.class.getName()).list();
    }

}