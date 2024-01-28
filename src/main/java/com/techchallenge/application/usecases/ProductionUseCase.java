package com.techchallenge.application.usecases;

import com.techchallenge.domain.entity.Production;

public interface ProductionUseCase {

    Production insert(Production production);

    Production findById(String orderId);
    Production findAll(int page, int size);


    Production preparation(String orderId);

    Production ready(String orderId);

    Production finish(String orderId);
}
