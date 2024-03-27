package com.techchallenge.infrastructure.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techchallenge.application.gateway.ProductionGateway;
import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.application.usecases.interactor.ProductionUseCaseInteractor;
import com.techchallenge.core.exceptions.handler.ExceptionHandlerConfig;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.Result;
import com.techchallenge.infrastructure.api.dto.ProductionResponse;
import com.techchallenge.infrastructure.api.mapper.ProductionMapper;
import com.techchallenge.infrastructure.gateways.ProductionRepositoryGateway;
import com.techchallenge.infrastructure.gateways.StatusOutboxRepositoryGateway;
import com.techchallenge.infrastructure.persistence.entity.ProductEntity;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import com.techchallenge.infrastructure.persistence.mapper.ProductionEntityMapper;
import com.techchallenge.infrastructure.persistence.mapper.StatusEntityOutboxMapper;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import com.techchallenge.infrastructure.persistence.repository.StatusEntityOutboxRespository;
import com.techchallenge.utils.ProductionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class ProductionApiTest {


    MockMvc mockMvc;
    ProductionApi productionApi;
    ProductionUseCase productionUseCase;
    ProductionMapper productionMapper;
    ProductionGateway productionGateway;
    @Spy
    ProductionRepository productionRepository;
    @Spy
    ProductRepository productRepository;
    StatusOutboxGateway statusOutboxGateway;
    ProductionEntityMapper productionEntityMapper;

    @Spy
    StatusEntityOutboxRespository statusEntityOutboxRespository;
    StatusEntityOutboxMapper statusEntityOutboxMapper;

    JsonUtils jsonUtils;

    @BeforeEach
    void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonUtils = new JsonUtils(objectMapper);
        statusEntityOutboxMapper = new StatusEntityOutboxMapper();
        statusOutboxGateway = new StatusOutboxRepositoryGateway(statusEntityOutboxRespository, statusEntityOutboxMapper);
        productionEntityMapper = new ProductionEntityMapper();
        productionGateway = new ProductionRepositoryGateway(productionRepository,
                productRepository,
                statusOutboxGateway,productionEntityMapper);
        productionUseCase = spy(new ProductionUseCaseInteractor(productionGateway));
        productionMapper = new ProductionMapper();
        productionApi = new ProductionApi(productionUseCase, productionMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(productionApi).setControllerAdvice(new ExceptionHandlerConfig()).build();
    }

    @Test
    void testUpdateToReady() throws Exception {
        List<ProductEntity> products = new ProductionHelper().getProducEntities();
        ProductionEntity production = new ProductionHelper().getProductionEntity("85255", "EM_PREPARACAO", products );
        when(productionRepository.findById("85255")).thenReturn(Optional.of(production));

        MvcResult mvcResult = mockMvc.perform(put("/production/api/v1/ready/85255").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Optional<Result<ProductionResponse>> response = getParse(mvcResult);

        int code = response.get().getCode();

        assertEquals(200, code, "Must Be Equals");
        assertEquals("85255", response.get().getBody().orderId(), "Must Be Equals");
        assertEquals("PRONTO", response.get().getBody().statusOrder(), "Must Be Equals");
        verify(productionRepository, times(1)).findById("85255");
        verify(productionRepository, times(1)).save(any());
        verify(statusEntityOutboxRespository, times(1)).save(any());
    }

    @Test
    void testUpdateToFinish() throws Exception {
        List<ProductEntity> products = new ProductionHelper().getProducEntities();
        ProductionEntity production = new ProductionHelper().getProductionEntity("85255", "PRONTO", products );
        when(productionRepository.findById("85255")).thenReturn(Optional.of(production));

        MvcResult mvcResult = mockMvc.perform(put("/production/api/v1/finish/85255").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        Optional<Result<ProductionResponse>> response = getParse(mvcResult);

        int code = response.get().getCode();

        assertEquals(200, code, "Must Be Equals");
        assertEquals("85255", response.get().getBody().orderId(), "Must Be Equals");
        assertEquals("FINALIZADO", response.get().getBody().statusOrder(), "Must Be Equals");

        verify(productionRepository, times(1)).findById("85255");
        verify(productionRepository, times(1)).save(any());
        verify(statusEntityOutboxRespository, times(1)).save(any());
    }

    @Test
    void tesFindById() throws Exception {
        List<ProductEntity> products = new ProductionHelper().getProducEntities();
        ProductionEntity production = new ProductionHelper().getProductionEntity("85255", "PRONTO", products );
        when(productionRepository.findById("85255")).thenReturn(Optional.of(production));

        MvcResult mvcResult = mockMvc.perform(get("/production/api/v1/find/85255")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

        Optional<Result<ProductionResponse>> response = getParse(mvcResult);

        int code = response.get().getCode();

        assertEquals(200, code, "Must Be Equals");
        assertEquals("85255", response.get().getBody().orderId(), "Must Be Equals");
        assertEquals("PRONTO", response.get().getBody().statusOrder(), "Must Be Equals");
        assertEquals(3, response.get().getBody().products().size(), "Must Be Equals");

        verify(productionRepository, times(1)).findById("85255");
        verify(productionRepository, never()).save(any());
        verify(statusEntityOutboxRespository,never()).save(any());
    }

    @Test
    void tesFindByIdNotFound() throws Exception {
        List<ProductEntity> products = new ProductionHelper().getProducEntities();
        ProductionEntity production = new ProductionHelper().getProductionEntity("85255", "PRONTO", products );
        when(productionRepository.findById("555")).thenReturn(Optional.of(production));

        MvcResult mvcResult = mockMvc.perform(get("/production/api/v1/find/85255")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

        Optional<Result<ProductionResponse>> response = getParse(mvcResult);

        int code = response.get().getCode();
        assertEquals(404, code, "Must Be Equals");

        verify(productionRepository, times(1)).findById("85255");
        verify(productionRepository, never()).save(any());
        verify(statusEntityOutboxRespository,never()).save(any());
    }

    private Optional<Result<ProductionResponse>> getParse(MvcResult mvcResult) throws UnsupportedEncodingException {
        return jsonUtils.parse(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Result<ProductionResponse>>() {
                });
    }

}