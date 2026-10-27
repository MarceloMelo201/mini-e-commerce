package com.mm.mapper;

import com.mm.dtos.ProductRequest;
import com.mm.dtos.ProductResponse;
import com.mm.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request) {
        return Product
                .builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .availableQuantity(request.availableQuantity())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .availableQuantity(product.getAvailableQuantity())
                .createdAt(product.getCreatedAt())
                .build();
    }


}
