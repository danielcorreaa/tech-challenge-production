package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.application.gateway.MessageGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.domain.entity.StatusOutbox;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Log4j2
@EnableScheduling
public class StatusProduce {

    private static final long SECOND = 1000;
    private static final long MINUTE = SECOND * 60;

    private StatusOutboxGateway statusOutboxGateway;

    private MessageGateway messageGateway;

    public StatusProduce(StatusOutboxGateway statusOutboxGateway, MessageGateway messageGateway) {
        this.statusOutboxGateway = statusOutboxGateway;
        this.messageGateway = messageGateway;
    }

    @Scheduled(fixedDelay = MINUTE)
    public void send(){
        List<StatusOutbox> toSend =  statusOutboxGateway.findByNotSend();
        if(toSend.isEmpty()){
            log.info("No message found for send");
        }
        toSend.forEach(send -> {
            messageGateway.send(send.getOrderId(), send.getStatus());
            send.send();
            statusOutboxGateway.insert(send);
            log.info("Message send with success! orderId - {} - status - {} ", send.getOrderId(), send.getStatus());
        });
    }
}
