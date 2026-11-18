
package com.mm.service;

import com.mm.dtos.StockRequest;
import com.mm.dtos.StockResponse;
import com.mm.entity.Product;
import com.mm.entity.StockReservation;
import com.mm.enums.ReservationStatus;
import com.mm.exception.InsufficientQuantityEx;
import com.mm.exception.ProductNotFoundEx;
import com.mm.exception.ReservationNotFoundEx;
import com.mm.repository.ProductRepository;
import com.mm.repository.StockRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private ReservationService reservationService;


    // CREATE RESERVATION

    @Test
    @DisplayName("Should create reservation successfully")
    void shouldCreateReservation() {

        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Product product = mock(Product.class);

        StockRequest request = new StockRequest(orderId, 2);

        when(productRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(product));

        when(stockRepository.findAllByProduct_ProductIdAndStatus(
                productId, ReservationStatus.ACTIVE))
                .thenReturn(List.of());

        when(product.getAvailableQuantity()).thenReturn(10);
        when(product.getPrice()).thenReturn(new BigDecimal("50.00"));
        when(product.getProductId()).thenReturn(productId);

        when(stockRepository.saveAndFlush(any(StockReservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockResponse result =
                reservationService.createReserve(productId, request);

        assertNotNull(result);

        ArgumentCaptor<StockReservation> captor =
                ArgumentCaptor.forClass(StockReservation.class);

        verify(stockRepository).saveAndFlush(captor.capture());

        StockReservation savedReservation = captor.getValue();

        assertEquals(orderId, savedReservation.getOrderId());
        assertEquals(2, savedReservation.getQuantity());
        assertEquals(ReservationStatus.ACTIVE, savedReservation.getStatus());
        assertSame(product, savedReservation.getProduct());
    }


    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenProductDoesNotExist() {

        UUID productId = UUID.randomUUID();

        StockRequest request =
                new StockRequest(UUID.randomUUID(), 2);

        when(productRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundEx.class,
                () -> reservationService.createReserve(productId, request)
        );

        verify(productRepository).findByProductIdForUpdate(productId);

        verifyNoInteractions(stockRepository);
    }


    @Test
    @DisplayName("Should throw exception when available stock is insufficient")
    void shouldThrowExceptionWhenStockIsInsufficient() {

        UUID productId = UUID.randomUUID();

        Product product = mock(Product.class);

        StockRequest request =
                new StockRequest(UUID.randomUUID(), 5);

        StockReservation existingReservation =
                mock(StockReservation.class);

        when(productRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(product));

        when(stockRepository.findAllByProduct_ProductIdAndStatus(
                productId, ReservationStatus.ACTIVE))
                .thenReturn(List.of(existingReservation));

        when(existingReservation.getQuantity()).thenReturn(6);
        when(product.getAvailableQuantity()).thenReturn(10);

        assertThrows(
                InsufficientQuantityEx.class,
                () -> reservationService.createReserve(productId, request)
        );

        verify(stockRepository, never())
                .saveAndFlush(any(StockReservation.class));
    }


    @Test
    @DisplayName("Should allow reservation when quantity equals available stock")
    void shouldAllowReservationWhenQuantityEqualsAvailableStock() {

        UUID productId = UUID.randomUUID();

        Product product = mock(Product.class);

        StockRequest request =
                new StockRequest(UUID.randomUUID(), 4);

        when(productRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(product));

        when(stockRepository.findAllByProduct_ProductIdAndStatus(
                productId, ReservationStatus.ACTIVE))
                .thenReturn(List.of());

        when(product.getAvailableQuantity()).thenReturn(4);
        when(product.getPrice()).thenReturn(new BigDecimal("25.00"));
        when(product.getProductId()).thenReturn(productId);

        when(stockRepository.saveAndFlush(any(StockReservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        StockResponse result =
                reservationService.createReserve(productId, request);

        assertNotNull(result);

        verify(stockRepository).saveAndFlush(any(StockReservation.class));
    }



    @Test
    @DisplayName("Should release reservation successfully")
    void shouldReleaseReservation() {

        UUID reservationId = UUID.randomUUID();

        StockReservation reservation =
                mock(StockReservation.class);

        when(stockRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        when(reservation.setStatus(ReservationStatus.RELEASED))
                .thenReturn(reservation);

        reservationService.releaseReservation(reservationId);

        verify(reservation).setStatus(ReservationStatus.RELEASED);
        verify(stockRepository).save(reservation);
    }


    @Test
    @DisplayName("Should throw exception when reservation does not exist")
    void shouldThrowExceptionWhenReservationDoesNotExist() {

        UUID reservationId = UUID.randomUUID();

        when(stockRepository.findById(reservationId))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundEx.class,
                () -> reservationService.releaseReservation(reservationId)
        );

        verify(stockRepository, never())
                .save(any(StockReservation.class));
    }
}
