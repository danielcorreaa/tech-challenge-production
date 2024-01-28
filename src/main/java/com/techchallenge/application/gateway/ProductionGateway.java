package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.Production;

import java.util.Optional;

public interface ProductionGateway {

    Production toReceive(Production production);
    Production updateStatusProduction(Production production);

    Optional<Production> findById(String orderId);
    Production findAll(int page, int size);
}
