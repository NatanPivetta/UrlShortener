package controller;

import dto.RegisterRequest;
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

    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email já cadastrado")
                    .build();
        }
        User user = new User();
        user.email =  registerRequest.email;
        user.passwordHash = passwordService.hashPassword(registerRequest.password);
        user.roles.add(Role.FREE); // padrão
        user.persist();

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
