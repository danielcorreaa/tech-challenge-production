package com.techchallenge.infrastructure.message.consumer;

import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.application.usecases.interactor.ProductionUseCaseInteractor;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.infrastructure.gateways.ProductionRepositoryGateway;
import com.techchallenge.infrastructure.gateways.StatusOutboxRepositoryGateway;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.message.consumer.mapper.ProductionMessageMapper;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import com.techchallenge.infrastructure.persistence.entity.StatusOutboxEntity;
import com.techchallenge.infrastructure.persistence.mapper.ProductionEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.StatusEntityOutboxMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import com.techchallenge.infrastructure.persistence.repository.StatusEntityOutboxRespository;
import com.techchallenge.utils.ProductionHelper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@ExtendWith(SpringExtension.class)
class PaymentConsumerTest {

    PaymentConsumer paymentConsumer;
    ProductionUseCase productionUseCase;

    StatusOutboxGateway statusOutboxGateway;

    ProductionMessageMapper productionMessageMapper;

    ProductionGateway productionGateway;

    JsonUtils jsonUtils;
    @Mock
    private ProductionRepository productionRepository;

    @Mock
    private ProductRepository productRepository;

    private ProductionEntityMapper productionEntityMapper;

    @Mock
    private StatusEntityOutboxRespository statusEntityOutboxRespository;
    private StatusEntityOutboxMapper statusEntityOutboxMapper;


    @BeforeEach
    void start(){
        jsonUtils = new JsonUtils(new ObjectMapperConfig().objectMapper());
        statusEntityOutboxMapper = new StatusEntityOutboxMapper();
        statusOutboxGateway = new StatusOutboxRepositoryGateway(statusEntityOutboxRespository, statusEntityOutboxMapper);
        productionEntityMapper = new ProductionEntityMapper();
        productionGateway = new ProductionRepositoryGateway(productionRepository, productRepository, statusOutboxGateway, productionEntityMapper);
        productionUseCase = new ProductionUseCaseInteractor(productionGateway);
        productionMessageMapper = new ProductionMessageMapper();
        paymentConsumer = new PaymentConsumer(productionUseCase, productionMessageMapper);
    }


    @Test
    void testListenPayment_withSuccess() throws InterruptedException {
        String orderId = "65f39d78af5fc21d9f9e8b00";

        PaymentDto paymentDto = jsonUtils.parse(new FileUtils()
                .getFile("/data/payment.json"), PaymentDto.class).get();

        Production production = new Production(paymentDto.orderId(),
                StatusOrder.EM_PREPARACAO.name(),
                productionMessageMapper.toProducts(paymentDto.products()));

        ProductionEntity entity = productionEntityMapper.toProductionEntity(production);
        AtomicInteger index = new AtomicInteger(0);
        production.getProducts().forEach(sku -> {
            entity.getProducts().get(index.get()).setSku(sku.getSku());
            entity.getProducts().get(index.get()).setId(getIdProduct(orderId, sku.getSku()));
            index.getAndIncrement();
        });

        Acknowledgment ack = spy(Acknowledgment.class);

        when(productionRepository.save(any(ProductionEntity.class))).thenReturn(entity);

        when(productRepository.saveAll(anyList())).thenReturn(entity.getProducts());

        paymentConsumer.listenPayment(paymentDto, ack);
        boolean messageConsumed = paymentConsumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        verify(productionRepository, times(1)).save(any(ProductionEntity.class));
        verify(statusEntityOutboxRespository, times(1)).save(any(StatusOutboxEntity.class));

    }

    @Test
    void testListenPayment_withError() throws InterruptedException {
        String orderId = "10998";
        List<Production> response = new ProductionHelper().getProductions();
        ProductionEntity entity = productionEntityMapper.toProductionEntity(response.get(0));
        PaymentDto paymentDto = jsonUtils.parse(new FileUtils()
                .getFile("/data/statusError.json"), PaymentDto.class).get();

        Acknowledgment ack = spy(Acknowledgment.class);

        paymentConsumer.listenPayment(paymentDto, ack);
        boolean messageConsumed = paymentConsumer.getLatch().await(10, TimeUnit.SECONDS);
        assertFalse(messageConsumed);
        verify(productionRepository, never()).findById(orderId);
        verify(productionRepository, never()).save(any(ProductionEntity.class));
        verify(statusEntityOutboxRespository, never()).save(any(StatusOutboxEntity.class));

    }

    private String getIdProduct(String orderId, String sku) {
        return orderId +"--"+ sku;
    }

}