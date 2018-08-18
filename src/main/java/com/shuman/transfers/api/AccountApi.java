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
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Account createAccount(AccountCreationRequest request) {
        if (request == null || request.getBalance() == null) {
            throw new LogicException("Please specify initial balance");
        }

        return accountService.createAccount(request.getBalance());
    }

    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransferResponse transferMoney(TransferRequest request) {
        if (request == null) {
            throw new LogicException("No transfer request found");
        }

        if (request.getAccountFromId() == null) {
            throw new LogicException("Please specify account from ID");
        }
        if (request.getAccountToId() == null) {
            throw new LogicException("Please specify account to ID");
        }
        if (request.getAmountToTransfer() == null) {
            throw new LogicException("Please amount to transfer");
        }

        accountService.transferMoney(request.getAccountFromId(), request.getAccountToId(), request.getAmountToTransfer());
        return new TransferResponse(true, "Transfer was successful");
    }

}
