package urlshortener.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

@MongoEntity(collection = "urls")
public class ShortURL extends PanacheMongoEntity {

    @BsonProperty("original_url")
    private String originalUrl;

    @BsonProperty("short_key")
    private String shortKey;

    @BsonProperty("user_id")
    private ObjectId userId;

    @BsonProperty("created_at")
    private Instant createdAt;

    @BsonProperty("numero_acessos")
    private Long numeroAcessos = 0L;

    @BsonProperty("status")
    private boolean status;

    @BsonProperty("expires_at")
    private Instant expiresAt;


    // Getters e Setters

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getNumeroAcessos() {
        return numeroAcessos;
    }

    public void addAcesso() {
        this.numeroAcessos += 1;
    }

    public boolean isStatus() {
        return status;
    }

    public void ativar() {
        this.status = true;
    }

    public void desativar() {
        this.status = false;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired(){
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

}
