package com.techchallenge.application.usecases.interactor;

import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.exceptions.NotFoundException;
import com.techchallenge.domain.entity.Production;

public class ProductionUseCaseInteractor implements ProductionUseCase {

    private ProductionGateway productionGateway;

    public ProductionUseCaseInteractor(ProductionGateway productionGateway) {
        this.productionGateway = productionGateway;
    }

    @Override
    public Production insert(Production production) {
        return productionGateway.toReceive(production);
    }

    @Override
    public Production findById(String orderId) {
        return productionGateway.findById(orderId).orElseThrow(() -> new NotFoundException("Production not found for orderId: "+ orderId));
    }

    @Override
    public Production findAll(int page, int size) {
        return null;
    }

    @Override
    public Production preparation(String orderId) {
        Production production = findById(orderId);
        return productionGateway.updateStatusProduction(production.preparation());
    }

    @Override
    public Production ready(String orderId) {
        Production production = findById(orderId);
        return productionGateway.updateStatusProduction(production.ready());
    }

    @Override
    public Production finish(String orderId) {
        Production production = findById(orderId);
        return productionGateway.updateStatusProduction(production.finish());
    }
}
