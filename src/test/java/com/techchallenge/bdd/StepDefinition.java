package com.techchallenge.bdd;


import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.core.response.JsonUtils;
import com.techchallenge.core.response.ObjectMapperConfig;
import com.techchallenge.core.utils.FileUtils;
import com.techchallenge.infrastructure.message.consumer.dto.OrderDto;
import com.techchallenge.infrastructure.message.consumer.dto.PaymentDto;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static io.restassured.RestAssured.given;

public class StepDefinition {

    private Response response;

    private String orderId;

    private String ENDPOINT_PRODUCTION = "http://localhost:8085/api/v1/production";



    @Dado("Dado que tenho um pedido na fila")
    public void dado_que_tenho_um_pedido_na_fila() {
       orderId = "5256";
    }
    @Quando("e quero passar ele para pronto")
    public void e_quero_passar_ele_para_pronto() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT_PRODUCTION+"/ready/{orderId}",orderId );

    }
    @Entao("devo conseguir alterar o status")
    public void devo_conseguir_alterar_o_status() {
        response.then().statusCode(HttpStatus.OK.value());
    }


    @Dado("Dado que tenho um pedido no status pronto")
    public void dado_que_tenho_um_pedido_no_status_pronto() {
        orderId = "5256";
    }
    @Quando("e quero passar ele para finalizado")
    public void e_quero_passar_ele_para_finalizado() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT_PRODUCTION+"/finish/{orderId}",orderId);
    }
    @Entao("devo conseguir alterar para finalizado")
    public void devo_conseguir_alterar_para_finalizado() {
        response.then().statusCode(HttpStatus.OK.value());
    }

    @Dado("Dado que tenho um pedido cadastrado")
    public void dado_que_tenho_um_pedido_cadastrado() {
        orderId = "5256";
    }
    @Dado("e tenho o id do pedido")
    public void e_tenho_o_id_do_pedido() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT_PRODUCTION+"/find/{orderId}","6593732dcfdb826a875770ff" );
    }
    @Entao("devo conseguir buscar o pedido que está sendo produzido")
    public void devo_conseguir_buscar_o_pedido_que_está_sendo_produzido() {
        response.then().statusCode(HttpStatus.OK.value());
    }





}
