package model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kafka.AccessEvent;

import java.sql.Timestamp;

@Entity
@Table(name = "url_acessos")
public class UrlAccess extends PanacheEntity {

    public String chave;
    public String ip;

    @Column(name = "user_agent")
    public String userAgent;
    @Column(name = "response_code")
    public int responseCode;

    public String method;

    public Timestamp timestamp;

    public UrlAccess(){}

   public void setEvent(AccessEvent event){
        this.chave = event.chave;
        this.ip = event.ip;
        this.userAgent = event.userAgent;
        this.responseCode = event.responseCode;
        this.method = event.method;
        this.timestamp = event.timestamp;
    }
}
