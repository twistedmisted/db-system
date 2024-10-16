package ua.zxz.multydbsysytem.service.impl;

import java.util.ArrayList;
import java.util.List;
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
  public QueryDto getById(long id) {
    QueryEntity queryEntity = getQueryEntity(id);
    return mapQueryEntityToDto(queryEntity);
  }

  private QueryEntity getQueryEntity(long id) {
    return queryRepository
        .findById(id)
        .orElseThrow(() -> new WrongDataException("Can't find query by id"));
  }

  private QueryDto mapQueryEntityToDto(QueryEntity queryEntity) {
    QueryDto queryDto = new QueryDto();
    queryDto.setId(queryEntity.getId());
    queryDto.setDbId(queryEntity.getDb().getId());
    queryDto.setQueryName(queryEntity.getQueryName());
    queryDto.setQuery(queryEntity.getQuery());
    return queryDto;
  }

  @Override
  public List<QueryDto> getAllByDb(long dbId) {
    List<QueryDto> queryDtos = new ArrayList<>();
    List<QueryEntity> allByDbId = queryRepository.findAllByDbId(dbId);
    for (QueryEntity queryEntity : allByDbId) {
      queryDtos.add(mapQueryEntityToDto(queryEntity));
    }
    return queryDtos;
  }

  @Override
  public void save(QueryDto queryDto) {
    if (existsByName(queryDto.getDbId(), queryDto.getQueryName())) {
      throw new WrongDataException("The query with this name already exists in this database");
    }
    QueryEntity queryEntity = mapQueryDtoToEntity(queryDto);
    queryRepository.save(queryEntity);
  }

  private QueryEntity mapQueryDtoToEntity(QueryDto queryDto) {
    QueryEntity queryEntity = new QueryEntity();
    queryEntity.setId(queryDto.getId());
    queryEntity.setQueryName(queryDto.getQueryName());
    queryEntity.setQuery(queryDto.getQuery());
    queryEntity.setDb(dbRepository.findById(queryDto.getDbId()).get());
    return queryEntity;
  }

  @Override
  public void update(QueryDto queryDto) {
    QueryEntity queryEntity = getQueryEntity(queryDto.getId());
    queryEntity.setQuery(queryDto.getQuery());
    queryEntity.setQueryName(queryDto.getQueryName());
    queryRepository.save(queryEntity);
  }

  @Override
  public void delete(long id) {
    queryRepository.deleteById(id);
  }

  private boolean existsByName(Long dbId, String queryName) {
    return queryRepository.existsByDbIdAndQueryName(dbId, queryName);
  }
}
