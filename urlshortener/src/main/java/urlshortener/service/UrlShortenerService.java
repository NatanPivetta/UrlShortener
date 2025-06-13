package urlshortener.service;


import jakarta.ws.rs.core.Response;

public interface UrlShortenerService {
    Response shortenUrl(String originalUrl, String userEmail);

    Response shortenUrlCustom(String originalUrl, String customKey, String userEmail);

    Response unlink(String chave);
}
