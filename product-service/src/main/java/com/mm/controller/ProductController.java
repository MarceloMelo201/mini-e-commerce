package com.mm.controller;

import com.mm.dtos.ProductRequest;
import com.mm.dtos.ProductResponse;
import com.mm.dtos.StockRequest;
import com.mm.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.findProductById(productId));
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

}
