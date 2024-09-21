package ua.zxz.multydbsysytem.web.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;

@Data
public class DbTokenPayload {

    @NotNull(message = "Database id can't be null")
    private Long dbId;

    @NotNull(message = "Database token life can't be null")
    private DbTokenLifeTime lifeTime;
}
