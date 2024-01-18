package com.techchallenge.domain.entity;

import com.techchallenge.core.exceptions.BusinessException;
import com.techchallenge.domain.enums.StatusOrder;
import com.techchallenge.domain.valueobject.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductionTest {


    @Nested
    class TestValidation {
        @Test
        void testCreateProductionOrderNull() {
            BusinessException bex =
                    assertThrows(BusinessException.class, () -> new Production(null, "RECEBIDO", getProducts()));
            Assertions.assertEquals("OrderId can't be null or empty", bex.getMessage());

        }

        @Test
        void testCreateProductionOrderEmpty() {
            BusinessException bex = assertThrows(BusinessException.class,
                    () -> new Production(" ", "RECEBIDO", getProducts()));
            Assertions.assertEquals("OrderId can't be null or empty", bex.getMessage());
        }

        @Test
        void testCreateProductionProductsIsNull() {
            assertThrows(BusinessException.class, () -> new Production("00","RECEBIDO",  null));
        }

        @Test
        void testCreateProductionProductsIsEmpty() {
            assertThrows(BusinessException.class, () -> new Production("00","RECEBIDO",  List.of()));
        }
    }

    @Nested
    class TestStatus {

        @Test
        void testCreateObjectProductionWithStatusRecebido() {
            Production production = new Production("6855", "RECEBIDO", getProducts());
            Assertions.assertEquals(StatusOrder.RECEBIDO, production.getStatus());
            Assertions.assertEquals("6855", production.getOrderId());
            Assertions.assertEquals(getProducts().size(), production.getProducts().size());
        }

        @Test
        void testChangeStatusProductionToEmPreparacao() {
            Production production = new Production("6855","RECEBIDO", getProducts());

            production.preparation();

            Assertions.assertEquals(StatusOrder.EM_PREPARACAO, production.getStatus());
        }

        @Test
        void testChangeStatusProductionToPronto() {
            Production production = new Production("6855","RECEBIDO", getProducts());
            production.preparation();
            production.ready();

            Assertions.assertEquals(StatusOrder.PRONTO, production.getStatus());
        }

        @Test
        void testChangeStatusProductionFinalizado() {
            Production production = new Production("6855","RECEBIDO", getProducts());

            production.preparation();
            production.ready();
            production.finish();

            Assertions.assertEquals(StatusOrder.FINALIZADO, production.getStatus());
        }
    }


    public Product getProduct(String sku, String category){
        return new Product(sku,"X Salada Bacon", category,
                "Carne com Alface e pao e bacon", "");
    }

    List<Product> getProducts(){
        Product product1 = getProduct("1234001", "LANCHE");
        Product product2 = getProduct("1234002", "BEBIDA");
        Product product3 = getProduct("1234003", "SEBREMESA");
        return Arrays.asList(product1, product2, product3);
    }

}