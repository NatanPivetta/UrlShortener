package consumers;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import kafka.ShortenUrlEvent;
import model.ShortURL;
import model.URLKey;
import model.User;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import repository.ShortUrlRepository;
import repository.UrlKeyRepository;
import repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class ShortenUrlConsumer {

    @Inject
    UserRepository userRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    UrlKeyRepository urlKeyRepository;

    @Incoming("url-shortener")
    @Blocking
    @Transactional
    public void consumeShortenUrl(ShortenUrlEvent event) {
        System.out.println("Recebido do tópico url-shortener: " + event.originalUrl);

        User user = userRepository.findByEmail(event.userEmail);


        URLKey key;
        if(!event.custom && event.chave == null){
            List<URLKey> availableKeys = urlKeyRepository.findAvailableKeys();
            // Engatilhar novas chaves caso lista esteja vazia ou pequena

            Random random = new Random();
            key = availableKeys.get(random.nextInt(availableKeys.size()));
            key.ativar();
        }else{
            key = criarUrlKey(event.chave);
            urlKeyRepository.save(key);
        }

        ShortURL shortURL = criarShortUrl(event.originalUrl, key, user);
        shortUrlRepository.save(shortURL);


        user.urlCount += 1;
        user.persist();
    }

    private ShortURL criarShortUrl(String originalUrl, URLKey key, User user) {
        ShortURL shortUrl = new ShortURL();
        shortUrl.setOriginalUrl(originalUrl);
        shortUrl.setUrlKey(key);
        shortUrl.user = user;
        shortUrl.ativar();
        return shortUrl;
    }

    private URLKey criarUrlKey(String customKey ) {
        URLKey customKeyURL = new URLKey();
        customKeyURL.setChave(customKey);
        customKeyURL.ativar();
        customKeyURL.setData_criacao(Timestamp.valueOf(LocalDateTime.now()));
        return customKeyURL;
    }
}


