package com.mm.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockRequest(

        @NotNull(message = "The order id cannot be null.")
        UUID orderId,

        @Min(value = 1, message = "The minimum quantity is 1.")
        @NotNull(message = "The quantity cannot be null.")
        Integer quantity
) {
}
