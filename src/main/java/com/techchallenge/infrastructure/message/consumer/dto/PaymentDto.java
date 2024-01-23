package com.techchallenge.infrastructure.message.consumer.dto;


import com.fasterxml.jackson.annotation.JsonAlias;

public record PaymentDto(
        @JsonAlias("externalReference")
        String orderId,
        @JsonAlias("orderStatus")
        String status) {
}
