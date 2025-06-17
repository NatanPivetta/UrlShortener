package consumers;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import kafka.ShortenUrlEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ShortenUrlConsumer {

    @Incoming("url-shortener")
    @Blocking
    @Transactional
    public void consumeShortenUrl(ShortenUrlEvent event) {
        System.out.println("Recebido do tópico url-shortener: " + event.originalUrl);
    }
}
