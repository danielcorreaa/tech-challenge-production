package com.techchallenge;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.infrastructure.message.config.KafkaConfig;
import com.techchallenge.infrastructure.message.config.KafkaConsumer;
import com.techchallenge.infrastructure.message.consumer.OrderConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import org.mockito.Mockito;
import org.mockito.configuration.IMockitoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(value = {"com.techchallenge"})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@EnableJpaRepositories
public class MysqlTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Primary
    @Bean
    public KafkaConfig kafkaConfig(){
        return mock(KafkaConfig.class);
    }



}
