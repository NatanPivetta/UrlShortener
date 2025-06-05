package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import model.UrlAccess;


@ApplicationScoped
public class UrlAccessRepository implements PanacheRepository<UrlAccess> {


    public void save(UrlAccess access) {
        access.persist();
    }
}
