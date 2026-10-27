package com.mm.exception;

public class ProductAlreadyExistsEx extends RuntimeException {
    public ProductAlreadyExistsEx() {
        super("Product already exists.");
    }
}
