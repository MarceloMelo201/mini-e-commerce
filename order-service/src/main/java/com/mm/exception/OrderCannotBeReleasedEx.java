package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrderCannotBeReleasedEx extends RuntimeException {
    public OrderCannotBeReleasedEx(String message) {
        super("Order cannot be released: " + message);
    }
}
