package kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import model.Role;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterEvent {
    public String userEmail;
    public String passwordHash;
    public int urlCount;
    public Set<Role> roles = new HashSet<>();

    public UserRegisterEvent() {}
}
