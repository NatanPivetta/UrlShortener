package kafka;

import java.sql.Timestamp;


public class AccessEvent {
    public String chave;
    public String ip;
    public String userAgent;
    public int responseCode;
    public String method;
    public Timestamp timestamp;

    public AccessEvent() {}
}
