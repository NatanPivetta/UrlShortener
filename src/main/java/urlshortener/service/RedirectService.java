package urlshortener.service;

import jakarta.ws.rs.core.Response;

public interface RedirectService {

    Response redirect(String chave);
}
