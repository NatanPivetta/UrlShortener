package kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessEvent {
    public String chave;
    public String ip;
    public String userAgent;
    public int responseCode;
    public String method;

    public AccessEvent() {}
}
