package com.shuman.transfers.exception;


import javax.ws.rs.WebApplicationException;

public class LogicException extends WebApplicationException {
    public LogicException(String message, Object... args) {
        super(String.format(message, args));
    }
}