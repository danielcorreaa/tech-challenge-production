package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
public class PaymentConsumer {

    private ProductionUseCase productionUseCase;
    private ProductionMessageMapper mapper;

    public PaymentConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper) {
        this.productionUseCase = productionUseCase;
        this.mapper = mapper;
    }

   @KafkaListener(topics = "${kafka.topic.consumer.payment}", groupId = "${kafka.topic.consumer.paymentId}",
            containerFactory = "kafkaListenerContainerFactoryPayment")
    public void listenPayment(PaymentDto message) {
        log.info("Received Message Payment: " + message.toString());
        if(message.status().equals(StatusOrder.EM_PREPARACAO.getValue())) {
            productionUseCase.preparation(message.orderId());
        }
    }
}
