package urlshortener.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import urlshortener.model.ShortURL;

@ApplicationScoped
public class ShortUrlRepository implements PanacheMongoRepository<ShortURL> {

    public ShortURL findByOriginalUrl(String originalUrl) {
        return find("originalUrl", originalUrl).firstResult();
    }

    public ShortURL findByKey(String chave) {
        return find("short_key = ?1", chave).firstResult();
    }

    public long countByUserId(ObjectId userId) {
        return count("user.id", userId);
    }


}
