package com.shuman.transfers.api;

import com.shuman.transfers.exception.LogicException;
import com.shuman.transfers.model.Account;
import com.shuman.transfers.model.AccountCreationRequest;
import com.shuman.transfers.model.TransferRequest;
import com.shuman.transfers.model.TransferResponse;
import com.shuman.transfers.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
public class AccountApi {
    @Inject
    private AccountService accountService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountById(@PathParam("id") Long id) {
        return accountService.getAccountById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(AccountCreationRequest request) {
        if (request == null || request.getBalance() == null) {
            throw new LogicException("Please specify initial balance");
        }

        return accountService.createAccount(request.getBalance());
    }
}
