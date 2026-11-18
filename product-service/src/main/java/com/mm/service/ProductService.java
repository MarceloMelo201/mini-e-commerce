package com.mm.service;

import com.mm.dtos.ProductRequest;
import com.mm.dtos.ProductResponse;
import com.mm.entity.Product;
import com.mm.exception.ProductAlreadyExistsEx;
import com.mm.exception.ProductNotFoundEx;
import com.mm.mapper.ProductMapper;
import com.mm.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepository, ProductMapper mapper) {
        this.productRepository = productRepository;
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
