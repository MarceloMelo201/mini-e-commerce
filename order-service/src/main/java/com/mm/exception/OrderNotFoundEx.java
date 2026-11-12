package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundEx extends RuntimeException {
    public OrderNotFoundEx(String message) {
        super("Order not found with id: " + message);
    }
}
