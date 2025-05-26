package urlshortener.service;


import urlshortener.model.ShortURL;

public interface UrlShortenerService {
    ShortURL shortenUrl(String originalUrl);
}
