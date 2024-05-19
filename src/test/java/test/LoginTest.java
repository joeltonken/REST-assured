package test;

import entidade.Login;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;


public class LoginTest {

    Login login;

    @BeforeAll
    public static void setupTest(){
        RestAssured.baseURI = "https://serverest.dev";
    }

    @Test
    public void loginComUsuarioRetornoSucesso(){
        Login usuario = new Login("beltrano1@qa.com.br", "teste");

        File file = new File("src/test/resources/schemas/loginSuccess.json");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(usuario)
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file))
                .and().extract().response();


        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assertions.assertEquals(message,"Login realizado com sucesso");
    }

    @Test
    public void loginComSenhaIncorretaRetorno401(){
        Login usuario = new Login("beltrano1@qa.com.br", "teste1");

        File file = new File("src/test/resources/schemas/loginError.json");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(usuario)
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file))
                .and().extract().response();


        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assertions.assertEquals(message,"Email e/ou senha inválidos");
    }

    @Test
    public void loginComSenhaEmBrancoRetorno400(){
        Login usuario = new Login("beltrano1@qa.com.br", "");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(usuario)
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .log().all()
                .and().extract().response();

        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("password");
        Assertions.assertEquals(message,"password não pode ficar em branco");
    }

}
