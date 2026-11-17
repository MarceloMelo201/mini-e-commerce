package com.mm.controller;

import com.mm.dtos.OrderRequest;
import com.mm.dtos.OrderResponse;
import com.mm.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(orderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findById(@PathVariable UUID orderId){
        return ResponseEntity.ok(service.findOrderById(orderId));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(service.confirmOrder(orderId));
    }

     @PostMapping("/{orderId}/release")
    public ResponseEntity<OrderResponse> releaseOrder(@PathVariable UUID orderId){
        return ResponseEntity.status(HttpStatus.OK).body(service.releaseOrder(orderId));
    }


}
