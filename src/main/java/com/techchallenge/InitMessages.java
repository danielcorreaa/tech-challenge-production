package com.techchallenge;

import com.techchallenge.application.usecases.ProductionUseCase;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.domain.entity.Production;
import com.techchallenge.domain.valueobject.Product;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import com.techchallenge.infrastructure.persistence.repository.ProductRepository;
import com.techchallenge.infrastructure.persistence.repository.ProductionRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitMessages implements  ApplicationListener<ContextRefreshedEvent> {
    private ProductionUseCase productionUseCase;

    public InitMessages(ProductionUseCase productionUseCase) {
        this.productionUseCase = productionUseCase;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Product product = new Product("852369001", "X salada", "LANCHE", "test", "");
        Production production = new Production("852369", "RECEBIDO", List.of(product));
        Production insert = productionUseCase.insert(production);

        createProduction("852370");
        productionUseCase.preparation("852370");

        createProduction("852371");
        productionUseCase.preparation("852371");
        productionUseCase.ready("852371");
    }

    private void createProduction(String orderId) {
        Production recebido = getProduction(orderId, "RECEBIDO", getProducts());
        productionUseCase.insert(recebido);
    }

    public Production getProduction(String orderId, String status, List<Product> products){
        return new Production(orderId, status, products);
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

    public Product getProduct(String sku, String title, String category){
        return new Product(sku, title, category, "test-description", "");
    }


}
