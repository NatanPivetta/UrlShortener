package urlshortener.service;


import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;

public interface UrlShortenerService {
    ShortURL shortenUrl(String originalUrl);

    ShortURL shortenUrlCustom(String originalUrl, String customKey);
}
