package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.RegisterRequest;
import dto.UserDTO;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Role;
import model.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import repository.UserRepository;
import security.JWTUtils;
import service.PasswordService;
import util.UserKafkaProducer;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    PasswordService passwordService;

    @Inject
    UserRepository userRepository;

    @Inject
    JWTUtils jwt;

    @Inject
    JsonWebToken jwtd;

    @Inject
    UserKafkaProducer producer;

    ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email já cadastrado")
                    .build();
        }
        UserDTO dto = new UserDTO();
        dto.userEmail =  registerRequest.email;
        dto.passwordHash = passwordService.hashPassword(registerRequest.password);
        dto.roles.add(Role.FREE.name()); // padrão

        try{
            String json = objectMapper.writeValueAsString(dto);
            producer.send("user-register", json);
            System.out.println("Mensagem enviada ao Kafka: " + json);
        }catch (Exception e){
            System.err.println("Erro ao converter User para JSON: " + e.getMessage());
        }

        return Response.status(Response.Status.CREATED)
                .entity("Usuário Registrado com sucesso")
                .build();
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(RegisterRequest registerRequest) {
        User user = userRepository.findByEmail(registerRequest.email);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(passwordService.checkPassword(registerRequest.password, user.passwordHash)) {
                return Response.status(Response.Status.OK)
                        .entity(jwt.generateToken(user))
                        .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @GET
    @Path("/jwt-debug")
    @Authenticated
    public String debugJwt() {
        if (jwtd == null) {
            return "JWT is null!";
        }
        return "JWT Subject: " + jwtd.getSubject() + "\nUPN: " + jwtd.getClaim("upn") + "\nGroups: " + jwtd.getGroups();
    }

}
