package ua.zxz.multydbsysytem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryDto {

  private Long id;

  @NotNull(message = "Database id can't be null")
  private Long dbId;

  @Size(min = 1, max = 250, message = "The length of query name must be from 1 to 250 characters")
  @NotNull(message = "Query name can't be null")
  @NotBlank(message = "Need to specify query name")
  private String queryName;

  @NotNull(message = "Query can't be null")
  @NotBlank(message = "Need to enter query")
  private String query;
}
