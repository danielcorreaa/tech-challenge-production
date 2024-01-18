package com.techchallenge.infrastructure.message.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.KafkaTestConfig;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class OrderConsumerIT {

    @Autowired
    OrderConsumer orderConsumer;

    @Autowired
    private ProductionUseCase productionUseCase;

    @Autowired
    private ProductionMessageMapper mapper;

    JsonUtils jsonUtils;


    @Value(value = "${kafka.topic.consumer.orders.topic}")
    String topic;

    @Autowired
    ProductionRepository productionRepository;

    @Autowired
    ProductRepository productRepository;


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
        //orderConsumer = new OrderConsumer(productionUseCase, mapper);
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        cleandb();
        System.out.println(orderConsumer.getLatch().getCount());
    }

    @Test
    void testListenOrder_withSuccess() throws InterruptedException {
        OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/order.json"), OrderDto.class).get();
        topicProducer().produce(orderDto);
        boolean messageConsumed = orderConsumer.getLatch().await(10, TimeUnit.SECONDS);
        Production production = productionUseCase.findById("6593732dcfdb826a875770ff");

        assertEquals("6593732dcfdb826a875770ff",production.getOrderId());
        assertEquals("RECEBIDO", production.getStatusValue());
        assertEquals(1, production.getProducts().size());
        assertEquals("6584ab97a00f0a786bf3698f", production.getProducts().get(0).getSku());
        assertTrue(messageConsumed);
    }

    @Test
    void testListenOrder_withError() throws InterruptedException {
        OrderDto orderDto = jsonUtils.parse(new FileUtils().getFile("/data/orderError.json"), OrderDto.class).get();
        topicProducer().produce(orderDto);
        boolean messageConsumed = orderConsumer.getLatch().getCount() == 0;
        assert(messageConsumed);
        try {
            Production production = productionUseCase.findById("6593732dcfdb826a875770ff");
            fail("Orders can't be exists");
        }catch (Exception ex){
            assertEquals("Production not found for orderId: 6593732dcfdb826a875770ff",ex.getMessage());
        }
    }

    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(kafkaContainer.getBootstrapServers());
    }


    public ProducerFactory<String, OrderDto> producerFactory(){
        return kafkaProducer().producerFactory();
    }

    @Bean
    public KafkaTemplate<String, OrderDto> kafkaTemplate() {
        return kafkaProducer().kafkaTemplate();
    }

    @Bean
    public TopicProducer<OrderDto> topicProducer(){
        return new TopicProducer<>(kafkaTemplate(), topic);
    }
    private void cleandb() {
        productRepository.deleteAll();
        productionRepository.deleteAll();
    }



}