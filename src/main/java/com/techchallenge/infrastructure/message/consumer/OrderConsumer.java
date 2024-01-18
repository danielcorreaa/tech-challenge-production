package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class OrderConsumer {

    private ProductionUseCase productionUseCase;
    private ProductionMessageMapper mapper;

    private CountDownLatch latch = new CountDownLatch(1);

    public OrderConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper) {
        this.productionUseCase = productionUseCase;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topic.consumer.orders.topic}", groupId = "${kafka.topic.consumer.orders.groupId}",
            containerFactory = "kafkaListenerContainerFactoryOrderDto")
    public void listenOrder(OrderDto orderDto, Acknowledgment ack) {
        log.info("Received Message in orderDto: " + orderDto.toString());
        try {
            Production production = mapper.toProduction(orderDto);
            productionUseCase.insert(production);
            ack.acknowledge();
            latch.countDown();
            System.out.println("N deveria aparecer");
        } catch (Exception ex){
            log.error("Message not process: "+ ex.getMessage());
        }

    }

    public CountDownLatch getLatch() {
        return latch;
    }

}
