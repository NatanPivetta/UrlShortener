package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.ShortURL;
import model.User;

import java.util.List;

@ApplicationScoped
public class ShortUrlRepository implements PanacheRepository<ShortURL> {

    public ShortURL findByOriginalUrl(String originalUrl) {
        return find("originalUrl", originalUrl).firstResult();
    }

    public ShortURL findByKey(String chave) {
        return find("urlKey.chave = ?1", chave).firstResult();
    }

    public void save(ShortURL shortUrl) {
        shortUrl.persist();
    }

    public long countByUserId(Long userId) {
        return count("user.id", userId);
    }

    public List<ShortURL> findByUser(User user) {
        return find("user", user).list();
    }


}
