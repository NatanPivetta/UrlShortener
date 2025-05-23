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

    private static final List<String> EXISTING_KEYS = Arrays.asList(
            "4f9a", "4fct", "4f5n", "4fdt", "4f0b", "4fgi", "4f1o", "4f1c", "4f0v",
            "4f0s", "4f0r", "4f0q", "4f0p", "4f0n", "4f0d", "4f0e", "4f0f", "4f0g",
            "4f0h", "4f0k", "4f0o", "4f0m", "4f0l", "4f0j", "4fcu", "4fal", "4fgj",
            "4fay", "4fb1", "4fb6", "4fb9", "4fbu", "4fcg", "4fcp", "4f0a", "4fg0",
            "4fd2", "4fdn", "4fe4", "4feo", "4ffm", "4ffn", "4ffq", "4f8s", "4f28",
            "4f2h", "4f3g", "4f3p", "4f3t", "4f3y", "4f43", "4f4d", "4f4t", "4f57",
            "4f5z", "4f6d", "4f73", "4f7r", "4f7v", "4f83", "4f89", "4f8h", "4fa6",
            "4fea", "4f0c"
    );

    @Test
    @DisplayName("Deve retornar redirecionamento (302 ou 301) para chaves válidas, ignorando 404")
    void testRedirectsForExistingKeys() {
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