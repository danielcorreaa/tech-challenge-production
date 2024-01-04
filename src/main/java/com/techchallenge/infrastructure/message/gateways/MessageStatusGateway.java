package com.techchallenge.infrastructure.message.gateways;

import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.infrastructure.gateways.dto.StatusDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


public class MessageStatusGateway implements MessageGateway {

    private KafkaTemplate<String, StatusDto> produce;

    public MessageStatusGateway(KafkaTemplate<String, StatusDto> produce) {
        this.produce = produce;
    }

    @Override
    public void send(String orderId, String status) {
        produce.send("", new StatusDto(orderId, status));
    }
}
