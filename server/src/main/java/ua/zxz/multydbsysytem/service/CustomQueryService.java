package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.QueryDto;

import java.util.List;

public interface CustomQueryService {

  QueryDto getById(long id);

  List<QueryDto> getAllByDb(long dbId);

  void save(QueryDto queryDto);

  void update(QueryDto queryDto);

  void delete(long id);
}
