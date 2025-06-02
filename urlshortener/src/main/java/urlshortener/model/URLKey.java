package urlshortener.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "chaves")
public class URLKey extends PanacheEntity {
    private String chave;
    private boolean status;
    private Timestamp data_criacao;
    private Timestamp data_atribuicao;
    private Timestamp data_validade;

    @Column(name = "numero_acessos")
    private Long numeroAcessos = 0L;


    //construtores
    public URLKey(){}
    public URLKey(String chave, boolean status) {
        this.chave = chave;
        this.status = status;
    }


    public Timestamp getData_atribuicao() {
        return data_atribuicao;
    }


    public Timestamp getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }

    public boolean isStatus() {
        return status;
    }


    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }


    public Timestamp getData_validade() {
        return data_validade;
    }

    public Long getNumeroAcessos() {
        return numeroAcessos;
    }
    public void addAcesso() {
        this.numeroAcessos += 1;
    }

    public void ativar() {
        this.status = true;
        this.data_atribuicao = Timestamp.valueOf(LocalDateTime.now());
        this.data_validade = Timestamp.valueOf(LocalDateTime.now().plusYears(1));
    }
}
