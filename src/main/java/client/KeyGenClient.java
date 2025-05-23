package client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/keygenservice")
@RegisterRestClient(configKey = "keygen-api")
public interface KeyGenClient {

    @GET
    @Path("/generatekeys")
    Response generateKeys(); // pode retornar um objeto, se quiser
}