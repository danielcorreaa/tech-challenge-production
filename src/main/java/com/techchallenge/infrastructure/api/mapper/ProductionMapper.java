package com.techchallenge.infrastructure.api.mapper;

import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.api.dto.ProductResponse;
import com.techchallenge.infrastructure.api.dto.ProductionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductionMapper {
    public ProductionResponse toProductionResponse(Production production) {
       return new ProductionResponse(production.getOrderId(), toProductResponseList(production.getProducts()),
               production.getStatusValue());
    }

    public ProductResponse toProductResponse(Product product){
        return new ProductResponse(product.getSku(), product.getTitle(), product.getCategory(), product.getDescription(),
                product.getImage());
    }

    public List<ProductResponse> toProductResponseList(List<Product> products){
        return products.stream().map(this::toProductResponse).toList();
    }
}
