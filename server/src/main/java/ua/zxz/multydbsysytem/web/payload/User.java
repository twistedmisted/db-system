package ua.zxz.multydbsysytem.web.payload;

import lombok.Data;
import ua.zxz.multydbsysytem.dto.UserDto;

@Data
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
        this.username = userDto.getUsername();
        this.email = userDto.getEmail();
    }
}
