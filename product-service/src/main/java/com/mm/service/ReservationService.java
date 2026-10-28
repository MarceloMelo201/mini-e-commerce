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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ReservationService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public ReservationService(ProductRepository productRepository, StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public StockResponse createReserve(UUID productId, StockRequest request) {
        Product product = productRepository.findByProductIdForUpdate(productId).orElseThrow(ProductNotFoundEx::new);

        int reservationQuantityStock = stockRepository.findAllByProduct_ProductIdAndStatus(productId, ReservationStatus.ACTIVE)
                .stream()
                .mapToInt(StockReservation::getQuantity)
                .sum();

        if (reservationQuantityStock + request.quantity() > product.getAvailableQuantity())
            throw new InsufficientQuantityEx();

        StockReservation reservation = StockReservation
                .builder()
                .orderId(request.orderId())
                .product(product)
                .quantity(request.quantity())
                .status(ReservationStatus.ACTIVE)
                .build();

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        var result = stockRepository.saveAndFlush(reservation);

        return new StockResponse(
                result.getReservationId(),
                result.getProduct().getProductId(),
                result.getQuantity(),
                totalPrice,
                result.getStatus().toString()
        );
    }

    @Transactional
    public void releaseReservation(UUID reservationId) {
        StockReservation reservation = stockRepository.findById(reservationId).orElseThrow(ReservationNotFoundEx::new);
        stockRepository.save(reservation.setStatus(ReservationStatus.RELEASED));
    }
}
