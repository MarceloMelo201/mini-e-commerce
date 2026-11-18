
package com.mm.service;

import com.mm.client.ProductServiceClient;
import com.mm.dtos.*;
import com.mm.entity.Order;
import com.mm.enums.OrderStatus;
import com.mm.exception.*;
import com.mm.mapper.OrderMapper;
import com.mm.repository.OrderRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private ProductServiceClient client;

    @InjectMocks
    private OrderService service;


    @Test
    void shouldCreateOrder() {
        UUID productId = UUID.randomUUID();
        OrderRequest request = mock(OrderRequest.class);
        StockResponse stock = mock(StockResponse.class);
        OrderResponse response = mock(OrderResponse.class);

        when(request.productId()).thenReturn(productId);
        when(request.quantity()).thenReturn(2);
        when(client.createReserve(eq(productId), any(StockRequest.class)))
                .thenReturn(stock);
        when(repository.saveAndFlush(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toOrderResponse(any(Order.class))).thenReturn(response);

        when(stock.productId()).thenReturn(productId);
        when(stock.reservationId()).thenReturn(UUID.randomUUID());
        when(stock.quantity()).thenReturn(2);
        when(stock.unitPrice()).thenReturn(new java.math.BigDecimal("50"));
        when(stock.totalPrice()).thenReturn(new java.math.BigDecimal("100"));

        assertNotNull(service.createOrder(request));

        verify(client).createReserve(eq(productId), any(StockRequest.class));
        verify(repository).saveAndFlush(any(Order.class));
    }


    @Test
    void shouldThrowWhenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        OrderRequest request = mock(OrderRequest.class);

        when(request.productId()).thenReturn(productId);
        when(request.quantity()).thenReturn(1);
        when(client.createReserve(eq(productId), any(StockRequest.class)))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThrows(ProductNotFoundEx.class,
                () -> service.createOrder(request));

        verify(repository, never()).saveAndFlush(any(Order.class));
    }


    @Test
    void shouldThrowWhenStockIsInsufficient() {
        UUID productId = UUID.randomUUID();
        OrderRequest request = mock(OrderRequest.class);

        when(request.productId()).thenReturn(productId);
        when(request.quantity()).thenReturn(1);
        when(client.createReserve(eq(productId), any(StockRequest.class)))
                .thenThrow(mock(FeignException.Conflict.class));

        assertThrows(InsufficientQuantityEx.class,
                () -> service.createOrder(request));

        verify(repository, never()).saveAndFlush(any(Order.class));
    }


    @Test
    void shouldConfirmOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);
        OrderResponse response = mock(OrderResponse.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(mapper.toOrderResponse(order)).thenReturn(response);

        assertNotNull(service.confirmOrder(orderId));

        verify(order).setOrderStatus(OrderStatus.CONFIRMED);
    }

    @Test
    void shouldThrowWhenOrderDoesNotExistOnConfirmation() {
        UUID orderId = UUID.randomUUID();

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundEx.class,
                () -> service.confirmOrder(orderId));
    }

    @Test
    void shouldThrowWhenOrderCannotBeConfirmed() {
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        assertThrows(OrderCannotBeConfirmedEx.class,
                () -> service.confirmOrder(orderId));

        verify(order, never()).setOrderStatus(any());
    }

    // FIND ORDER

    @Test
    void shouldFindOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(mapper.toOrderResponse(order)).thenReturn(mock(OrderResponse.class));

        assertNotNull(service.findOrderById(orderId));
    }

    @Test
    void shouldThrowWhenOrderDoesNotExistOnSearch() {
        UUID orderId = UUID.randomUUID();

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundEx.class,
                () -> service.findOrderById(orderId));
    }

    // RELEASE ORDER

    @Test
    void shouldReleaseOrder() {
        UUID orderId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        Order order = mock(Order.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(order.getReservationId()).thenReturn(reservationId);
        when(mapper.toOrderResponse(order)).thenReturn(mock(OrderResponse.class));

        assertNotNull(service.releaseOrder(orderId));

        verify(order).setOrderStatus(OrderStatus.RELEASED);
        verify(client).releaseReserve(reservationId);
    }

    @Test
    void shouldThrowWhenOrderCannotBeReleased() {
        UUID orderId = UUID.randomUUID();
        Order order = mock(Order.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        assertThrows(OrderCannotBeReleasedEx.class,
                () -> service.releaseOrder(orderId));

        verifyNoInteractions(client);
    }

    @Test
    void shouldThrowWhenReservationDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        UUID reservationId = UUID.randomUUID();
        Order order = mock(Order.class);

        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(order.getReservationId()).thenReturn(reservationId);

        doThrow(mock(FeignException.NotFound.class))
                .when(client).releaseReserve(reservationId);

        assertThrows(ReservationNotFoundEx.class,
                () -> service.releaseOrder(orderId));
    }
}
