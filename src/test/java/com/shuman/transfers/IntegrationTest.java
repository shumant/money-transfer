package com.shuman.transfers;

import com.shuman.transfers.config.AppConfig;
import com.shuman.transfers.model.*;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author shuman
 * @since 21/08/2018
 */
@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
public class IntegrationTest extends JerseyTest {
    private final static String ACCOUNTS_PATH = "/accounts";
    private final static String TRANSFER_PATH = ACCOUNTS_PATH + "/transfer";

    private final static long account1Id = 1L;
    private final BigDecimal account1Balance = BigDecimal.valueOf(10000.23);

    private final static long account2Id = 2L;
    private final BigDecimal account2Balance = BigDecimal.valueOf(932.23932);

    private final BigDecimal transfer1Value = account1Balance.subtract(BigDecimal.valueOf(2423.34));
    private final BigDecimal transfer2Value = BigDecimal.valueOf(2423932999.34);

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new AppConfig();
    }

    @Test
    public void doTest() {
        Account account1 = createAccount(account1Balance, account1Id);
        Account account2 = createAccount(account2Balance, account2Id);

        makePayment(account1Id, account2Id, transfer1Value,
                    HttpStatus.OK_200, new TransferResponse(true, "Transfer was successful"));

        makePayment(account1Id, account2Id, transfer2Value,
                    HttpStatus.BAD_REQUEST_400,
                    new ExceptionResponse().setMessage(String.format("Account %d does not have enough money to transfer. Balance: %s. Amount to transfer: %s",
                                                                     account1Id, account1Balance.subtract(transfer1Value),
                                                                     transfer2Value)));

        account1.setBalance(setScale(account1Balance.subtract(transfer1Value)));
        account2.setBalance(setScale(account2Balance.add(transfer1Value)));
        verifyAccountState(account1);
        verifyAccountState(account2);

        makePayment(account1Id, account2Id, BigDecimal.TEN.negate(),
                    HttpStatus.BAD_REQUEST_400,
                    new ExceptionResponse().setMessage("Can not transfer negative money amount"));
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

        Assert.assertEquals("wrong status code", HttpStatus.OK_200, response.getStatus());
        Assert.assertEquals("wrong response body", createdAccount, response.readEntity(Account.class));

        return createdAccount;
    }

    private void makePayment(Long fromId, Long toId, BigDecimal amount, int expectedStatus, Object expectedResponse) {
        TransferRequest requestBody = new TransferRequest()
                .setAccountFromId(fromId)
                .setAccountToId(toId)
                .setAmountToTransfer(amount);

        Response response = target(TRANSFER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE))
                .invoke();

        Assert.assertEquals("wrong status code", expectedStatus, response.getStatus());
        Assert.assertEquals("wrong response body", expectedResponse, response.readEntity(expectedResponse.getClass()));
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

}
