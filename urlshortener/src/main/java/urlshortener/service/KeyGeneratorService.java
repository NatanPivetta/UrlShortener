package urlshortener.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;

@ApplicationScoped
public class KeyGeneratorService {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom random = new SecureRandom();
    private static final int KEY_LENGTH = 6; // Pode ser ajustado conforme necessário (ex: 6-8 chars)

    public String generateKey(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(BASE62.length());
            sb.append(BASE62.charAt(index));
        }
        return sb.toString();
    }
}
