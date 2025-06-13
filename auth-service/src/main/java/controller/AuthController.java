package controller;

import dto.RegisterRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Role;
import model.User;
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
        user.persist();
        user.roles.add(Role.FREE); // padrão

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
}
