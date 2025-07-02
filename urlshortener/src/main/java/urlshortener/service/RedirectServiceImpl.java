package urlshortener.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import urlshortener.dto.UrlAccessDTO;
import urlshortener.model.ShortURL;
import urlshortener.util.AccessKafkaProducer;
import urlshortener.util.RedisCacheService;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;


import java.sql.Timestamp;
import java.time.Instant;

@ApplicationScoped
public class RedirectServiceImpl implements RedirectService {

    @Inject
    RedisCacheService cache;

    @Inject
    AccessKafkaProducer kafkaProducer;

    ObjectMapper mapper = new ObjectMapper();

    public Response redirect(@PathParam("chave") String chave, HttpHeaders headers, UriInfo uriInfo, Request request) {
        System.out.println("Redirecting to " + chave);

        if ("favicon.ico".equals(chave)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        UrlAccessDTO dto = new UrlAccessDTO();
        dto.timestamp = Timestamp.from(Instant.now());
        dto.chave = chave;
        dto.method = request.getMethod();
        dto.ip = headers.getHeaderString("X-Forwarded-For");
        if (dto.ip == null) {
            dto.ip = headers.getRequestHeaders().getFirst("Host");
        }
        dto.userAgent = headers.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT);



        ShortURL shortUrl;

        String location = null;
        int statusCode;
        String errorMessage = null;

        String cachedUrl = cache.get(chave);
        if (cachedUrl != null) {
            location = cachedUrl;
            statusCode = Response.Status.FOUND.getStatusCode();
        } else {
            shortUrl = ShortURL.find("short_key = ?1", chave).firstResult();
            System.out.println(shortUrl.getOriginalUrl());

            if (shortUrl == null || shortUrl.isStatus()) {
                statusCode = Response.Status.NOT_FOUND.getStatusCode();
                errorMessage = "Chave não encontrada ou inativa";
            } else if (shortUrl.isExpired()) {
                statusCode = Response.Status.GONE.getStatusCode();
                errorMessage = "Chave expirada";
            } else {
                shortUrl = ShortURL.find("short_key = ?1", chave).firstResult();
                if (shortUrl == null) {
                    statusCode = Response.Status.NOT_FOUND.getStatusCode();
                    errorMessage = "URL encurtada não encontrada";
                } else {
                    location = shortUrl.getOriginalUrl();
                    statusCode = Response.Status.FOUND.getStatusCode();
                }
            }
        }

        dto.responseCode = String.valueOf(statusCode);

        try {
            String json = mapper.writeValueAsString(dto);
            System.out.println("Enviando para o kafka: " + json);
            kafkaProducer.send("url-access", chave, json);
        } catch (Exception e) {
            System.err.println("Erro ao converter DTO para JSON: " + e.getMessage());
        }

        if (statusCode == Response.Status.FOUND.getStatusCode()) {
            return Response.status(Response.Status.FOUND)
                    .header("Location", location)
                    .build();
        } else {
            return Response.status(statusCode)
                    .entity(errorMessage)
                    .build();
        }
    }



}
