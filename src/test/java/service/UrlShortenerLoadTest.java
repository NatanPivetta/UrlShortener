package service;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UrlShortenerLoadTest {

    @Test
    public void testGenerateMultipleShortUrls() {
        for (int i = 1; i <= 2989; i++) {
            String originalUrl = "https://url" + i + ".com/";

            RestAssured.given()
                    .contentType("text/plain")
                    .body(originalUrl)
                    .when()
                    .post("/shortener/shorten")
                    .then()
                    .statusCode(200);
        }
    }
}
