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
    public List<String> roles;

    public UserResponseDTO(User user) {
        this.username = user.email;

        // Converte Set<Role> para List<String>
        Set<Role> userRoles = user.roles;
        if (userRoles != null) {
            this.roles = userRoles.stream()
                    .map(Enum::name) // ou role.getName() se for private
                    .collect(Collectors.toList());
        } else {
            this.roles = Collections.emptyList();
        }
    }
}
