package ua.zxz.multydbsysytem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    public UserDto(String username) {
        this.username = username;
    }
}
