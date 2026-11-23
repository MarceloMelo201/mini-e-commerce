package com.mm.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "The name cannot be blank.")
        String name,

        @NotBlank(message = "The description cannot be blank.")
        String description,

        @NotNull(message = "The price cannot be null.")
        @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount.")
        @PositiveOrZero(message = "Price must be positive or zero.")
        BigDecimal price,

        @NotNull(message = "Available quantity cannot be null.")
        @Positive(message = "Available quantity must be positive.")
        Integer availableQuantity
) {
}
