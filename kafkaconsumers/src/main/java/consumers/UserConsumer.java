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
    @Transactional
    public void consumeUserRegister(UserRegisterEvent event) {
        System.out.println("Consuming user register event: " + event.userEmail);

        User user = new User();
        user.email = event.userEmail;
        user.passwordHash = event.passwordHash;
        user.urlCount = 0;
        user.roles = event.roles;

        userRepository.save(user);
    }
}
