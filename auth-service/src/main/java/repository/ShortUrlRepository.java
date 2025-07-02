package repository;


import model.User;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ApplicationScoped;
import model.ShortURL;
import model.User;
import org.bson.types.ObjectId;

import java.util.List;

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

    public List<ShortURL> findByUser(User user) {
        return find("userId", user.id).list();
    }


}
