package com.mm.entity;

import com.mm.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stock_reservation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reservationId;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID orderId;

    @JoinColumn(nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    public StockReservation setStatus(ReservationStatus status) {
        this.status = status;
        return this;
    }
}
