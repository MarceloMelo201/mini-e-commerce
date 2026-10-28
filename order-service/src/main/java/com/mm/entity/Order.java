package com.mm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Column(nullable = false, updatable = false)
    private UUID productId;

    @Column(nullable = false, updatable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false, updatable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
