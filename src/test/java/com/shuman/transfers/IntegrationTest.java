package com.shuman.transfers;

import com.shuman.transfers.config.AppConfig;
import com.shuman.transfers.model.*;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author shuman
 * @since 21/08/2018
 */
@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
public class IntegrationTest extends JerseyTest {
    private static final String ACCOUNTS_PATH = "/accounts";
    private static final String TRANSFER_PATH =  "/transfers";

    private static final int ACCOUNTS_COUNT = 15;
    private static final int CONCURRENCY_LEVEL = 40;
    private static final Random random = new Random();


    private final int account1Id = 1;
    private final int account2Id = 2;

    @Override
    protected Application configure() {
        return new AppConfig();
    }

    @Test
    public void doTest() throws Exception {
        // populate accounts
        BigDecimal[] accounts = populateAccounts();

        // make successful transfer
        BigDecimal transferAmount = calculateAffordableAmount(accounts[account1Id - 1]);
        makeTransfer(account1Id, account2Id, transferAmount,
                     HttpStatus.OK_200, new TransferResponse(true, "Transfer was successful"));
        // keep balances change
        accounts[account1Id - 1] = accounts[account1Id - 1].subtract(transferAmount);
        accounts[account2Id - 1] = accounts[account2Id - 1].add(transferAmount);

        // verify accounts balances are ok
        verifyAccountState(new Account((long) account1Id, accounts[account1Id - 1]));
        verifyAccountState(new Account((long) account2Id, accounts[account2Id - 1]));


        // make transfer with too large amount
        BigDecimal transferOverSize = BigDecimal.valueOf(1000);
        makeTransfer(account1Id, account2Id, accounts[account1Id - 1].add(transferOverSize),
                     HttpStatus.BAD_REQUEST_400,
                     new ExceptionResponse().setMessage(String.format("Account %d does not have enough money to transfer. Balance: %s. Amount to transfer: %s",
                                                                      account1Id, accounts[account1Id - 1],
                                                                      accounts[account1Id - 1].add(transferOverSize))));

        // make transfer with negative amount
        makeTransfer(account1Id, account2Id, BigDecimal.TEN.negate(),
                     HttpStatus.BAD_REQUEST_400,
                     new ExceptionResponse().setMessage("Can not transfer negative money amount"));

        testMultithreadedEnvironment(accounts);
    }

    private void testMultithreadedEnvironment(BigDecimal[] accounts) throws Exception {
        // prepare
        List<Callable<Object>> transferTasks = new ArrayList<>();

        // make random transfers in one thread and collect transfer tasks
        for (int i = 0; i < CONCURRENCY_LEVEL; i++) {
            // random accounts IDs
            int accountFrom = ThreadLocalRandom.current().nextInt(1, ACCOUNTS_COUNT + 1);
            int accountTo = accountFrom;
            while (accountTo == accountFrom) {
                accountTo = ThreadLocalRandom.current().nextInt(1, ACCOUNTS_COUNT + 1);
            }

            // random transfer amount
            BigDecimal transferAmount = calculateAffordableAmount(accounts[accountFrom - 1]);

            // make single-threaded transfer
            accounts[accountFrom - 1] = accounts[accountFrom - 1].subtract(transferAmount);
            accounts[accountTo - 1] = accounts[accountTo - 1].add(transferAmount);

            // keep task for multithreaded environment
            transferTasks.add(new TransferTask(accountFrom, accountTo, transferAmount));
        }

        // invoke tasks asynchronously
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        List<Future<Object>> futures = executorService.invokeAll(transferTasks);
        for (Future<Object> future : futures) {
            future.get(); // this will throw Exception if anything goes wrong in async task
        }

        // verify accounts state
        for (int i = 1; i <= ACCOUNTS_COUNT; i++) {
            verifyAccountState(new Account((long) i, accounts[i - 1]));
        }
    }

    private BigDecimal[] populateAccounts() {
        BigDecimal[] accounts = new BigDecimal[ACCOUNTS_COUNT];

        for (int i = 0; i < accounts.length; i++) {
            BigDecimal balance = setScale(new BigDecimal(100_000 + random.nextDouble() * 100_000));
            createAccount(balance, (long) i + 1);
            accounts[i] = balance;
        }

        return accounts;
    }

    private Account createAccount(BigDecimal balance, Long expectedId) {
        AccountCreationRequest requestBody = new AccountCreationRequest()
                .setBalance(balance);

        Response response = target(ACCOUNTS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE))
                .invoke();

        Account createdAccount = new Account(setScale(balance))
                .setId(expectedId);

        Assert.assertEquals("wrong response body", createdAccount, response.readEntity(Account.class));
        Assert.assertEquals("wrong status code", HttpStatus.OK_200, response.getStatus());

        return createdAccount;
    }

    private void makeTransfer(int fromId, int toId, BigDecimal amount, int expectedStatus, Object expectedResponse) {
        TransferRequest requestBody = new TransferRequest()
                .setAccountFromId((long) fromId)
                .setAccountToId((long) toId)
                .setAmountToTransfer(amount);

        Response response = target(TRANSFER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE))
                .invoke();

        Assert.assertEquals("wrong response body", expectedResponse, response.readEntity(expectedResponse.getClass()));
        Assert.assertEquals("wrong status code", expectedStatus, response.getStatus());
    }

    private BigDecimal calculateAffordableAmount(BigDecimal balance) {
        return setScale(balance.divide(BigDecimal.valueOf(CONCURRENCY_LEVEL)));
    }

    private BigDecimal setScale(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_DOWN);
    }

    private void verifyAccountState(Account account) {
        Response response = target(ACCOUNTS_PATH)
                .path("/" + account.getId())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        Assert.assertEquals("wrong status code", HttpStatus.OK_200, response.getStatus());
        Assert.assertEquals("wrong account state", account, response.readEntity(Account.class));
    }

    private class TransferTask implements Callable<Object> {
        private int accountFrom;
        private int accountTo;
        private BigDecimal amount;

        public TransferTask(int accountFrom, int accountTo, BigDecimal amount) {
            this.accountFrom = accountFrom;
            this.accountTo = accountTo;
            this.amount = amount;
        }

        @Override
        public Object call() throws Exception {
            makeTransfer(accountFrom, accountTo, amount,
                         HttpStatus.OK_200, new TransferResponse(true, "Transfer was successful"));
            return null;
        }
    }

}
