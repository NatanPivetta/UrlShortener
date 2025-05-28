package urlshortener.util;


import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.inject.Inject;


@ApplicationScoped
public class RedisCacheService {



    ValueCommands<String, String> commands;

    @Inject
    public RedisCacheService(RedisDataSource redisDataSource) {
        this.commands = redisDataSource.value(String.class);
    }



    public void put(String chave, String url) {
        commands.setex("url: " + chave,86400 , url);
    }

    public String get(String chave) {
        return commands.get("url:" + chave);
    }

    public void delete(String chave) {
        commands.getdel("url:" + chave);
    }
}
