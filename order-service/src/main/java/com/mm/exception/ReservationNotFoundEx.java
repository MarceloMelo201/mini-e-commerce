package com.mm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReservationNotFoundEx extends RuntimeException {
    public ReservationNotFoundEx(String message) {
        super("Reservation not found: " + message);
    }
}
