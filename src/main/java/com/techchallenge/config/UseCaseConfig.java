package com.techchallenge.config;

import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.application.usecases.interactor.ProductionUseCaseInteractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public ProductionUseCase productionUseCase(ProductionGateway productionGateway) {
		return new ProductionUseCaseInteractor(productionGateway);
	}

}
