package com.techchallenge.infrastructure.api;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.response.Result;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.infrastructure.api.dto.ProductionResponse;
import com.techchallenge.infrastructure.api.mapper.ProductionMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("production/api/v1")
public class ProductionApi {

    private ProductionUseCase productionUseCase;
    private ProductionMapper mapper;

    public ProductionApi(ProductionUseCase productionUseCase, ProductionMapper mapper) {
        this.productionUseCase = productionUseCase;
        this.mapper = mapper;
    }

    @PutMapping("/ready/{orderId}")
    public ResponseEntity<Result<ProductionResponse>> ready(@PathVariable String orderId) {
        Production production = productionUseCase.ready(orderId);
        return ResponseEntity.ok(Result.ok(mapper.toProductionResponse(production)));
    }

    @PutMapping("/finish/{orderId}")
    public ResponseEntity<Result<ProductionResponse>> finish(@PathVariable String orderId) {
        Production production = productionUseCase.finish(orderId);
        return ResponseEntity.ok(Result.ok(mapper.toProductionResponse(production)));
    }

    @GetMapping("/find/{orderId}")
    public ResponseEntity<Result<ProductionResponse>> findByid(@PathVariable String orderId) {
        Production production = productionUseCase.findById(orderId);
        return ResponseEntity.ok(Result.ok(mapper.toProductionResponse(production)));
    }

}
