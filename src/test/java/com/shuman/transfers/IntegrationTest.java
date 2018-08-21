package com.shuman.transfers;

import com.shuman.transfers.config.AppConfig;
import com.shuman.transfers.model.Account;
import com.shuman.transfers.model.AccountCreationRequest;
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

/**
 * @author shuman
 * @since 21/08/2018
 */
public class IntegrationTest extends JerseyTest {
    private final static String ACCOUNTS_PATH = "/accounts";
    private final static String TRANSFER_PATH = ACCOUNTS_PATH + "/transfer";

    private final static long account1Id = 1L;
    private final BigDecimal account1Balance = BigDecimal.valueOf(10000.23);

    private final static long account2Id = 2L;
    private final BigDecimal account2Balance = BigDecimal.valueOf(932.23932);

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new AppConfig();
    }

    @Test
    public void doTest() {
        createAccount(account1Balance, account1Id);
        createAccount(account2Balance, account2Id);



    }

    private void createAccount(BigDecimal balance, Long expectedId) {
        AccountCreationRequest requestBody = new AccountCreationRequest()
                .setBalance(balance);

        Response response = target(ACCOUNTS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE))
                .invoke();

        Account createdAccount = new Account(account1Balance)
                .setId(expectedId);

        Assert.assertEquals("wrong status code", HttpStatus.OK_200, response.getStatus());
        Assert.assertEquals("wrong response body", createdAccount, response.readEntity(Account.class));
    }
}
