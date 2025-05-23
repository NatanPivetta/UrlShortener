package model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "chaves")
public class URLKey extends PanacheEntity {
    private String chave;
    private boolean status;
    private Timestamp data_criacao;
    private Timestamp data_atribuicao;
    private Timestamp valido_ate;


    //construtores
    public URLKey(){}
    public URLKey(String chave, boolean status) {
        this.chave = chave;
        this.status = status;
    }


    public Timestamp getData_atribuicao() {
        return data_atribuicao;
    }

    public void setData_atribuicao(Timestamp data_atribuicao) {
        this.data_atribuicao = data_atribuicao;
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public void setData_validade(Timestamp timestamp) {
        this.valido_ate = timestamp;
    }

    public Timestamp getData_validade() {
        return valido_ate;
    }
}
