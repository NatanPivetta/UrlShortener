package urlshortener.controller;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import urlshortener.model.User;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UserRepository;
import urlshortener.service.KeyGeneratorService;
import urlshortener.service.UrlShortenerService;
import urlshortener.util.UrlRequest;

import java.util.Random;

@Path("/shortener")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UrlShortenerController {

    @Inject
    UrlShortenerService service;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    KeyGeneratorService kgs;

    @POST
    @Path("/shorten")
    @Authenticated
    public Response shortenUrl(UrlRequest request, @Context SecurityContext ctx) {
        if (ctx.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Usuário não autenticado").build();
        }

        String userEmail = ctx.getUserPrincipal().getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Usuário não encontrado").build();
        }

        String role = user.role;

        if (request.originalUrl == null || request.originalUrl.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("URL original é obrigatória").build();
        }

        if (!countUrls(userEmail, role)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Limite de URLs encurtadas atingido para o plano: " + role)
                    .build();
        }

        if (shortUrlRepository.findByOriginalUrl(request.originalUrl) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("URL já encurtada no sistema: " + request.originalUrl)
                    .build();
        }

        // Se o usuário forneceu uma chave customizada
        if (request.customKey != null && !request.customKey.isBlank()) {
            if (!request.customKey.matches("^[a-zA-Z0-9]{4,20}$")) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Chave personalizada inválida").build();
            }

            if (shortUrlRepository.findByKey(request.customKey) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Chave personalizada já está em uso: " + request.customKey)
                        .build();
            }

            return service.shortenUrlCustom(request.originalUrl, request.customKey, user);
        }

        // Gera uma chave base62 única sob demanda
        String generatedKey;
        int attempt = 0;
        do {
            int random = new Random().nextInt(4,15);
            generatedKey = kgs.generateKey(random);
            attempt++;
            // Segurança extra para evitar colisão (improvável, mas possível)
        } while (shortUrlRepository.findByKey(generatedKey) != null && attempt < 5);

        if (attempt == 5) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Falha ao gerar chave única para URL")
                    .build();
        }

        return service.shortenUrlCustom(request.originalUrl, generatedKey, user);
    }

    @POST
    @Path("/unlink")
    public Response unlinkUrl(String chave) {
        return service.unlink(chave);
    }

    @GET
    @Path("/testeauth")
    @Authenticated
    public String testAuth(@Context SecurityContext ctx) {
        return "Usuário autenticado: " + ctx.getUserPrincipal().getName();
    }

    private boolean countUrls(String userEmail, String role) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) return false;

        long userUrlCount = shortUrlRepository.countByUserId(user.id);

        int maxAllowed = switch (role) {
            case "GOLD" -> 50;
            case "SILVER" -> 10;
            default -> 5;
        };

        return userUrlCount < maxAllowed;
    }
}
