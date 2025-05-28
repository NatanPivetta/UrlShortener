package urlshortener.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import urlshortener.model.ShortURL;
import urlshortener.model.URLKey;
import urlshortener.util.RedisCacheService;


import java.sql.Timestamp;
import java.time.LocalDateTime;

@ApplicationScoped
public class RedirectServiceImpl implements RedirectService {

    @Inject
    RedisCacheService cache;

    @Transactional
    public Response redirect(@PathParam("chave") String chave) {

        URLKey urlKey;
        ShortURL shortUrl;

        String cachedUrl = cache.get(chave);
        if (cachedUrl != null) {
            System.out.println("Redis: " + cachedUrl);

            // Contabilizar o acesso para a chave existente em cache
            urlKey = URLKey.find("chave = ?1", chave).firstResult();
            if (urlKey != null) {
                urlKey.addAcesso();
            }

            shortUrl = ShortURL.find("urlKey = ?1", urlKey).firstResult();
            if (shortUrl != null) {
                shortUrl.addAcesso();
            }

            return Response.status(Response.Status.FOUND)
                    .header("Location", cachedUrl)
                    .build();
        } else {
            urlKey = URLKey.find("chave = ?1", chave).firstResult();

            if (urlKey == null || !urlKey.isStatus()) {
                return Response.status(Response.Status.NOT_FOUND).entity("Chave não encontrada ou inativa").build();
            }

            if (urlKey.getData_validade().before(Timestamp.valueOf(LocalDateTime.now()))) {
                return Response.status(Response.Status.GONE).entity("Chave expirada").build();
            }

            shortUrl = ShortURL.find("urlKey = ?1", urlKey).firstResult();
            if (shortUrl == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("URL encurtada não encontrada").build();
            }

            urlKey.addAcesso();
            shortUrl.addAcesso();

            return Response.status(Response.Status.FOUND)
                    .header("Location", shortUrl.originalUrl)
                    .entity(shortUrl.originalUrl)
                    .build();
        }
    }
}
