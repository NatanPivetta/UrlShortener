package urlshortener.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import urlshortener.model.URLKey;

import java.util.List;

@ApplicationScoped
public class UrlKeyRepository implements PanacheRepository<URLKey> {

    /**
     * Retorna todas as chaves disponíveis (status = false)
     */
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

    public void save(URLKey key) {
        key.persist();
    }
}
