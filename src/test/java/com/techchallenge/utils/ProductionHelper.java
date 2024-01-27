package com.techchallenge.utils;

import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.persistence.entity.ProductEntity;
import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import com.techchallenge.infrastructure.persistence.entity.StatusOutboxEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductionHelper {

    public List<Production> getProductions(){        ;
       return List.of(
               getProduction("10998", "RECEBIDO", getProducts()),
               getProduction("8521", "RECEBIDO", getProducts()),
               getProduction("985", "RECEBIDO", getProducts())
       );
    }

    public Production getProduction(String orderId, String status, List<Product> products){
        return new Production(orderId, status, products);
    }

    public Product getProduct(String sku, String title, String category){
        return new Product(sku, title, category, "test-description", "");
    }


    public List<Product> getProducts(){
        List<String> skus  = List.of("1222", "4345", "98001");
        List<String> titles  = List.of("X Salada Banco", "Coca Cola 600 ml", "Bolo Chocolate");
        List<String> categories  = List.of("LANCHE", "BEBIDA", "SOBREMESA");
        List<Product> response = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            response.add(getProduct(skus.get(i), titles.get(i), categories.get(i)));
        }
        return response;
    }

    public ProductionEntity getProductionEntity(String orderId, String status,List<ProductEntity> products){
        return ProductionEntity.builder()
                .orderId(orderId).status(status).products(products)
                .build();
    }


    public List<ProductEntity> getProducEntities() {

        List<String> skus  = List.of("1222", "4345", "98001");
        List<String> titles  = List.of("X Salada Banco", "Coca Cola 600 ml", "Bolo Chocolate");
        List<String> categories  = List.of("LANCHE", "BEBIDA", "SOBREMESA");
        List<ProductEntity> response = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            response.add(getProducEntity(Long.valueOf(i), skus.get(i), titles.get(i), categories.get(i)));
        }
        return response;
    }

    public ProductEntity getProducEntity(Long id, String sku, String title, String category) {
        return ProductEntity.builder().id(id).image("").sku(sku).title(title).category(category).description("test").build();
    }

    public StatusOutboxEntity statusOutboxEntity(LocalDateTime createDate, Boolean send,String status, String orderId ){
        return StatusOutboxEntity.builder().createTime(createDate).send(send).status(status).orderId(orderId).build();
    }

    //RECEBIDO, EM_PREPARACAO, PRONTO, FINALIZADO;
    public List<StatusOutboxEntity> buildListStatusOutboxEntity(){
        LocalDateTime date1 = LocalDateTime.of(2022,11,25,10,20,0);
        LocalDateTime date2 = LocalDateTime.of(2022,11,25,10,25,0);
        LocalDateTime date3 = LocalDateTime.of(2022,11,25,10,27,0);

        return List.of(
                statusOutboxEntity(date1, false, "EM_PREPARACAO", "45639"),
                statusOutboxEntity(date2, false, "PRONTO", "45639"),
                statusOutboxEntity(date3, false, "FINALIZADO", "45639")
        );
    }
}
