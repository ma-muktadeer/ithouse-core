package com.ithouse.core.message.exception;

import com.ithouse.core.message.AbstractMessageHeader;
import com.ithouse.core.message.GenericMessage;
import com.ithouse.core.message.interfaces.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Message<?>> handleException(Exception e) {
        logger.error("Global Exception Handler caught: ", e);

        GenericMessage<?> errorResponse = new GenericMessage<>();
        AbstractMessageHeader header = new AbstractMessageHeader();
        header.setStatus("ERROR");
        header.setErrorMsg(e.getMessage());
        header.setStatusDesc("An internal error occurred");

        errorResponse.setHeader(header);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
