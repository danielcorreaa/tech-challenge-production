package com.techchallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "production")
public class ProductionEntity {

    @Id
    @Column(name = "orderId", nullable = false)
    private String orderId;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "production")
    private List<ProductEntity> products;
    private String status;

}
