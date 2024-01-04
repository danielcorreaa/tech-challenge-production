package com.techchallenge.infrastructure.message.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.MysqlTestConfig;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.utils.MockObject;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MysqlTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class PaymentConsumerTest {

    PaymentConsumer consumer;

    @Autowired
    ProductionUseCase productionUseCase;

    @Autowired
    StatusOutboxGateway statusOutboxGateway;

    @Autowired
    ProductionMessageMapper mapper;

    JsonUtils jsonUtils;


    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:latest"));

    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);

    }
    @BeforeAll
    static void setUp(){
        mySQLContainer.withReuse(true);
        mySQLContainer.start();
        kafkaContainer.withReuse(true);
        kafkaContainer.start();
    }
    @AfterAll
    static void setDown(){
        mySQLContainer.stop();
        kafkaContainer.stop();
    }

    @BeforeEach
    void start(){
        ObjectMapper objectMapper = new ObjectMapper();
        jsonUtils = new JsonUtils(objectMapper);
        consumer = new PaymentConsumer(productionUseCase, mapper);
        List<Production> response = new MockObject().getProductions();
        response.stream().forEach(productionUseCase::insert);
    }


    @Test
    void testListenPayment(){
        PaymentDto message = new PaymentDto("10998", StatusOrder.EM_PREPARACAO.getValue());

        consumer.listenPayment(message);

        Production response = productionUseCase.findById("10998");
        List<StatusOutbox> byNotSend = statusOutboxGateway.findByNotSend();

        assertEquals("EM_PREPARACAO", response.getStatusValue());

        assertEquals("10998", byNotSend.get(0).getOrderId());
        assertFalse(byNotSend.get(0).getSend());
        assertEquals("EM_PREPARACAO", byNotSend.get(0).getStatus());
    }

}