package com.techchallenge.application.usecases.interactor;

import com.techchallenge.MysqlTestConfig;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import com.techchallenge.utils.MockObject;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration( classes = {MysqlTestConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@Testcontainers
class ProductionUseCasaIT {

    @Autowired
    ProductionUseCase productionUseCase;

    @Autowired
    ProductionRepository productionRepository;

    @Autowired
    ProductRepository productRepository;

    MockObject mockObject;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer(DockerImageName.parse("mysql:8.0-debian"));

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
    void start(){
        clean();
        mockObject = new MockObject();
    }

    @Test
    void testiInsertProduction(){
        Product product = new Product("1223", "X salada", "LANCHE", "test", "");
        Production production = new Production("12234", "RECEBIDO", List.of(product));
        Production insert = productionUseCase.insert(production);
        assertEquals("12234", insert.getOrderId());
    }

    @Test
    void testFindById(){
        createProduction();
        Production byId = productionUseCase.findById("5256");
        assertEquals("5256", byId.getOrderId());
        assertEquals("RECEBIDO", byId.getStatusValue());
        assertEquals(3, byId.getProducts().size());
    }

    @Test
    void testPreparation(){
        createProduction();
        productionUseCase.preparation("5256");
        Production byId = productionUseCase.findById("5256");
        assertEquals("5256", byId.getOrderId());
        assertEquals("EM_PREPARACAO", byId.getStatusValue());
        assertEquals(3, byId.getProducts().size());
    }

    @Test
    void testReady(){
        createProduction();
        productionUseCase.preparation("5256");
        productionUseCase.ready("5256");
        Production byId = productionUseCase.findById("5256");
        assertEquals("5256", byId.getOrderId());
        assertEquals("PRONTO", byId.getStatusValue());
        assertEquals(3, byId.getProducts().size());

    }

    @Test
    void testFinish(){
        createProduction();
        productionUseCase.preparation("5256");
        productionUseCase.ready("5256");
        productionUseCase.finish("5256");
        Production byId = productionUseCase.findById("5256");
        assertEquals("5256", byId.getOrderId());
        assertEquals("FINALIZADO", byId.getStatusValue());
        assertEquals(3, byId.getProducts().size());
    }

    void clean(){
        productRepository.deleteAll();
        productionRepository.deleteAll();
    }
    private void createProduction() {
        Production recebido = mockObject.getProduction("5256", "RECEBIDO", mockObject.getProducts());
        productionUseCase.insert(recebido);
    }

}