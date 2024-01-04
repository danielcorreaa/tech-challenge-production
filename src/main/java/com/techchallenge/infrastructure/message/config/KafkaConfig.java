package com.techchallenge.infrastructure.message.config;

import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.infrastructure.gateways.dto.StatusDto;
import com.techchallenge.infrastructure.message.consumer.OrderConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.message.gateways.MessageStatusGateway;
import com.techchallenge.infrastructure.message.produce.StatusProduce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Bean
    public KafkaConsumer kafkaConsumerConfig(){
        return new KafkaConsumer(bootstrapAddress, groupId);
    }

    @Bean
    public ConsumerFactory<String, OrderDto> consumerFactoryOrderDto(){
        return kafkaConsumerConfig().consumerFactory(jsonDeserializer(new JsonDeserializer<>(OrderDto.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> kafkaListenerContainerFactoryOrder(){
        return kafkaConsumerConfig().kafkaListenerContainerFactory(consumerFactoryOrderDto());
    }

    @Bean
    public OrderConsumer orderConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper){
        return new OrderConsumer(productionUseCase,mapper);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentDto> kafkaListenerContainerFactoryPayment(){
        return kafkaConsumerConfig().kafkaListenerContainerFactory(consumerFactoryPaymentDto());
    }
    @Bean
    public ConsumerFactory<String, PaymentDto> consumerFactoryPaymentDto(){
        return kafkaConsumerConfig().consumerFactory(jsonDeserializer(new JsonDeserializer<>(PaymentDto.class)));
    }

    //@Bean
    public <T> JsonDeserializer<T> jsonDeserializer(JsonDeserializer jsonDeserializer){
        JsonDeserializer<T> deserializer = jsonDeserializer;
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
    @Bean
    public KafkaProducer kafkaProducer(){
        return new KafkaProducer(bootstrapAddress);
    }

    @Bean
    public ProducerFactory<String, StatusDto> producerFactory(){
        return kafkaProducer().producerFactory();
    }

    @Bean
    public KafkaTemplate<String, StatusDto> kafkaTemplate() {
        return kafkaProducer().kafkaTemplate();
    }

    @Bean
    public MessageStatusGateway messageStatusGateway(){
        return new MessageStatusGateway(kafkaTemplate());
    }
    @Bean
    public StatusProduce statusProduce(StatusOutboxGateway statusOutboxGateway){
        return new StatusProduce(statusOutboxGateway, messageStatusGateway());
    }

}
