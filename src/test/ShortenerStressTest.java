import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ShortenerStressTest {

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8084/shortener/shorten";
        HttpClient client = HttpClient.newHttpClient();

        for (int i = 1; i <= 2989; i++) {
            String originalUrl = "https://url" + i + ".com/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(originalUrl))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("[" + i + "] URL shortened: " + response.body());
                } else {
                    System.err.println("[" + i + "] Failed with status: " + response.statusCode());
                }

            } catch (Exception e) {
                System.err.println("[" + i + "] Exception: " + e.getMessage());
            }
        }
    }
}
