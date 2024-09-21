package ua.zxz.multydbsysytem.web.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Необхідно ввести псевдонім.")
    private String username;

    @NotBlank(message = "Необіхдно ввести пароль.")
    @Size(min = 8, message = "Пароль введено некоректно.")
    private String password;
}
