package urlshortener.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import urlshortener.model.URLKey;

import java.util.List;

@ApplicationScoped
public class UrlKeyRepository implements PanacheRepository<URLKey> {


    public List<URLKey> findAvailableKeys() {
        return find("status = false").list();
    }

    public URLKey findByKey(String key) {
        List<URLKey> keylist = find("chave = ?1", key).list();

        if(keylist.isEmpty()) {
            return null;
        }
        return keylist.getFirst();
    }

    public boolean existsByKey(String key) {
        boolean exists = findByKey(key) != null;
        return exists;
    }

    public void save(URLKey key) {
        key.persist();
    }
}
