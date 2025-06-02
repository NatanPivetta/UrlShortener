package urlshortener.service;


import jakarta.ws.rs.core.Response;

public interface UrlShortenerService {
    Response shortenUrl(String originalUrl);

    Response shortenUrlCustom(String originalUrl, String customKey);
}
