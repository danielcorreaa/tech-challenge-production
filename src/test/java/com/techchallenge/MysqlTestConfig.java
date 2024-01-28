package com.techchallenge;

import com.techchallenge.config.KafkaConfig;
import com.techchallenge.core.kafka.KafkaConsumerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.produce.dto.StatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.bouncycastle.crypto.tls.CertificateURL;

import static org.mockito.Mockito.mock;


@ComponentScan(value = {"com.techchallenge"})
@EnableJpaRepositories
public class MysqlTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.orders.groupId}")
    private String groupIdOrders;

    @Value(value = "${kafka.topic.consumer.payment.groupId}")
    private String groupIdPayment;

    @Value(value = "${kafka.topic.producer.topic}")
    private String topic;

    @Primary
    @Bean
    public KafkaConfig kafkaConfig(){
        return mock(KafkaConfig.class);
    }

    @Primary
    @Bean
    public TopicProducer<StatusDto> topicProducer(){
        return mock(TopicProducer.class);
    }

}
