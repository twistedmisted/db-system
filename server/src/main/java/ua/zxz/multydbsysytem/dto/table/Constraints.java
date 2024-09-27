package ua.zxz.multydbsysytem.dto.table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Constraints {
    private boolean primaryKey;
    private boolean identity;
    private boolean autoIncrement;
    private boolean notNull;
    private boolean unique;

    @JsonProperty("foreignTable")
    private ForeignTableDto foreignTable;
}
