package com.shuman.transfers.exception;

import java.text.MessageFormat;

public class LogicException extends RuntimeException {
    public LogicException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }
}
