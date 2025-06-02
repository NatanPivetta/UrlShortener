package urlshortener.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import urlshortener.model.ShortURL;

@ApplicationScoped
public class ShortUrlRepository implements PanacheRepository<ShortURL> {

    /**
     * Busca uma ShortURL existente pela URL original
     */
    public ShortURL findByOriginalUrl(String originalUrl) {
        return find("originalUrl", originalUrl).firstResult();
    }


    public void persist(ShortURL shortUrl) {
        shortUrl.persist();
    }
}
