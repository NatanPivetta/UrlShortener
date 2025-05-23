package service;

import client.KeyGenClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.URLKey;
import model.ShortURL;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Path("/shortener")
public class UrlShortenerService {

    @Inject
    @RestClient
   KeyGenClient kgc;

    private static final Random random = new Random();

    @POST
    @Path("/shorten")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public ShortURL shortenUrl(String originalUrl) {

        ShortURL existing = ShortURL.find("originalUrl", originalUrl).firstResult();
        if (existing != null) {
            return existing; // Já existe, retorna o mesmo ShortURL
        }else{
            // Busca chaves não utilizadas
            List<URLKey> availableKeys = URLKey.find("status = false").list();

            if (availableKeys.isEmpty()) {
                throw new WebApplicationException("No available keys", 400);
            }

            if(availableKeys.size() < 100){
                gerarChaves();
            }

            // Escolhe uma chave aleatoriamente
            URLKey selectedKey = availableKeys.get(random.nextInt(availableKeys.size()));

            selectedKey.setStatus(true);
            selectedKey.setData_atribuicao(Timestamp.valueOf(LocalDateTime.now()));
            selectedKey.setData_validade(Timestamp.valueOf(LocalDateTime.now().plusYears(1)));

            // Cria e persiste a nova URL encurtada
            ShortURL shortUrl = new ShortURL();
            shortUrl.originalUrl = originalUrl;
            shortUrl.urlKey = selectedKey;
            shortUrl.persist();

            return shortUrl;
        }


    }

    private void gerarChaves() {
        System.out.println("Solicitando geração de chaves...");
        Response response = kgc.generateKeys();
        if (response.getStatus() != 200) {
            throw new RuntimeException("Falha ao gerar chaves: " + response.readEntity(String.class));
        } else {
            System.out.println(response.readEntity(String.class));
        }
    }
}
