package dto;

import model.ShortURL;
import model.User;

import java.util.List;

public class UserResponseDTO {
    String username;
    public List<ShortURL> urls;
    public long urlsCount;

    public UserResponseDTO(User user) {
        this.username = user.email;
    }
}
