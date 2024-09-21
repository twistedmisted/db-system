package ua.zxz.multydbsysytem.web.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TableFieldPayload {

    @NotNull(message = "The table field can't be null")
    @Size(min = 1, max = 255, message = "The length of field name can be between 1 and 255 characters")
    private String name;
}
