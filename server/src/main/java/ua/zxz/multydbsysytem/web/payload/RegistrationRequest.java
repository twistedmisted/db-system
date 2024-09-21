package ua.zxz.multydbsysytem.web.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotNull(message = "The first name can't be null")
    @NotBlank(message = "The first name can't be empty")
    @Size(max = 50, message = "The max length of first name is 50 characters")
    private String firstName;

    @NotNull(message = "The last name can't be null")
    @NotBlank(message = "The last name can't be empty")
    @Size(max = 50, message = "The max length of last name is 50 characters")
    private String lastName;

    @NotNull(message = "The username can't be null")
    @NotBlank(message = "The username can't be empty")
    @Size(min = 2, max = 255, message = "The username length can be between 2 and 256 characters")
    private String username;

    @NotNull(message = "The email can't be null")
    @Email(message = "It is not an email")
    private String email;

    @Size(min = 8, message = "The minimum length of password is 8 characters")
    private String password;
}
