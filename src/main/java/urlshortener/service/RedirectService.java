package urlshortener.service;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public interface RedirectService {

    Response redirect(String chave, HttpHeaders headers, UriInfo uriInfo, Request request);
}
