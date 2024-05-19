package test;

import com.github.javafaker.Faker;
import entidade.Usuario;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

public class UsuarioTest {

    Usuario usuario;
    @BeforeAll
    public static void setupTest(){   RestAssured.baseURI = "https://serverest.dev";    }
    @BeforeEach
    public void before(){
        usuario = getNewUser();
    }

    @Test
    public void criarUsuarioComSucesso(){
        Usuario usuario = getNewUser();

        File file = new File("src/test/resources/schemas/userSuccess.json");

        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .log().all()
                .when()
                    .body(usuario)
                    .post("usuarios")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file))
                .and().extract().response();


        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assertions.assertEquals(message,"Cadastro realizado com sucesso");
    }

    @Test
    public void criarUsuarioComEmailJaExistente(){
        Usuario usuario = getNewUser("fulano@qa.com");

        File file = new File("src/test/resources/schemas/userError.json");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(usuario)
                .post("usuarios")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file))
                .and().extract().response();


        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assertions.assertEquals(message,"Este email já está sendo usado");
    }

    @Test
    public void criarUsuarioComDadosDoEmailEmBranco(){

        Usuario usuario = getNewUser("");

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(usuario)
                .post("usuarios")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .log().all()
                .and().extract().response();


        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("email");
        Assertions.assertEquals(message,"email não pode ficar em branco");
    }

    @Test
    public void getUsuarioCompletoComSucesso(){
        File file = new File("src/test/resources/schemas/getUsersFull.json");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("usuarios")
                .then()
                .statusCode(SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(file));

    }

    public Usuario getNewUser(){
       return getNewUser(new Faker().internet().emailAddress());
    }

    public Usuario getNewUser(String email){
        Faker faker = new Faker();
        return new Usuario(
                faker.name().fullName(),
                email,
                "teste123",
                "true");
    }
}
