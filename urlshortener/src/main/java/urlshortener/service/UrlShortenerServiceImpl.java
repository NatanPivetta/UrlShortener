package urlshortener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import urlshortener.dto.ShortUrlEvent;
import urlshortener.model.ShortURL;
import urlshortener.model.User;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.util.UrlShortenerProducer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Inject
    KeyGeneratorService kgs;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    UrlShortenerProducer producer;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Response shortenUrl(String originalUrl, User user) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new WebApplicationException("URL original é obrigatória.", 400);
        }

        ShortUrlEvent event = new ShortUrlEvent();
        event.originalUrl = originalUrl;
        event.userId = user.id.toString();
        event.userEmail = user.email;
        event.custom = false;
        event.expiresAt = Instant.now().plus(365, ChronoUnit.DAYS).toString(); // Expiração padrão de 1 ano

        try {
            String json = mapper.writeValueAsString(event);
            System.out.println("[URL SHORTENER] Enviando para o Kafka: " + json);
            producer.send("url-shortener", json);
        } catch (Exception e) {
            System.err.println("[URL SHORTENER] Erro ao converter evento para JSON: " + e.getMessage());
            throw new WebApplicationException("Erro interno ao processar a URL.", 500);
        }

        return Response.accepted().entity("URL em processamento.").build();
    }

    @Override
    public Response shortenUrlCustom(String originalUrl, String customKey, User user) {
        if (!customKey.matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new WebApplicationException("Chave personalizada inválida (mínimo 4, máximo 20 caracteres alfanuméricos).", 400);
        }

        ShortUrlEvent event = new ShortUrlEvent();
        event.originalUrl = originalUrl;
        event.chave = customKey;
        event.userId = user.id.toString();
        event.userEmail = user.email;
        event.custom = true;
        event.expiresAt = Instant.now().plus(365, ChronoUnit.DAYS).toString(); // Expiração padrão
        try {
            String json = mapper.writeValueAsString(event);
            System.out.println("[URL SHORTENER] Enviando URL personalizada para o Kafka: " + json);
            producer.send("url-shortener", json);
        } catch (Exception e) {
            System.err.println("[URL SHORTENER] Erro ao converter evento personalizado para JSON: " + e.getMessage());
            throw new WebApplicationException("Erro interno ao processar a URL personalizada.", 500);
        }

        return Response.accepted().entity("URL personalizada em processamento.").build();
    }

    @Override
    public Response unlink(String chave) {
        ShortURL shortURL = shortUrlRepository.findByKey(chave);
        if (shortURL == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Chave não encontrada.")
                    .build();
        }

        shortURL.desativar();
        shortUrlRepository.persist(shortURL);

        return Response.ok()
                .entity("Chave e URL desativadas com sucesso.")
                .build();
    }

}
