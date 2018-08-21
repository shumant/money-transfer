package com.shuman.transfers.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TransferRequest {
    Long accountFromId;
    Long accountToId;
    BigDecimal amountToTransfer;
}
