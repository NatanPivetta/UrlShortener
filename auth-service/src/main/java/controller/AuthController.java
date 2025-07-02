package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.RegisterRequest;
import dto.UserDTO;
import dto.UserResponseDTO;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Role;
import model.ShortURL;
import model.TokenResponse;
import model.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import repository.ShortUrlRepository;
import repository.UserRepository;
import security.JWTUtils;
import service.PasswordService;
import util.UserKafkaProducer;

import java.util.Collections;
import java.util.List;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    PasswordService passwordService;

    @Inject
    UserRepository userRepository;

    @Inject
    ShortUrlRepository shortUrlRepository;

    @Inject
    JWTUtils jwt;

    @Inject
    JsonWebToken jwtd;

    @Inject
    UserKafkaProducer producer;

    ObjectMapper objectMapper = new ObjectMapper();

    @POST
    @Path("/register")
    public Response register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email já cadastrado")
                    .build();
        }
        UserDTO dto = new UserDTO();
        dto.email =  registerRequest.email;
        dto.username = registerRequest.username;
        dto.passwordHash = passwordService.hashPassword(registerRequest.password);
        dto.role = Role.FREE.name();

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(RegisterRequest registerRequest) {

        User user = userRepository.findByEmail(registerRequest.email);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(passwordService.checkPassword(registerRequest.password, user.passwordHash)) {
            TokenResponse tk = new TokenResponse(jwt.generateToken(user));
            System.out.println("Login para: " + registerRequest.email);
            return Response.status(Response.Status.OK)
                        .entity(tk)
                        .build();
        }
        System.out.println("Erro ao tentar fazer login para: " + registerRequest.email);
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @GET
    @Path("/me")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        System.out.println("Buscando informações do user: " + email);
        User user = userRepository.findByEmail(email);

        UserResponseDTO dto = new UserResponseDTO(user);
        List<ShortURL> urls = shortUrlRepository.findByUser(user);
        dto.urls = urls != null ? urls : Collections.emptyList();

        dto.urlsCount = user.urlCount;
        System.out.println("Login: " + dto.role + " - " + dto.username);



        return Response.ok(dto).build();
    }

    @POST
    @Path("/upgrade")
    @Authenticated
    public Response upgrade(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado.")
                    .build();
        }

        List<String> rolesOrder = List.of("FREE", "SILVER", "GOLD");
        int currentIndex = rolesOrder.indexOf(user.role);
        if (currentIndex == -1 || currentIndex == rolesOrder.size() - 1) {
            return Response.ok("Usuário já possui o plano mais alto.").build();
        }

        String nextRole = rolesOrder.get(currentIndex + 1);
        user.role = nextRole;
        userRepository.update(user);

        System.out.println("Role atualizada: " + user.email + " - " + user.role);
        return Response.ok("Plano atualizado para: " + nextRole).build();
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
