package urlshortener.controller;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Role;
import urlshortener.model.User;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;
import urlshortener.repository.UserRepository;
import urlshortener.service.KeyGeneratorService;
import urlshortener.service.UrlShortenerService;
import urlshortener.util.UrlRequest;

@Path("/shortener")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UrlShortenerController {

    @Inject
    UrlShortenerService service;

    @Inject
    UrlKeyRepository urlKeyRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    KeyGeneratorService kgs;


    @POST
    @Path("/shorten")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response shortenUrl(UrlRequest request, @Context SecurityContext ctx) {

        System.out.println("originalUrl: " + request.originalUrl);
        System.out.println("customKey: " + request.customKey);



        if(urlKeyRepository.findAvailableKeys().size() < 1000){
            kgs.generateKeys(1000);
        }

        if (ctx.getUserPrincipal() == null) {
            System.out.println("User: " + ctx.getUserPrincipal());
            return Response.status(Response.Status.UNAUTHORIZED).entity("Usuário não autenticado").build();
        }

        if(shortUrlRepository.findByOriginalUrl(request.originalUrl)  != null){
            return Response.status(Response.Status.CONFLICT)
                    .entity("Url já encurtada no sistema: " + request.originalUrl)
                    .build();
        }
        if(urlKeyRepository.findByKey(request.customKey)  != null){
            return Response.status(Response.Status.CONFLICT)
                    .entity("Chave já utilizada no sistema: " + request.customKey)
                    .build();
        }

        String userEmail = ctx.getUserPrincipal().getName();
        User user = userRepository.findByEmail(userEmail);
        String role = user.roles.contains(Role.GOLD) ? "GOLD"
                : user.roles.contains(Role.SILVER) ? "SILVER"
                : "FREE";

        if (request.originalUrl == null || request.originalUrl.isBlank()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("URL original é obrigatória")
                    .build();
        }

        if(!countUrls(userEmail, role)){
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Limite de URLs encurtadas atingido para o plano: " + role)
                    .build();
        }

        if (request.customKey != null && !request.customKey.isBlank()) {
            return service.shortenUrlCustom(request.originalUrl, request.customKey, user);
        } else {
            return service.shortenUrl(request.originalUrl, user);
        }
    }

    @POST
    @Path("/unlink")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response unlinkUrl(String chave) {
        return service.unlink(chave);
    }


    @GET
    @Path("/testeauth")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public String testAuth(@Context SecurityContext ctx) {
        return "Usuário autenticado: " + ctx.getUserPrincipal().getName();
    }




    private boolean countUrls(String userEmail, String role){
        System.out.println(userEmail + " - " + role);
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return false;
        }

        long userUrlCount = shortUrlRepository.countByUserId(user.id);

        int maxAllowed = switch (role) {
            case "GOLD" -> 50;
            case "SILVER" -> 10;
            default -> 5;
        };

        return userUrlCount < maxAllowed;
    }


}
