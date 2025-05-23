package service;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.URLKey;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Path("/keygenservice")
public class KeyGen {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int)(num % 62);
            sb.append(BASE62.charAt(remainder));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    @GET
    @Path("/generatekeys")
    @Transactional
    public Response GenerateKeys() {

        URLKey ultimaChave = URLKey.find("ORDER BY id DESC").firstResult();
        long startId;

        if (ultimaChave == null) {
            startId = 1000000L; // Primeiro ID se não houver chaves
        } else {
            startId = 1000000L + ultimaChave.id + 1; // Começa após o último ID
        }

        System.out.println("Generating keys starting from ID: " + startId);
        String lastKey = null;

        try {
            for (int i = 0; i < 1000; i++) {
                URLKey urlKey = new URLKey();
                String key = encodeBase62(startId + i);
                urlKey.setChave(key);
                urlKey.setData_criacao(Timestamp.valueOf(LocalDateTime.now()));
                urlKey.persist();
                lastKey = key;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao gerar as chaves: " + e.getMessage())
                    .build();
        }

        System.out.println("Last Key generated: " + lastKey);
        return Response.ok("Chaves geradas com sucesso! Última chave: " + lastKey).build();
    }


}
