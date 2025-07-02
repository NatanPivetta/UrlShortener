package model;


import io.quarkus.mongodb.panache.PanacheMongoEntity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.sql.Timestamp;

@MongoEntity(collection = "url_accesses")
public class UrlAccess extends PanacheMongoEntity {

    @BsonProperty("")
    public String short_key;
    public String ip;
    public String user_agent;

    public int responseCode;

    public String method;

    public Timestamp accessed_at;

    public UrlAccess(){}

    public String getChave() {
        return short_key;
    }

    public void setChave(String chave) {
        this.short_key = chave;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Timestamp getTimestamp() {
        return accessed_at;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.accessed_at = timestamp;
    }
}
