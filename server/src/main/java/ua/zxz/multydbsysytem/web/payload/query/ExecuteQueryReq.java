package ua.zxz.multydbsysytem.web.payload.query;

import lombok.Data;

@Data
public class ExecuteQueryReq {
  private Long dbId;
  private String query;
}
