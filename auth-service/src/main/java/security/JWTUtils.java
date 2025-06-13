package security;


import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import model.User;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JWTUtils {

    public String generateToken(User user) {
        return Jwt.issuer("auth-service")
                .upn(user.email)
                .groups(user.roles.stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()))
                .expiresIn(Duration.ofHours(1))
                .sign();
    }
}

