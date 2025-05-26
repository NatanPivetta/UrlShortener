package urlshortener.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;

import java.util.List;
import java.util.Random;

@ApplicationScoped
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Inject
    KeyGeneratorService kgs;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    UrlKeyRepository urlKeyRepository;



    private static final Random random = new Random();

    @Override
    @Transactional
    public ShortURL shortenUrl(String originalUrl) {

        ShortURL existing = shortUrlRepository.findByOriginalUrl(originalUrl);
        if (existing != null) {
            return existing;
        }

        List<URLKey> availableKeys = urlKeyRepository.findAvailableKeys();

        if (availableKeys.isEmpty()) {
            throw new WebApplicationException("No available keys", 400);
        }

        if (availableKeys.size() < 100) {
            kgs.generateKeys(1000);
        }

        URLKey selectedKey = availableKeys.get(random.nextInt(availableKeys.size()));
        selectedKey.ativar();

        ShortURL shortUrl = criarShortUrl(originalUrl, selectedKey);
        shortUrlRepository.persist(shortUrl);

        return shortUrl;
    }

    private ShortURL criarShortUrl(String originalUrl, URLKey key) {
        ShortURL shortUrl = new ShortURL();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setUrlKey(key);
        return shortUrl;
    }
}
