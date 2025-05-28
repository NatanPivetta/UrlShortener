package urlshortener.controller;


import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import urlshortener.service.RedirectService;


@Path("/")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class RedirectController {

    @Inject
    RedirectService service;

    @GET
    @Path("/{chave}")
    @Transactional
    public Response redirect(@PathParam("chave") String chave) {
        return service.redirect(chave);
    }

}
