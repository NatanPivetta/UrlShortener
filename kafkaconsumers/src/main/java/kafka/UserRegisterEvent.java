package kafka;

public class UserRegisterEvent {
    public String email;
    public String passwordHash;
    public Long urlCount;
    public String username;
    public String role;

    public UserRegisterEvent() {}
}
