package repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.UrlAccess;


@ApplicationScoped

public class UrlAccessRepository implements PanacheMongoRepository<UrlAccess> {


    public void save(UrlAccess access) {
        access.persist();
    }
}
