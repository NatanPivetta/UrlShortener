package urlshortener.service;


import jakarta.ws.rs.core.Response;
import urlshortener.model.User;

public interface UrlShortenerService {
    Response shortenUrl(String originalUrl, User user);

    Response shortenUrlCustom(String originalUrl, String customKey, User user);

    Response unlink(String chave);
}
