package com.shuman.transfers.exception;

import com.shuman.transfers.model.ExceptionResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author shuman
 * @since 20/08/2018
 */
public class LogicExceptionMapper implements ExceptionMapper<LogicException> {
    @Override
    public Response toResponse(LogicException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(new ExceptionResponse().setMessage(exception.getMessage()))
                       .type(MediaType.APPLICATION_JSON_TYPE)
                       .build();
    }
}
