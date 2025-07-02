package consumers;

import dto.UserDTO;
import jakarta.enterprise.context.ApplicationScoped;
import model.Role;
import model.User;
import kafka.UserRegisterEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import repository.UserRepository;

import java.util.stream.Collectors;

@ApplicationScoped
public class UserConsumer {

    @Inject
    UserRepository userRepository;

    @Incoming("user-register")
    @Blocking
    public void consumeUserRegister(UserRegisterEvent event) {
        System.out.println("Consuming user register event: " + event.email);

        User user = new User();
        user.email = event.email;
        user.passwordHash = event.passwordHash;
        user.urlCount = 0L;
        user.role = event.role;
        user.username = event.username;

        userRepository.save(user);
    }
}
