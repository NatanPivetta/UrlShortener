package urlshortener.repository;


import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import urlshortener.model.User;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {
        public User findByEmail(String userEmail) {
            return find("email", userEmail).firstResult();
        }
}
