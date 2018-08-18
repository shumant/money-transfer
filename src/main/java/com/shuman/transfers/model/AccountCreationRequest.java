package com.shuman.transfers.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreationRequest {
    private BigDecimal balance;
}
