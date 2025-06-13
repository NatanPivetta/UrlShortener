package urlshortener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public Response shortenUrl(String originalUrl, String userEmail) {

        ShortURL existing = shortUrlRepository.findByOriginalUrl(originalUrl);
        if (existing != null) {
            return Response
                    .status(Response.Status.OK)
                    .entity(existing)
                    .build();
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

        return Response
                .status(Response.Status.CREATED)
                .entity(shortUrl)
                .build();
    }

    @Override
    @Transactional
    public Response shortenUrlCustom(String originalUrl, String customKey, String userEmail) {
        System.out.println(customKey);

        if (!customKey.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new WebApplicationException("Chave inválida. Use apenas letras e números (4-20 caracteres).", 400);
        }

        ShortURL existing = shortUrlRepository.findByOriginalUrl(originalUrl);
        if (existing != null) {
            return Response
                    .status(Response.Status.OK)
                    .entity(existing)
                    .build();
        }


        URLKey customKeyURL = urlKeyRepository.findByKey(customKey);
        if(customKeyURL == null) {
            customKeyURL = criarUrlKey(customKey);

        }

        ShortURL shortUrl = criarShortUrl(originalUrl,customKeyURL);
        shortUrlRepository.persist(shortUrl);
        return Response
                .status(Response.Status.CREATED)
                .entity(shortUrl)
                .build();
    }

    @Override
    @Transactional
    public Response unlink(String chave){

        URLKey key = urlKeyRepository.findByKey(chave);
        if (key == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Chave não encontrada")
                    .build();
        }

        ShortURL shortURL = shortUrlRepository.findByKey(chave);
        if (shortURL == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Url não encontrada")
                    .build();
        }

        shortURL.desativar();
        shortUrlRepository.save(shortURL);

        key.desativar();
        urlKeyRepository.save(key);

        return Response.ok()
                .entity("Chave e URL desassociadas")
                .build();
    }

    private ShortURL criarShortUrl(String originalUrl, URLKey key) {
        ShortURL shortUrl = new ShortURL();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setUrlKey(key);
        shortUrl.ativar();
        return shortUrl;
    }

    private URLKey criarUrlKey(String customKey ) {
        URLKey customKeyURL = new URLKey();
        customKeyURL.setChave(customKey);
        customKeyURL.ativar();
        customKeyURL.setData_criacao(Timestamp.valueOf(LocalDateTime.now()));
        urlKeyRepository.save(customKeyURL);
        return customKeyURL;
    }
}
