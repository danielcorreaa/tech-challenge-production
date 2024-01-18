package com.techchallenge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@ComponentScan(value = {"com.techchallenge"})
@EnableJpaRepositories
public class KafkaTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.orders.groupId}")
    private String groupIdOrders;

    @Value(value = "${kafka.topic.consumer.payment.groupId}")
    private String groupIdPayment;

    @Value(value = "${kafka.topic.producer.topic}")
    private String topic;



}
