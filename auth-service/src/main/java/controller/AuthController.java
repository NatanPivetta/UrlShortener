package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.RegisterRequest;
import dto.UserDTO;
import dto.UserResponseDTO;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import model.Role;
import model.TokenResponse;
import model.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import repository.ShortUrlRepository;
import repository.UserRepository;
import security.JWTUtils;
import service.PasswordService;
import util.UserKafkaProducer;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(RegisterRequest registerRequest) {
        User user = userRepository.findByEmail(registerRequest.email);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(passwordService.checkPassword(registerRequest.password, user.passwordHash)) {
            TokenResponse tk = new TokenResponse(jwt.generateToken(user));
            System.out.println("Token: " + tk.token);
                return Response.status(Response.Status.OK)
                        .entity(tk)
                        .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @GET
    @Path("/me")
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email);
        UserResponseDTO dto = new UserResponseDTO(user);
        dto.urls = shortUrlRepository.findByUser(user);
        dto.urlsCount = shortUrlRepository.countByUserId(user.id);

        return Response.ok(dto).build();
    }

    @POST
    @Path("/upgrade")
    @Authenticated
    @Transactional
    public Response upgrade(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não encontrado.")
                    .build();
        }

        List<Role> roleOrder = List.of(Role.FREE, Role.SILVER, Role.GOLD);
        Role nextRole = roleOrder.stream()
                .filter(role -> !user.roles.contains(role))
                .findFirst()
                .orElse(null);

        if (nextRole == null) {
            return Response.ok("Usuário já possui todas as roles.").build();
        }

//        user.roles.clear();
        user.roles.add(nextRole);
        userRepository.save(user);
        System.out.println("Role atualizada: " + user.email + " - " + user.roles);

        return Response.ok(Response.Status.OK)
                .entity("Role Atualizada para: " + nextRole)
                .build();

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
