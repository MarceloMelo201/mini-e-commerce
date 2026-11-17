package com.mm.client;

import com.mm.dtos.StockRequest;
import com.mm.dtos.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(url = "http://localhost:8081", name = "product-service")
public interface ProductServiceClient {

    @PostMapping("/internal/products/{productId}/reserve")
    StockResponse createReserve(@PathVariable("productId") UUID productId, @RequestBody StockRequest request);

    @PostMapping("/internal/reservations/{reservationId}/release")
    void releaseReserve(@PathVariable("reservationId") UUID reservationId);
}
