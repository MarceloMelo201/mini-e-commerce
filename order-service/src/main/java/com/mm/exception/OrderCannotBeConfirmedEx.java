package com.mm.exception;

public class OrderCannotBeConfirmedEx extends RuntimeException {
    public OrderCannotBeConfirmedEx(String message) {
        super("This order cannot be confirmed: " + message);
    }
}
