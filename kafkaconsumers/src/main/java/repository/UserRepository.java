package repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import model.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    public User findByEmail(String userEmail) {
        return find("email", userEmail).firstResult();
    }

    public void save(User user) {
        persist(user);
    }

}
