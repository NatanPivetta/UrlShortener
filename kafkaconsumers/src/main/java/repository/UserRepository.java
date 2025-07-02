package repository;


import io.quarkus.mongodb.panache.PanacheMongoRepository;
import model.User;

import jakarta.enterprise.context.ApplicationScoped;



@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {
    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public void save(User user) {
        persist(user);
    }
}

