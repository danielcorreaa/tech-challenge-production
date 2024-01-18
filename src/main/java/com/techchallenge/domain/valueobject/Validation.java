package com.techchallenge.domain.valueobject;

import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.entity.Production;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Validation {


    public static final String DESCRIPTION = "Description";
    public static final String TITLE = "Title";
    public static final String SKU = "Sku";
    public static final String CATEGORY = "Category";
    public static final String ORDER_ID = "OrderId";

    private Validation() {
    }

    public static String validate(String value, String fieldName) {
        return Optional.ofNullable(value).filter(v -> !v.trim().isEmpty())
                .orElseThrow(() -> new BusinessException(fieldName +" can't be null or empty"));
    }

    public static List<Product> validate(List<Product> products){
        return Optional.ofNullable(products)
                .orElseThrow(() -> new BusinessException("Products can't be null"))
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), result -> {
                    if (result.isEmpty())
                        throw new BusinessException("Products can't be null");
                    return result;
                }));
    }


    public static String validateDescription(String description) {
        return validate(description, DESCRIPTION);
    }

    public static String validateCategory(String category) {
        return validate(category, CATEGORY);
    }

    public static String validateTitle(String title) {
        return validate(title,TITLE);
    }

    public static String validateSku(String sku) {
        return validate(sku, SKU);
    }

    public static String validateOrderId(String orderId) {
        return validate(orderId, ORDER_ID);
    }

    public static Production validateProduction(Production production) {
        return Optional.ofNullable(production).orElseThrow(() -> new BusinessException("Production can't be null"));
    }
}
