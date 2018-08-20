package com.shuman.transfers.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author shuman
 * @since 20/08/2018
 */
@Data
@Accessors(chain = true)
public class ExceptionResponse {
    private boolean success = false;
    private String message;
}
