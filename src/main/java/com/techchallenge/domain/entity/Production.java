package com.techchallenge.domain.entity;

import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.domain.valueobject.Validation;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Production {

    public static final String INVALID_STATUS_TO_ORDER = "Invalid status to order";
    private String orderId;
    private List<Product> products;
    private StatusOrder status;


    public Production(String orderId,  String statusOrder,  List<Product> products) {
        this.orderId = Validation.validateOrderId(orderId);
        this.products = Validation.validate(products);
        this.status = StatusOrder.getByName(statusOrder);
    }

    public Production preparation() {
        this.status = Optional.ofNullable(this.status)
                .filter(Predicate.isEqual(StatusOrder.RECEBIDO))
                .map(st -> StatusOrder.EM_PREPARACAO)
                .orElseThrow(() -> new BusinessException(INVALID_STATUS_TO_ORDER));
        return this;
    }

    public  Production ready() {
        this.status = Optional.ofNullable(this.status)
                .filter(Predicate.isEqual(StatusOrder.EM_PREPARACAO))
                .map(st -> StatusOrder.PRONTO)
                .orElseThrow(() -> new BusinessException(INVALID_STATUS_TO_ORDER));
        return this;
    }

    public Production finish() {
        this.status = Optional.ofNullable(this.status)
                .filter(Predicate.isEqual(StatusOrder.PRONTO))
                .map(st -> StatusOrder.FINALIZADO)
                .orElseThrow(() -> new BusinessException(INVALID_STATUS_TO_ORDER));
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public StatusOrder getStatus() {
        return status;
    }

    public String getStatusValue() {
        return status.getValue();
    }


}
