package com.techchallenge.config;

import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.kafka.KafkaConsumerConfig;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.KafkaTopic;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.infrastructure.message.consumer.PaymentConsumer;
import com.techchallenge.infrastructure.message.produce.dto.StatusDto;
import com.techchallenge.infrastructure.message.consumer.OrderConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.message.produce.StatusProduce;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.orders.groupId}")
    private String groupIdOrders;

    @Value(value = "${kafka.topic.consumer.payment.groupId}")
    private String groupIdPayment;

    @Value(value = "${kafka.topic.producer.status}")
    private String topic;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfigOrders(){
        return new KafkaConsumerConfig(bootstrapAddress, groupIdOrders);
    }

    @Bean
    public ConsumerFactory<String, OrderDto> consumerFactoryOrderDto(){
        return kafkaConsumerConfigOrders().consumerFactory(jsonDeserializer(new JsonDeserializer<>(OrderDto.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> kafkaListenerContainerFactoryOrderDto(){
        return kafkaConsumerConfigOrders().kafkaListenerContainerFactory(consumerFactoryOrderDto());
    }

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfigPayment(){
        return new KafkaConsumerConfig(bootstrapAddress, groupIdPayment);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentDto> kafkaListenerContainerFactoryPaymentDto(){
        return kafkaConsumerConfigPayment().kafkaListenerContainerFactory(consumerFactoryPaymentDto());
    }
    @Bean
    public ConsumerFactory<String, PaymentDto> consumerFactoryPaymentDto(){
        return kafkaConsumerConfigPayment().consumerFactory(jsonDeserializer(new JsonDeserializer<>(PaymentDto.class)));
    }

    public <T> JsonDeserializer<T> jsonDeserializer(JsonDeserializer<T> deserializer){
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
    @Bean
    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(bootstrapAddress);
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
    public TopicProducer<StatusDto> topicProducer(){
        return new TopicProducer<>(kafkaTemplate(), topic);
    }
    @Bean
    public KafkaTopic kafkaTopic(){
        return new KafkaTopic(bootstrapAddress, topic);
    }

    @Bean
    public NewTopic newTopic(){
        return kafkaTopic().createTopic(3, (short) 1);
    }

    @Bean
    public OrderConsumer orderConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper){
        return new OrderConsumer(productionUseCase,mapper);
    }

    @Bean
    public PaymentConsumer paymentConsumer(ProductionUseCase productionUseCase, ProductionMessageMapper mapper){
        return new PaymentConsumer(productionUseCase, mapper);
    }





}
