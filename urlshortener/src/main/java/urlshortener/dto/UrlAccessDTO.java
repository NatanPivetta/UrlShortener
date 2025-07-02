package urlshortener.dto;


import java.sql.Timestamp;
import java.time.Instant;

public class UrlAccessDTO {
    public String chave;
    public String ip;
    public String userAgent;
    public String responseCode;
    public String method;
    public Timestamp timestamp;
}

