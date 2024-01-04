package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
public class OrderConsumer {

    private ProductionUseCase productionUseCase;
    private ProductionMessageMapper mapper;

    public OrderConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper) {
        this.productionUseCase = productionUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topic.consumer.orders}", groupId = "${kafka.topic.consumer.groupId}",
            containerFactory = "kafkaListenerContainerFactoryOrder")
    public void listenOrder(OrderDto message) {
        log.info("Received Message in order: " + message.toString());
        Production production = mapper.toProduction(message);
        productionUseCase.insert(production);
    }

}
