package urlshortener.util;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class RedisCacheService {

    @Inject
    RedisClient redisClient;

    public void put(String chave, String url) {
        redisClient.setex("url:" + chave, "86400", url);
    }

    public String get(String chave) {
        Response response = redisClient.get("url:" + chave);
        return response == null ? null : response.toString();
    }

    public void delete(String chave) {
        redisClient.del(List.of("url:" + chave));
    }
}
