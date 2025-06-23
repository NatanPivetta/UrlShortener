package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.URLKey;

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
        return findByKey(key) != null;
    }

    public void save(URLKey key) {
        key.persist();
    }
}

