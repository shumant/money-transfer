package com.shuman.transfers.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    Long accountFromId;
    Long accountToId;
    BigDecimal amountToTransfer;
}
