package urlshortener.dto;

public class ShortUrlEvent {
    public String originalUrl;
    public String chave;
    public String userEmail;
    public String userId;
    public boolean custom;
    public String expiresAt;
}

