package kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AccessEvent extends PanacheEntity {
    public String chave;
    public String ip;
    public String userAgent;
    public int responseCode;
    public String method;
    public Timestamp timestamp;

    public AccessEvent() {}
}
