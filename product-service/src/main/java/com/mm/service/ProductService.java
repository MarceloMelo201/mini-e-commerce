package com.mm.service;

import com.mm.dtos.ProductRequest;
import com.mm.dtos.ProductResponse;
import com.mm.dtos.StockRequest;
import com.mm.dtos.StockResponse;
import com.mm.entity.Product;
import com.mm.entity.StockReservation;
import com.mm.enums.ReservationStatus;
import com.mm.exception.InsufficientQuantityEx;
import com.mm.exception.ProductAlreadyExistsEx;
import com.mm.exception.ProductNotFoundEx;
import com.mm.exception.ReservationNotFoundEx;
import com.mm.mapper.ProductMapper;
import com.mm.repository.ProductRepository;
import com.mm.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, StockRepository stockRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.mapper = mapper;
    }

    //criar produto
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.name()))
            throw new ProductAlreadyExistsEx();

        Product product = mapper.toEntity(request);

        Product result = productRepository.saveAndFlush(product);

        return mapper.toResponse(result);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(UUID productId) {
        Product result = productRepository.findById(productId).orElseThrow(ProductNotFoundEx::new);
        return mapper.toResponse(result);
    }


    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAll().stream().map(mapper::toResponse).toList();
    }

}
