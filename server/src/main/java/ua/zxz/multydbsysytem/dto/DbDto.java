package ua.zxz.multydbsysytem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DbDto {

    private Long id;
    private String name;
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private Timestamp createdAt;
    private DbStatus status;
    private DbTokenDto token;
    @JsonIgnore
    private UserDto user;
}
