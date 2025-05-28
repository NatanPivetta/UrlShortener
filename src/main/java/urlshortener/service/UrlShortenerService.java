package urlshortener.service;


import jakarta.ws.rs.core.Response;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;

public interface UrlShortenerService {
    Response shortenUrl(String originalUrl);

    Response shortenUrlCustom(String originalUrl, String customKey);
}
