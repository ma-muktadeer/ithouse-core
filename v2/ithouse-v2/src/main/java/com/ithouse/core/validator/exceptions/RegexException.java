package com.ihouse.core.message.validator.exceptions;

import java.io.Serial;

public class RegexException extends Exception{
    @Serial
    private static final long serialVersionUID = 1L;
    public RegexException(String message) {
        super(message);
    }
}
