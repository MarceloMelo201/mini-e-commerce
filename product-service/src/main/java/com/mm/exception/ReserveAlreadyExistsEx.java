package com.mm.exception;

public class ReserveAlreadyExistsEx extends RuntimeException {
    public ReserveAlreadyExistsEx() {
        super("Reserve already exists.");
    }
}
