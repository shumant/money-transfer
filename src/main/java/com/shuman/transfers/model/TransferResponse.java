package com.shuman.transfers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferResponse {
    private boolean success;
    private String message;
}
