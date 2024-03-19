package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Log4j2
public class PaymentConsumer {

    private ProductionUseCase productionUseCase;
    private ProductionMessageMapper mapper;

    private CountDownLatch latch = new CountDownLatch(1);

    public PaymentConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper) {
        this.productionUseCase = productionUseCase;
        this.mapper = mapper;
    }

   @KafkaListener(topics = "${kafka.topic.consumer.payment.topic}", groupId = "${kafka.topic.consumer.payment.groupId}",
            containerFactory = "kafkaListenerContainerFactoryPaymentDto")
    public void listenPayment(PaymentDto message, Acknowledgment ack) {
        log.info("Received Message Payment: {}" , message);
        try {

            Production production = new Production(message.orderId(),
                    StatusOrder.EM_PREPARACAO.name(),
                    mapper.toProducts(message.products()));

            productionUseCase.insert(production);
            ack.acknowledge();
            latch.countDown();
        }catch (Exception ex){
            log.error("Message not process: "+ ex.getMessage());
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
