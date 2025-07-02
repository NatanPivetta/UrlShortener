package dto;

import model.Role;
import model.ShortURL;
import model.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserResponseDTO {
    public String username;
    public List<ShortURL> urls;
    public long urlsCount;
    public String role;

    public UserResponseDTO(User user) {
        this.username = user.username;
        this.role = user.role;
        this.urlsCount = user.urlCount;
    }
}
