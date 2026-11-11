package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientQuantityEx extends RuntimeException {
    public InsufficientQuantityEx(String message) {
        super("The available quantity of the product is insufficient: " + message);
    }
}
