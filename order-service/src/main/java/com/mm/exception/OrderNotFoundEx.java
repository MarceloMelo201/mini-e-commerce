package com.mm.exception;

public class OrderNotFoundEx extends RuntimeException {
    public OrderNotFoundEx(String message) {
        super("Order not found with id: " + message);
    }
}
