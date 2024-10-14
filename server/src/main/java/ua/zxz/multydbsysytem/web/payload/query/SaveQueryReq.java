package ua.zxz.multydbsysytem.web.payload.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveQueryReq {
  @NotNull(message = "Database id can't be null")
  private Long dbId;

  @NotNull(message = "Query name can't be null")
  @NotBlank(message = "Need to specify query name")
  private String queryName;

  @NotNull(message = "Query can't be null")
  @NotBlank(message = "Need to enter query")
  private String query;
}
