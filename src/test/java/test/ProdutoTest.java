package test;

import entidade.Produto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.apache.http.HttpStatus.SC_OK;

public class ProdutoTest {

    Produto produto;

    @BeforeAll
    public static void setupTest(){
        RestAssured.baseURI = "https://serverest.dev";
    }

    @Test
    public void getProdutosCompletoComSucesso(){

        File file = new File("src/test/resources/schemas/getProductsFull.json");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("produtos")
                .then()
                .statusCode(SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file));

    }



}
