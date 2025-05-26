package urlshortener.service;

import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import urlshortener.model.ShortURL;
import urlshortener.util.RedisCacheService;

import java.util.List;

public class TopUrlScheduler {

    @Inject
    RedisCacheService redis;

    @Scheduled(every = "1m")
    @Transactional
    public void updateTopAccessUrls(){
        List<ShortURL> topUrls = ShortURL.find(
                "urlKey.status = true and numeroAcessos > 0 order by numeroAcessos desc"
        ).list();

        if (topUrls.isEmpty()) return;

        int topCount = Math.max(1, (int) (topUrls.size() * 0.2));
        List<ShortURL> top20Percent = topUrls.subList(0, topCount);

        for (ShortURL url : top20Percent) {
            redis.put(url.urlKey.getChave(), url.originalUrl);
        }

        // System.out.println("Cache atualizado com top " + topCount + " URLs ativas mais acessadas");

    }
}
