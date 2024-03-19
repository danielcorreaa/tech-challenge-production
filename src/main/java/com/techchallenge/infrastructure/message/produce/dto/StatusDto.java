package com.techchallenge.infrastructure.message.produce.dto;

import com.techchallenge.infrastructure.message.consumer.dto.ProductDto;

import java.util.List;

public record StatusDto(String orderId, String status) {
}
