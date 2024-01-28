package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.infrastructure.persistence.entity.ProductEntity;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import com.techchallenge.infrastructure.persistence.mapper.ProductionEntityMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class ProductionRepositoryGateway implements ProductionGateway {

    private ProductionRepository productionRepository;
    private ProductRepository productRepository;
    private StatusOutboxGateway statusOutboxGateway;
    private ProductionEntityMapper mapper;

    public ProductionRepositoryGateway(ProductionRepository productionRepository, ProductRepository productRepository,
                                       StatusOutboxGateway statusOutboxGateway, ProductionEntityMapper mapper) {
        this.productionRepository = productionRepository;
        this.productRepository = productRepository;
        this.statusOutboxGateway = statusOutboxGateway;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public Production toReceive(Production production) {
        ProductionEntity productionEntity = mapper.toProductionEntity(production);
        productionEntity = productionRepository.save(productionEntity);
        var products = mapper.toProductEntities(production, productionEntity);
        List<ProductEntity> productEntities = productRepository.saveAll(products);
        productionEntity.setProducts(productEntities);

        return mapper.toProduction(productionEntity);
    }

    @Transactional
    @Override
    public Production updateStatusProduction(Production production) {
        productionRepository.save(mapper.toProductionEntity(production));
        statusOutboxGateway.insert(new StatusOutbox(production.getOrderId(), production.getStatusValue()));
        return production;
    }

    @Override
    public Optional<Production> findById(String orderId) {
        var production =  productionRepository.findById(orderId);
        return production.map( pron -> mapper.toProduction(pron));
    }

    @Override
    public Production findAll(int page, int size) {
        return null;
    }
}
