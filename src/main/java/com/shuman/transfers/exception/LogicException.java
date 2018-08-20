package com.shuman.transfers.exception;


public class LogicException extends RuntimeException {
    public LogicException(String message, Object... args) {
        super(String.format(message, args));
    }
}
