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
    public List<Account> getAccounts () {
        return accountDao.findAll();
    }

    @Transactional
    public void transferMoney(Long fromId, Long toId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new LogicException("Can not transfer negative money amount");
        }

        amount = setScale(amount);

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

        accountFrom.setBalance(setScale(accountFrom.getBalance().subtract(amount)));
        accountTo.setBalance(setScale(accountTo.getBalance().add(amount)));

        accountDao.update(accountFrom);
        accountDao.update(accountTo);
    }

    private void verifyTransfer (Account from, Account to, Long fromId, Long toId, BigDecimal amount) {
        if (from == null) {
            throw new LogicException("There is no account with ID %d", fromId);
        }
        if (to == null) {
            throw new LogicException("There is no account with ID %d", toId);
        }
        if (from.getBalance() == null) {
            throw new LogicException("There is no info on account %d balance", fromId);
        }
        if (to.getBalance() == null) {
            throw new LogicException("There is no info on account %d balance", toId);
        }

        if (from.getBalance().compareTo(amount) < 0) {
            throw new LogicException("Account %d does not have enough money to transfer. Balance: %d. Amount to transfer: %d",
                                     from.getId(), String.valueOf(from.getBalance()), String.valueOf(amount));
        }
    }

    private BigDecimal setScale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_DOWN);
    }
}
