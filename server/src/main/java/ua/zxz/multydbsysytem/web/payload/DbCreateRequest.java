package ua.zxz.multydbsysytem.web.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ua.zxz.multydbsysytem.dto.DbStatus;
import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DbCreateRequest {

    @NotNull(message = "The db name can't be null")
    @Size(min = 2, max = 256, message = "The length of db name is min = 2 and max = 256")
    private String dbName;

    @NotNull(message = "The tokenLifeTime can't be null")
    private DbTokenLifeTime tokenLifeTime;

    @JsonIgnore
    private DbStatus dbStatus = DbStatus.CREATING;
}
