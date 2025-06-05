package service;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import urlshortener.service.KeyGeneratorService;

@QuarkusTest
public class UrlShortenerLoadTest {

    @Inject
    KeyGeneratorService kgs;

    @Test
    public void testGenerateMultipleShortUrls() {
        kgs.generateKeys(10000);
    }
}
