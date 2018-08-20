package com.shuman.transfers.service;

import com.shuman.transfers.dao.AccountDao;
import com.shuman.transfers.exception.LogicException;
import com.shuman.transfers.model.Account;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Transactional(rollbackOn = {Throwable.class})
public class AccountService {
    @Inject
    private AccountDao accountDao;

    public Account createAccount(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new LogicException("Can not create an Account with negative balance");
        }

        Long id = accountDao.save(new Account(balance));
        return new Account(id, balance);
    }

    public List<Account> getAccounts () {
        return accountDao.findAll();
    }

    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new LogicException("Can not transfer negative money amount");
        }

        Account accountFrom, accountTo;

        // always fetch first account with lower ID, so we do not get deadlock because of Pessimistic write
        if (fromId < toId) {
            accountFrom = accountDao.getWithLock(fromId);
            accountTo = accountDao.getWithLock(toId);
        } else if (toId > fromId) {
            accountTo = accountDao.getWithLock(toId);
            accountFrom = accountDao.getWithLock(fromId);
        } else {
            throw new LogicException("Provided bank accounts IDs are the same");
        }

        verifyTransfer(accountFrom, accountTo, fromId, toId, amount);

        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));

        accountDao.save(accountFrom);
        accountDao.save(accountTo);
    }

    private void verifyTransfer (Account from, Account to, Long fromId, Long toId, BigDecimal amount) {
        if (from == null) {
            throw new LogicException("There is no account with ID {}", fromId);
        }
        if (to == null) {
            throw new LogicException("There is no account with ID {}", toId);
        }
        if (from.getBalance() == null) {
            throw new LogicException("There is no info on account {} balance", fromId);
        }
        if (to.getBalance() == null) {
            throw new LogicException("There is no info on account {} balance", toId);
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new LogicException("Account {} does not have enough money to transfer. Balance: {}. Amount to transfer: {}",
                                     from.getId(), from.getBalance(), amount);
        }
    }
}
