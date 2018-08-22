package com.shuman.transfers.api;

import com.shuman.transfers.exception.LogicException;
import com.shuman.transfers.model.TransferRequest;
import com.shuman.transfers.model.TransferResponse;
import com.shuman.transfers.service.TransferService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/transfers")
public class TransferApi {
    @Inject
    private TransferService transferService;

    @POST
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

        transferService.makeTransfer(request.getAccountFromId(), request.getAccountToId(), request.getAmountToTransfer());
        return new TransferResponse(true, "Transfer was successful");
    }
}
