package urlshortener.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import urlshortener.model.Role;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;
import urlshortener.service.UrlShortenerService;
import urlshortener.util.UrlRequest;

import java.util.Set;

@Path("/shortener")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class UrlShortenerController {

    @Inject
    UrlShortenerService service;

    @Inject
    UrlKeyRepository urlKeyRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @POST
    @Path("/shorten")
    @RolesAllowed({"free", "silver", "gold"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shortenUrl(UrlRequest request, @Context SecurityContext ctx) {
        String userEmail = ctx.getUserPrincipal().getName();

        String role = ctx.isUserInRole("gold") ? "gold"
                : ctx.isUserInRole("silver") ? "silver"
                : "free";

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
            return service.shortenUrlCustom(request.originalUrl, request.customKey, request.userEmail);
        } else {
            return service.shortenUrl(request.originalUrl, request.userEmail);
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


    private boolean countUrls(String userEmail, String role){
        long userUrlCount = shortUrlRepository.countUrlsByUser(userEmail);

        int maxAllowed = switch (role) {
            case "gold" -> 50;
            case "silver" -> 10;
            default -> 5;
        };

        return userUrlCount <= maxAllowed;
    }


}
