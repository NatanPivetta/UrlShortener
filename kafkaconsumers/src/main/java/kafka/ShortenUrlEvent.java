package kafka;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortenUrlEvent {

    public String originalUrl;
    public String chave;
    public String userEmail;
    public boolean custom;

    public ShortenUrlEvent(){}

}
