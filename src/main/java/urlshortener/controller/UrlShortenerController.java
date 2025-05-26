package urlshortener.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import urlshortener.model.ShortURL;
import urlshortener.service.UrlShortenerService;

@Path("/shortener")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class UrlShortenerController {

    @Inject
    UrlShortenerService service;

    @POST
    @Path("/shorten")
    public ShortURL shorten(String originalUrl) {
        return service.shortenUrl(originalUrl);
    }
}
