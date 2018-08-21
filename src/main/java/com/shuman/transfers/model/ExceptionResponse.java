package com.shuman.transfers.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author shuman
 * @since 20/08/2018
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ExceptionResponse {
    private String message;
    private boolean success = false;
}
