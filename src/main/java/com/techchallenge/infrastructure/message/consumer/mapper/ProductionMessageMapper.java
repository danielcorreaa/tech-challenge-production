package com.techchallenge.infrastructure.message.consumer.mapper;

import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductionMessageMapper {

    public Production toProduction(OrderDto orderDto){
        return new Production(orderDto.id(), orderDto.statusOrder(), toProducts(orderDto));
    }

    public Product toProduct(ProductDto productDto){
        return new Product(productDto.sku(), productDto.title(), productDto.category(), productDto.description(),
                    productDto.image());
    }

    public List<Product> toProducts(OrderDto orderDto){
        return orderDto.products().stream().map(this::toProduct).toList();
    }
}
