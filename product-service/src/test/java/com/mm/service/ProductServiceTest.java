package com.mm.service;

import com.mm.dtos.ProductRequest;
import com.mm.dtos.ProductResponse;
import com.mm.entity.Product;
import com.mm.exception.ProductAlreadyExistsEx;
import com.mm.exception.ProductNotFoundEx;
import com.mm.mapper.ProductMapper;
import com.mm.repository.ProductRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {

        ProductRequest request = mock(ProductRequest.class);
        Product product = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);

        when(request.name()).thenReturn("Notebook");
        when(productRepository.existsByName("Notebook"))
                .thenReturn(false);

        when(mapper.toEntity(request))
                .thenReturn(product);

        when(productRepository.saveAndFlush(product))
                .thenReturn(product);

        when(mapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertSame(response, result);

        verify(productRepository).existsByName("Notebook");
        verify(mapper).toEntity(request);
        verify(productRepository).saveAndFlush(product);
        verify(mapper).toResponse(product);
    }


    @Test
    @DisplayName("Should throw exception when product already exists")
    void shouldThrowExceptionWhenProductAlreadyExists() {

        ProductRequest request = mock(ProductRequest.class);

        when(request.name()).thenReturn("Notebook");

        when(productRepository.existsByName("Notebook"))
                .thenReturn(true);

        assertThrows(
                ProductAlreadyExistsEx.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository).existsByName("Notebook");

        verify(productRepository, never())
                .saveAndFlush(any());

        verifyNoInteractions(mapper);
    }


    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {

        // Arrange
        UUID productId = UUID.randomUUID();

        Product product = mock(Product.class);
        ProductResponse response = mock(ProductResponse.class);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(mapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result =
                productService.findProductById(productId);

        assertNotNull(result);
        assertSame(response, result);

        verify(productRepository).findById(productId);
        verify(mapper).toResponse(product);
    }


    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenProductDoesNotExist() {

        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundEx.class,
                () -> productService.findProductById(productId)
        );

        verify(productRepository).findById(productId);

        verifyNoInteractions(mapper);
    }


    @Test
    @DisplayName("Should return all products")
    void shouldFindAllProducts() {

        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);

        ProductResponse response1 = mock(ProductResponse.class);
        ProductResponse response2 = mock(ProductResponse.class);

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        when(mapper.toResponse(product1))
                .thenReturn(response1);

        when(mapper.toResponse(product2))
                .thenReturn(response2);

        List<ProductResponse> result =
                productService.findAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(response1, result.get(0));
        assertSame(response2, result.get(1));

        verify(productRepository).findAll();
        verify(mapper).toResponse(product1);
        verify(mapper).toResponse(product2);
    }


    @Test
    @DisplayName("Should return empty list when there are no products")
    void shouldReturnEmptyListWhenNoProductsExist() {

        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> result =
                productService.findAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productRepository).findAll();

        verifyNoInteractions(mapper);
    }
}
