package com.mm.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProductResponse(
        UUID productId,
        String name,
        String description,
        BigDecimal price,
        Integer availableQuantity,
        LocalDateTime createdAt
) {
}
