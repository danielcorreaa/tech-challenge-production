package com.techchallenge.bdd;


import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class StepDefinition {

    private Response response;

    private String orderId;

    private String ENDPOINT_PRODUCTION = "http://localhost:8085/tech-challenge-production/production/api/v1";



    @Dado("Dado que tenho um pedido na fila")
    public void dado_que_tenho_um_pedido_na_fila() {
       orderId = "852370";
    }
    @Quando("e quero passar ele para pronto")
    public void e_quero_passar_ele_para_pronto() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT_PRODUCTION+"/ready/{orderId}",orderId );

    }
    @Entao("devo conseguir alterar o status")
    public void devo_conseguir_alterar_o_status() {
        response.then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/production-schema-find.json"));
    }


    @Dado("Dado que tenho um pedido no status pronto")
    public void dado_que_tenho_um_pedido_no_status_pronto() {
        orderId = "852371";
    }
    @Quando("e quero passar ele para finalizado")
    public void e_quero_passar_ele_para_finalizado() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT_PRODUCTION+"/finish/{orderId}",orderId);
    }
    @Entao("devo conseguir alterar para finalizado")
    public void devo_conseguir_alterar_para_finalizado() {
        response.then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/production-schema-find.json"));;
    }

    @Dado("Dado que tenho um pedido cadastrado")
    public void dado_que_tenho_um_pedido_cadastrado() {
        orderId = "852369";
    }
    @Dado("e tenho o id do pedido")
    public void e_tenho_o_id_do_pedido() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(ENDPOINT_PRODUCTION+"/find/{orderId}",orderId);
    }

    @Entao("devo conseguir buscar o pedido que esta sendo produzido")
    public void devo_conseguir_buscar_o_pedido_que_esta_sendo_produzido() {
        response.then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/production-schema-find.json"));;
    }

}
