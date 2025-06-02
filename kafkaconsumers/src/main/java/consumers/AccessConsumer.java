package consumers;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kafka.AccessEvent;
import model.ShortURL;
import model.URLKey;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.reactive.messaging.annotations.Blocking;



@ApplicationScoped
public class AccessConsumer {


    @Incoming("url-access")
    @Blocking
    @Transactional
    public void consumir(AccessEvent event) {
        System.out.println("Consuming event - chave: " + event.chave + ", responseCode: " + event.responseCode + ", method: " + event.method );

        if (event.responseCode != 302) return;

        URLKey key = URLKey.find("chave", event.chave).firstResult();
        if (key != null) {
            key.addAcesso();
            key.persist();
            ShortURL shortUrl = ShortURL.find("urlKey", key).firstResult();
            if (shortUrl != null) {
                shortUrl.addAcesso();
                shortUrl.persist();
            }

        }
    }
}
