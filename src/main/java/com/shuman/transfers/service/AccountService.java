package com.shuman.transfers.service;

import com.shuman.transfers.annotation.Transactional;
import com.shuman.transfers.dao.AccountDao;
import com.shuman.transfers.exception.LogicException;
import com.shuman.transfers.model.Account;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
@Intercepted
public class AccountService {
    @Inject
    private AccountDao accountDao;

    @Transactional
    public Account createAccount(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new LogicException("Can not create an Account with negative balance");
        }

        balance = setScale(balance);

        Long id = accountDao.save(new Account(balance));
        return new Account(id, balance);
    }

    @Transactional
    public List<Account> getAccounts() {
        return accountDao.findAll();
    }

    @Transactional
    public Account getAccountById(Long id) {
        return accountDao.findById(id);
    }

    private BigDecimal setScale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_DOWN);
    }
}
