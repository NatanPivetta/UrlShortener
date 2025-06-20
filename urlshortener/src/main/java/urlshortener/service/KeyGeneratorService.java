package urlshortener.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import urlshortener.model.URLKey;
import urlshortener.repository.UrlKeyRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@ApplicationScoped
public class KeyGeneratorService {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Inject
    UrlKeyRepository urlKeyRepository;

    private static String encodeBase62(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int)(num % 62);
            sb.append(BASE62.charAt(remainder));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    @Transactional
    public void generateKeys(int quantidade) {
        URLKey ultimaChave = URLKey.find("ORDER BY id DESC").firstResult();
        long startId = (ultimaChave == null) ? 1000000L : 1000000L + ultimaChave.id;


        for (int i = 0; i < quantidade; i++) {
            URLKey urlKey = new URLKey();
            String key = encodeBase62(startId + i);
            if(!urlKeyRepository.existsByKey((key))){
                urlKey.setChave(key);
                urlKey.setData_criacao(Timestamp.valueOf(LocalDateTime.now()));
                urlKey.persist();
                System.out.println("Chave gerada: " + urlKey.getChave());
            }else{
                System.out.println("Chave " + key + "Já existe");
            }

        }
    }

}
