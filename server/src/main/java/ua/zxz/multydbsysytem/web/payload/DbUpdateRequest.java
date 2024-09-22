package ua.zxz.multydbsysytem.web.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DbUpdateRequest {

    @NotNull(message = "The db name can't be null")
    @Size(min = 2, max = 256, message = "The length of db name is min = 2 and max = 256")
    private String dbName;
}
