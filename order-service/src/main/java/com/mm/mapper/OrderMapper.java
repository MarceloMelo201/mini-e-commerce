package com.mm.mapper;

import com.mm.dtos.OrderResponse;
import com.mm.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    
    public OrderResponse toOrderResponse(Order order){
        return OrderResponse
                .builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .reservationId(order.getReservationId())
                .quantity(order.getQuantity())
                .unitPrice(order.getUnitPrice())
                .totalPrice(order.getTotalPrice())
                .status(order.getOrderStatus().toString())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
