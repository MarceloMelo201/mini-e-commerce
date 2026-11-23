package com.mm.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequest(

        @NotNull(message = "The product id cannot be null.")
        UUID productId,

        @Min(value = 1, message = "The quantity cannot be negative or zero.")
        @NotNull(message = "The quantity cannot be null.")
        Integer quantity

) {
}
