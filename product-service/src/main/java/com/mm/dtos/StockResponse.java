package com.mm.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record StockResponse(
        UUID reservationId,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String status
) {
}
