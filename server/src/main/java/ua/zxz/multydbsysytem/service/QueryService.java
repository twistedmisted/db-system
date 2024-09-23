package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.web.payload.query.Condition;
import ua.zxz.multydbsysytem.web.payload.query.UpdateQueryRequest;

import java.util.List;
import java.util.Map;

public interface QueryService {

    List<Object> getByColumn(long dbId, String tableName, Condition request);

    List<Object> getAll(long dbId, String tableName);

    void save(long dbId, String tableName, Map<String, Object> object);

    void update(long dbId, String tableName, UpdateQueryRequest request);

    void delete(long dbId, String tableName, Condition condition);
}
