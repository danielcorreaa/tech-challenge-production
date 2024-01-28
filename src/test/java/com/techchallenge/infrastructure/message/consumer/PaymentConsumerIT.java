package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.KafkaTestConfig;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import com.techchallenge.utils.ProductionHelper;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {KafkaTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class PaymentConsumerIT {

    @Autowired
    PaymentConsumer paymentConsumer;

    @Autowired
    ProductionUseCase productionUseCase;

    @Autowired
    StatusOutboxGateway statusOutboxGateway;

    @Autowired
    ProductionMessageMapper mapper;

    JsonUtils jsonUtils;

    @Autowired
    ProductionRepository productionRepository;

    @Autowired

    ProductRepository productRepository;

    @Value(value = "${kafka.topic.consumer.payment.topic}")
    String topic;


    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0-debian"));

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
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        cleandb();
        loadDb();
    }

    @Test
    void testListenPayment_withSuccess() throws InterruptedException {
        PaymentDto paymentDto = jsonUtils.parse(new FileUtils()
                .getFile("/data/status.json"), PaymentDto.class).get();

        topicProducer().produce(paymentDto);
        boolean messageConsumed = paymentConsumer.getLatch().await(10, TimeUnit.SECONDS);
        Production production = productionUseCase.findById("10998");

        assertEquals("10998",production.getOrderId());
        assertEquals("EM_PREPARACAO", production.getStatusValue());
        assertEquals(3, production.getProducts().size());
        assertEquals("1222", production.getProducts().get(0).getSku());
        assertTrue(messageConsumed);
    }

    @Test
    void testListenPayment_withError() throws InterruptedException {
        PaymentDto paymentDto = jsonUtils.parse(new FileUtils()
                .getFile("/data/statusError.json"), PaymentDto.class).get();
        topicProducer().produce(paymentDto);
        boolean messageConsumed = paymentConsumer.getLatch().await(10, TimeUnit.SECONDS);
        Production production = productionUseCase.findById("10998");
        assertEquals("RECEBIDO", production.getStatusValue());
        assertEquals(3, production.getProducts().size());
        assertFalse(messageConsumed);
    }



    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(kafkaContainer.getBootstrapServers());
    }


    public ProducerFactory<String, PaymentDto> producerFactory(){
        return kafkaProducer().producerFactory();
    }

    @Bean
    public KafkaTemplate<String, PaymentDto> kafkaTemplate() {
        return kafkaProducer().kafkaTemplate();
    }

    @Bean
    public TopicProducer<PaymentDto> topicProducer(){
        return new TopicProducer<>(kafkaTemplate(), topic);
    }


    private void cleandb() {
        productRepository.deleteAll();
        productionRepository.deleteAll();
    }

    private void loadDb() {
        List<Production> response = new ProductionHelper().getProductions();
        response.stream().forEach(productionUseCase::insert);
    }

}