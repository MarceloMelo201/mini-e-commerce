package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundEx extends RuntimeException {
    public ProductNotFoundEx(String message ) {
        super("Product not found with id: " + message);
    }
}
