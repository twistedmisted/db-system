package ua.zxz.multydbsysytem.web.payload.query;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateQueryRequest {

    private Condition condition;
    private Map<String, Object> object;
}
