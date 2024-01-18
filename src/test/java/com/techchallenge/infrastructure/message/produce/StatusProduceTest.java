package com.techchallenge.infrastructure.message.produce;

import com.techchallenge.MysqlTestConfig;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.infrastructure.message.produce.dto.StatusDto;
import com.techchallenge.infrastructure.persistence.repository.StatusEntityOutboxRespository;
import com.techchallenge.utils.MockObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MysqlTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class StatusProduceTest {

    StatusProduce produce;

    @Autowired
    StatusOutboxGateway statusOutboxGateway;

    @Mock
    TopicProducer<StatusDto> topicProducer;


    @Autowired
    StatusEntityOutboxRespository statusEntityOutboxRespository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:latest"));

    @DynamicPropertySource
    static void overrrideMongoDBContainerProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto",() -> "update");
    }
    @BeforeAll
    static void setUp(){
        mySQLContainer.withReuse(true);
        mySQLContainer.start();
    }
    @AfterAll
    static void setDown(){
        mySQLContainer.stop();
    }

    @BeforeEach
    void init(){
        produce = new StatusProduce(statusOutboxGateway,topicProducer);
        clear();
    }

    @Test
    void testVerifyMessageToSend(){
        insertAll();
        List<StatusOutbox> byNotSend = statusOutboxGateway.findByNotSend();
        assertEquals("EM_PREPARACAO", byNotSend.get(0).getStatus());
        assertEquals("PRONTO", byNotSend.get(1).getStatus());
        assertEquals("FINALIZADO", byNotSend.get(2).getStatus());
        assertEquals(3, byNotSend.size());
    }



    @Test
    void testSendMessageStatus(){
        insertAll();
        produce.send();
        List<StatusOutbox> byNotSend = statusOutboxGateway.findByNotSend();

        assertEquals(0, byNotSend.size());
        Mockito.verify(topicProducer, times(3)).produce(any(), any());
    }

    private void insertAll() {
        new MockObject().buildListStatusOutboxEntity().forEach(statusEntityOutboxRespository::save);
    }

    private void clear() {
        statusEntityOutboxRespository.deleteAll();
    }


}