package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.QueryDto;
import ua.zxz.multydbsysytem.entity.QueryEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.repository.QueryRepository;
import ua.zxz.multydbsysytem.service.CustomQueryService;

@Service
@RequiredArgsConstructor
public class CustomQueryServiceImpl implements CustomQueryService {

  private final QueryRepository queryRepository;
  private final DbRepository dbRepository;

  @Override
  public void save(QueryDto queryDto) {
    if (existsByName(queryDto.getDbId(), queryDto.getQueryName())) {
      throw new WrongDataException("The query with this name already exists in this database");
    }
    QueryEntity queryEntity = new QueryEntity();
    queryEntity.setQueryName(queryDto.getQueryName());
    queryEntity.setQuery(queryDto.getQuery());
    queryEntity.setDb(dbRepository.findById(queryDto.getDbId()).get());
    queryRepository.save(queryEntity);
  }

  private boolean existsByName(Long dbId, String queryName) {
    return queryRepository.existsByDbIdAndQueryName(dbId, queryName);
  }
}
