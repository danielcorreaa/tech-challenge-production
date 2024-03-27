package com.techchallenge.infrastructure.message.consumer.dto;


import com.fasterxml.jackson.annotation.JsonAlias;


import java.util.List;

public record PaymentDto(
        @JsonAlias("externalReference")
        String orderId,
        @JsonAlias("orderStatus")
        String status,

        @JsonAlias("itens")
        List<ProductDto> products

) {

        @Override
        public String toString() {
                return "PaymentDto{" +
                        "orderId='" + orderId + '\'' +
                        ", status='" + status + '\'' +
                        ", products=" + products +
                        '}';
        }
}
