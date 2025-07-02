package consumers;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kafka.ShortenUrlEvent;
import model.ShortURL;
import model.User;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import repository.ShortUrlRepository;
import repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@ApplicationScoped
public class ShortenUrlConsumer {

    @Inject
    UserRepository userRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;


    @Incoming("url-shortener")
    @Blocking
    public void consumeShortenUrl(ShortenUrlEvent event) {
        System.out.println("Recebido do tópico url-shortener: " + event.originalUrl);

        User user = userRepository.findByEmail(event.userEmail);
        if (user == null) {
            System.err.println("Usuário não encontrado: " + event.userEmail);
            return;
        }


        ShortURL shortURL = criarShortUrl(event.originalUrl, event.chave, user);
        shortUrlRepository.save(shortURL);


        user.urlCount += 1;
        user.update();
    }

    private ShortURL criarShortUrl(String originalUrl, String key, User user) {
        ShortURL shortUrl = new ShortURL();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setShortKey(key);
        shortUrl.setUserId(user.id);
        shortUrl.setCreatedAt(Instant.now());
        shortUrl.setExpiresAt(Instant.now().plus(365, ChronoUnit.DAYS));
        shortUrl.ativar();
        return shortUrl;
    }
}


