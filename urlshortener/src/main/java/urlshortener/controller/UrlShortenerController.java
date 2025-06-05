package urlshortener.controller;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.repository.ShortUrlRepository;
import urlshortener.repository.UrlKeyRepository;
import urlshortener.service.UrlShortenerService;
import urlshortener.util.UrlRequest;

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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shortenUrl(UrlRequest request) {
        if (request.originalUrl == null || request.originalUrl.isBlank()) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("URL original é obrigatória")
                    .build();
        }

        if (request.customKey != null && !request.customKey.isBlank()) {
            return service.shortenUrlCustom(request.originalUrl, request.customKey);
        } else {
            return service.shortenUrl(request.originalUrl);
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



}
