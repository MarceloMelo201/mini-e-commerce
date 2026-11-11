package com.mm.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record OrderResponse(

        UUID orderId,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String status,
        LocalDateTime createdAt

){
}
