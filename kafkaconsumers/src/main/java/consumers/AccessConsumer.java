package consumers;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import kafka.AccessEvent;
import model.ShortURL;
import model.UrlAccess;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;
import repository.ShortUrlRepository;
import repository.UrlAccessRepository;


@ApplicationScoped
public class AccessConsumer {

    @Inject
    UrlAccessRepository accessRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;


    @Incoming("url-access")
    @Blocking
    public void consumir(AccessEvent event) {
        System.out.println("Consuming event - chave: " + event.chave + ", responseCode: " + event.responseCode + ", method: " + event.method );

        if (event.responseCode != 302) return;

        String key = event.chave;
        if (key != null) {
            ShortURL shortUrl = ShortURL.find("short_key", key).firstResult();
            System.out.println("Buscando shortURL para chave: " + key + " -> Resultado: " + shortUrl);
            if (shortUrl != null) {
                System.out.println("ID do objeto antes de persistir: " + shortUrl.id);
                shortUrl.numeroAcessos += 1;
                shortUrl.update();
            }
            UrlAccess access = new UrlAccess();
            access.setChave(event.chave);
            access.setIp(event.ip);
            access.setMethod(event.method);
            access.setResponseCode(event.responseCode);
            access.setUser_agent(event.userAgent);
            access.setTimestamp(event.timestamp);
            accessRepository.save(access);
        }
    }
}
