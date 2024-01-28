package com.techchallenge.infrastructure.message.consumer.dto;


import java.util.List;

public record OrderDto (String id, List<ProductDto> products, String statusOrder ) {



}
