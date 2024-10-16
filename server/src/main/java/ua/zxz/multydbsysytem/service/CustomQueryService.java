package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.QueryDto;
import ua.zxz.multydbsysytem.web.payload.query.ExecuteQueryReq;

import java.util.List;
import java.util.Map;

public interface CustomQueryService {

  QueryDto getById(long id);

  List<QueryDto> getAllByDb(long dbId);

  void save(QueryDto queryDto);

  void update(QueryDto queryDto);

  void delete(long id);

  Object execute(ExecuteQueryReq req);

  Object executeCustomQuery(long dbId, String queryName, Map<String, ?> request);
}
