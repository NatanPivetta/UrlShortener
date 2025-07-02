package urlshortener.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.HashSet;
import java.util.Set;

@MongoEntity(collection = "users")
public class User extends PanacheMongoEntity {

    public String email;
    public String username;
    public String passwordHash;
    public String role;
    public Long urlCount;
}