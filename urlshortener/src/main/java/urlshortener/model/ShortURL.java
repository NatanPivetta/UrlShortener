package urlshortener.model;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name= "short_url")
public class ShortURL extends PanacheEntity {

    @Column(nullable = false, name="original_url")
    private String originalUrl;

    @ManyToOne
    @JoinColumn(name = "url_key_id", nullable = false, unique = true)
    private URLKey urlKey;

    @Column(name = "numero_acessos")
    private Long numeroAcessos = 0L;

    private boolean status;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public URLKey getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(URLKey urlKey) {
        this.urlKey = urlKey;
    }

    public Long getNumeroAcessos() {
        return numeroAcessos;
    }
    public void addAcesso() {
        this.numeroAcessos += 1;
    }

    public void ativar(){this.status = true;}
    public void desativar(){this.status = false;}
}
