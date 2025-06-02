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
import urlshortener.model.URLKey;
import urlshortener.util.AccessKafkaProducer;
import urlshortener.util.RedisCacheService;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Request;


import java.sql.Timestamp;
import java.time.LocalDateTime;

@ApplicationScoped
public class RedirectServiceImpl implements RedirectService {

    @Inject
    RedisCacheService cache;

    @Inject
    AccessKafkaProducer kafkaProducer;

    ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public Response redirect(@PathParam("chave") String chave, HttpHeaders headers, UriInfo uriInfo, Request request) {
        System.out.println("Redirecting to " + chave);

        if ("favicon.ico".equals(chave)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        UrlAccessDTO dto = new UrlAccessDTO();
        dto.chave = chave;
        dto.method = request.getMethod();
        dto.ip = headers.getHeaderString("X-Forwarded-For");
        if (dto.ip == null) {
            dto.ip = headers.getRequestHeaders().getFirst("Host");
        }
        dto.userAgent = headers.getRequestHeaders().getFirst(HttpHeaders.USER_AGENT);



        URLKey urlKey;
        ShortURL shortUrl;

        String location = null;
        int statusCode;
        String errorMessage = null;

        String cachedUrl = cache.get(chave);
        if (cachedUrl != null) {
            location = cachedUrl;
            statusCode = Response.Status.FOUND.getStatusCode();
        } else {
            urlKey = URLKey.find("chave = ?1", chave).firstResult();

            if (urlKey == null || !urlKey.isStatus()) {
                statusCode = Response.Status.NOT_FOUND.getStatusCode();
                errorMessage = "Chave não encontrada ou inativa";
            } else if (urlKey.getData_validade().before(Timestamp.valueOf(LocalDateTime.now()))) {
                statusCode = Response.Status.GONE.getStatusCode();
                errorMessage = "Chave expirada";
            } else {
                shortUrl = ShortURL.find("urlKey = ?1", urlKey).firstResult();
                if (shortUrl == null) {
                    statusCode = Response.Status.NOT_FOUND.getStatusCode();
                    errorMessage = "URL encurtada não encontrada";
                } else {
                    location = shortUrl.originalUrl;
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
