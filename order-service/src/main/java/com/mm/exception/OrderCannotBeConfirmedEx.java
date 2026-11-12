package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrderCannotBeConfirmedEx extends RuntimeException {
    public OrderCannotBeConfirmedEx(String message) {
        super("This order cannot be confirmed: " + message);
    }
}
