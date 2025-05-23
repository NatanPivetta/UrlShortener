package service;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import model.ShortURL;
import model.URLKey;


import java.sql.Timestamp;
import java.time.LocalDateTime;

@Path("/")
public class RedirectService {


    @GET
    @Path("/{chave}")
    public Response redirect(@PathParam("chave") String chave) {
        URLKey urlKey = URLKey.find("chave = ?1", chave).firstResult();

        if (urlKey == null || !urlKey.isStatus()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Chave não encontrada ou inativa").build();
        }

        if (urlKey.getData_validade().before(Timestamp.valueOf(LocalDateTime.now()))) {
            return Response.status(Response.Status.GONE).entity("Chave expirada").build();
        }

        ShortURL shortUrl = ShortURL.find("urlKey = ?1", urlKey).firstResult();
        if (shortUrl == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("URL encurtada não encontrada").build();
        }

        return Response.status(Response.Status.FOUND) // Código 302
                .header("Location", shortUrl.originalUrl)
                .build();
    }
}
