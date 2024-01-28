package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.persistence.entity.ProductEntity;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ProductionEntityMapper {

    public List<ProductEntity> toProductEntities(Production production, ProductionEntity entity) {
       return production.getProducts().stream().map(product ->
                ProductEntity.builder()
                        .id(getIdProduct(production.getOrderId(), product.getSku()))
                        .sku(product.getSku())
                        .image(product.getImage())
                        .description(product.getDescription())
                        .title(product.getTitle())
                        .category(product.getCategory())
                        .production(entity)
                        .build()
        ).toList();
    }

    private String getIdProduct(String orderId, String sku) {
        return orderId +"--"+ sku;
    }


    public ProductionEntity toProductionEntity(Production production) {
        return ProductionEntity.builder().status(production.getStatus().toString())
                .orderId(production.getOrderId())
                .products(toProductEntities(production))
                .build();
    }

    public Production toProduction(ProductionEntity productionEntity) {
        return new Production(productionEntity.getOrderId(), productionEntity.getStatus(),
                toProducts(productionEntity.getProducts()));
    }

    public Product toProduct(ProductEntity productEntity){
        return new Product(productEntity.getSku(), productEntity.getTitle(), productEntity.getCategory(),productEntity.getDescription(), productEntity.getImage());
    }

    public List<Product> toProducts(List<ProductEntity> productEntities){
        return Optional.ofNullable(productEntities).orElse(Collections.emptyList())
                .stream().map(this::toProduct).toList();
    }

    public List<ProductEntity> toProductEntities(Production production) {
        return production.getProducts().stream().map(product ->
                ProductEntity.builder()
                        .sku(product.getSku())
                        .image(product.getImage())
                        .description(product.getDescription())
                        .title(product.getTitle())
                        .category(product.getCategory())
                        .build()
        ).toList();
    }



}
