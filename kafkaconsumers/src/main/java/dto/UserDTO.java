package dto;

import java.util.List;

public class UserDTO {
    public String userEmail;
    public String passwordHash;
    public int urlCount;
    public List<String> roles;
    public UserDTO() {}
}
