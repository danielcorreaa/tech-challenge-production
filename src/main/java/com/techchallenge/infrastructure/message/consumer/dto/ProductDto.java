package com.techchallenge.infrastructure.message.consumer.dto;


import com.fasterxml.jackson.annotation.JsonAlias;

public record ProductDto(
        @JsonAlias("skuNumber")
        String sku, String title, String category, String description, String image) {



}
