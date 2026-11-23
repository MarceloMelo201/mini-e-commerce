package com.mm.repository;

import com.mm.entity.StockReservation;
import com.mm.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockReservation, UUID> {


    List<StockReservation> findAllByProduct_ProductIdAndStatus(
            UUID productId,
            ReservationStatus status
    );

    boolean existsByOrderId(UUID orderId);
}
