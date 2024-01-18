package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.application.usecases.interactor.ProductionUseCaseInteractor;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.gateways.ProductionRepositoryGateway;
import com.techchallenge.infrastructure.gateways.StatusOutboxRepositoryGateway;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import com.techchallenge.infrastructure.persistence.mapper.ProductionEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.StatusEntityOutboxMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import com.techchallenge.infrastructure.persistence.repository.StatusEntityOutboxRespository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderConsumerTest {

    OrderConsumer orderConsumer;

    private ProductionUseCase productionUseCase;

    private ProductionMessageMapper productionMessageMapper;

    private ProductionGateway productionGateway;

    JsonUtils jsonUtils;

    @Mock
    private ProductionRepository productionRepository;

    @Mock
    private ProductRepository productRepository;

    private StatusOutboxGateway statusOutboxGateway;
    private ProductionEntityMapper productionEntityMapper;

    @Mock
    private StatusEntityOutboxRespository statusEntityOutboxRespository;
    private StatusEntityOutboxMapper statusEntityOutboxMapper;


    @BeforeEach
    void start(){
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        statusEntityOutboxMapper = new StatusEntityOutboxMapper();
        statusOutboxGateway = new StatusOutboxRepositoryGateway(statusEntityOutboxRespository, statusEntityOutboxMapper);
        productionEntityMapper = new ProductionEntityMapper();
        productionGateway = new ProductionRepositoryGateway(productionRepository, productRepository, statusOutboxGateway, productionEntityMapper);
        productionUseCase = new ProductionUseCaseInteractor(productionGateway);
        productionMessageMapper = new ProductionMessageMapper();
        orderConsumer = new OrderConsumer(productionUseCase, productionMessageMapper);
    }


    @Test
    void testListenOrder_withSucess() throws InterruptedException {
       OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/order.json"), OrderDto.class).get();
       Production production = productionMessageMapper.toProduction(orderDto);
       ProductionEntity entity = productionEntityMapper.toProductionEntity(production);
       when(productionRepository.save(any(ProductionEntity.class))).thenReturn(entity);
       when(productRepository.saveAll(anyList())).thenReturn(entity.getProducts());
       Acknowledgment ack = spy(Acknowledgment.class);

       orderConsumer.listenOrder(orderDto, ack);


       boolean messageConsumed = orderConsumer.getLatch().await(10, TimeUnit.SECONDS);
       assertTrue(messageConsumed);
       verify(ack, times(1)).acknowledge();
       verify(productionRepository, times(1)).save(any());
       verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testListenOrder_withError() throws InterruptedException {
        OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/orderError.json"), OrderDto.class).get();
        Acknowledgment ack = spy(Acknowledgment.class);
        orderConsumer.listenOrder(orderDto, ack);
        boolean messageConsumed = orderConsumer.getLatch().await(10, TimeUnit.SECONDS);
        assertFalse(messageConsumed);
        verify(ack, never()).acknowledge();
        verify(productionRepository, never()).save(any());
        verify(productRepository, never()).saveAll(anyList());
    }

}