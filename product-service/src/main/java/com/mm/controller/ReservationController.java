package com.mm.controller;

import com.mm.dtos.StockRequest;
import com.mm.dtos.StockResponse;
import com.mm.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    @PostMapping("/products/{productId}/reserve")
    public ResponseEntity<StockResponse> createReserve(@PathVariable UUID productId, @Valid @RequestBody StockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReserve(productId, request));
    }

    @PostMapping("/reservations/{reservationId}/release")
    public ResponseEntity<?> releaseReservation(@PathVariable UUID reservationId) {
        reservationService.releaseReservation(reservationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
