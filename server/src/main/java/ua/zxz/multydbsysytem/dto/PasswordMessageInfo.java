package ua.zxz.multydbsysytem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordMessageInfo {

    private String name;
    private String surname;
    private String email;
    private String password;
}
