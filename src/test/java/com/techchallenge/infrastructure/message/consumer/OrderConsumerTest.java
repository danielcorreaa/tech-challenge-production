package com.techchallenge.infrastructure.message.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.MysqlTestConfig;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.infrastructure.message.consumer.OrderConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.utils.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MysqlTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class OrderConsumerTest {

    OrderConsumer orderConsumer;

    @Autowired
    private ProductionUseCase productionUseCase;

    @Autowired
    private ProductionMessageMapper mapper;

    JsonUtils jsonUtils;

    @Autowired
    StatusOutboxGateway statusOutboxGateway;


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
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jsonUtils = new JsonUtils(objectMapper);
        orderConsumer = new OrderConsumer(productionUseCase, mapper);
    }


    @Test
    void testListenOrder(){
       Optional<OrderDto> orderDto = jsonUtils.parse(new FileUtils().getFile("/data/orders.json"), OrderDto.class);
       orderConsumer.listenOrder(orderDto.orElse(null));
       productionUseCase.findById("6593732dcfdb826a875770ff");

       Production response = productionUseCase.findById("6593732dcfdb826a875770ff");
       List<StatusOutbox> byNotSend = statusOutboxGateway.findByNotSend();

       assertEquals("RECEBIDO", response.getStatusValue());
       assertEquals(0, byNotSend.size());
    }



}