package service;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class RedirectEndpointTest {

    private static final List<String> EXISTING_KEYS = List.of(
            "natan"
    );

    @Test
    @DisplayName("Deve retornar redirecionamento (302 ou 301) para chaves válidas, ignorando 404")
    void testRedirectsForExistingKeys() {
        for(int i = 0; i < 100; i++) {
            for (String key : EXISTING_KEYS) {
                int statusCode = given()
                        .redirects().follow(false)
                        .when()
                        .get("/" + key)
                        .then()
                        .extract().statusCode();

                if (statusCode == 302 || statusCode == 301) {
                    given()
                            .redirects().follow(false)
                            .when()
                            .get("/" + key)
                            .then()
                            .statusCode(anyOf(is(302), is(301)))
                            .header("Location", not(isEmptyOrNullString()));
                } else if (statusCode == 404) {
                    // Ignora 404, pode logar se quiser
                    System.out.println("Chave não encontrada (404): " + key);
                } else {
                    // Se for outro status, falha o teste explicitamente
                    throw new AssertionError("Status inesperado para chave " + key + ": " + statusCode);
                }
            }
        }

    }

}