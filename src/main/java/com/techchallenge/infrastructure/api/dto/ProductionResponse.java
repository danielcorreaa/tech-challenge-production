package com.techchallenge.infrastructure.api.dto;


import java.util.List;

public record ProductionResponse(String orderId, List<ProductResponse> products, String statusOrder) {
}
