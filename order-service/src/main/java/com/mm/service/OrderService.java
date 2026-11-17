package com.mm.service;

import com.mm.client.ProductServiceClient;
import com.mm.dtos.OrderRequest;
import com.mm.dtos.OrderResponse;
import com.mm.dtos.StockRequest;
import com.mm.dtos.StockResponse;
import com.mm.entity.Order;
import com.mm.enums.OrderStatus;
import com.mm.exception.*;
import com.mm.mapper.OrderMapper;
import com.mm.repository.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final ProductServiceClient client;

    public OrderService(OrderRepository repository, OrderMapper mapper, ProductServiceClient client) {
        this.repository = repository;
        this.mapper = mapper;
        this.client = client;
    }


    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        UUID orderId = UUID.randomUUID();
        StockRequest stockRequest = new StockRequest(orderId, request.quantity());
        StockResponse response = createStock(request.productId(), stockRequest);

        Order order = Order
                .builder()
                .orderId(orderId)
                .productId(response.productId())
                .reservationId(response.reservationId())
                .quantity(response.quantity())
                .unitPrice(response.unitPrice())
                .totalPrice(response.totalPrice())
                .orderStatus(OrderStatus.PENDING)
                .build();

        Order result = repository.saveAndFlush(order);
        return mapper.toOrderResponse(result);
    }

    private StockResponse createStock(UUID productId, StockRequest request) {
        try {
            return client.createReserve(productId, request);

        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundEx(productId.toString());

        } catch (FeignException.Conflict e) {
            throw new InsufficientQuantityEx(productId.toString());
        }
    }


    @Transactional
    public OrderResponse confirmOrder(UUID orderId) {
        Order result = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundEx(orderId.toString()));

        if(!result.getOrderStatus().equals(OrderStatus.PENDING))
            throw new OrderCannotBeConfirmedEx(orderId.toString());

        result.setOrderStatus(OrderStatus.CONFIRMED);
        return mapper.toOrderResponse(result);
    }


    @Transactional(readOnly = true)
    public OrderResponse findOrderById(UUID orderId) {
        return repository
                .findById(orderId)
                .map(mapper::toOrderResponse)
                .orElseThrow(()-> new OrderNotFoundEx(orderId.toString()));
    }

    @Transactional
    public OrderResponse releaseOrder(UUID orderId) {
        Order result = repository.findById(orderId).orElseThrow(() -> new OrderNotFoundEx(orderId.toString()));

        if(!result.getOrderStatus().equals(OrderStatus.PENDING))
            throw new OrderCannotBeReleasedEx(orderId.toString());

        result.setOrderStatus(OrderStatus.RELEASED);
        releaseReserve(result.getReservationId());
        return mapper.toOrderResponse(result);
    }

    private void releaseReserve(UUID reservationId) {
        try {
            client.releaseReserve(reservationId);

        } catch (FeignException.NotFound e) {
            throw new ReservationNotFoundEx(reservationId.toString());
        }
    }
}


