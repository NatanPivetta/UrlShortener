package urlshortener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import urlshortener.dto.ShortUrlEvent;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.model.User;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;
import urlshortener.util.UrlShortenerProducer;

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

    @Inject
    UrlShortenerProducer producer;

    ObjectMapper mapper = new ObjectMapper();





    private static final Random random = new Random();


    @Override
    @Transactional
    public Response shortenUrl(String originalUrl, User user) {
        ShortUrlEvent event = new ShortUrlEvent();
        event.originalUrl = originalUrl;
        event.userEmail = user.email;
        event.custom = false;


        try {
            String json = mapper.writeValueAsString(event);
            System.out.println("Enviando para o kafka: " + json);
            producer.send("url-shortener", json);

        } catch (Exception e) {
            System.err.println("Erro ao converter DTO para JSON: " + e.getMessage());
        }


        return Response.accepted().entity("URL em processamento.").build();
    }

    @Override
    @Transactional
    public Response shortenUrlCustom(String originalUrl, String customKey, User user) {
        if (!customKey.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new WebApplicationException("Chave inválida", 400);
        }

        ShortUrlEvent event = new ShortUrlEvent();
        event.originalUrl = originalUrl;
        event.chave = customKey;
        event.userEmail = user.email;
        event.custom = true;

        try {
            String json = mapper.writeValueAsString(event);
            System.out.println("Enviando para o kafka: " + json);
            producer.send("url-shortener", json);

        } catch (Exception e) {
            System.err.println("Erro ao converter DTO para JSON: " + e.getMessage());
        }

        return Response.accepted().entity("URL personalizada em processamento.").build();
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

    private ShortURL criarShortUrl(String originalUrl, URLKey key, User user) {
        ShortURL shortUrl = new ShortURL();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setUrlKey(key);
        shortUrl.user = user;
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
