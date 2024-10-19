package ua.zxz.multydbsysytem.service.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.QueryDto;
import ua.zxz.multydbsysytem.entity.QueryEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.repository.QueryRepository;
import ua.zxz.multydbsysytem.service.CustomQueryService;
import ua.zxz.multydbsysytem.web.payload.query.ExecuteQueryReq;

@Service
@RequiredArgsConstructor
public class CustomQueryServiceImpl implements CustomQueryService {

  private final QueryRepository queryRepository;
  private final DbRepository dbRepository;
  private final DataSourceService dataSourceService;

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

  @Override
  public Object execute(ExecuteQueryReq req) {
    JdbcTemplate dbJdbcTemplate = dataSourceService.getJdbcTemplateByDb(req.getDbId());
    return dbJdbcTemplate.query(
        req.getQuery(),
        rs -> {
          List<Object> list = new ArrayList<>();
          while (rs.next()) {
            list.add(mapObject(rs));
          }
          return list;
        });
  }

  @Override
  public Object executeCustomQuery(long dbId, String queryName, Map<String, ?> request) {
    NamedParameterJdbcTemplate dbJdbcTemplate =
        dataSourceService.getNamedParameterJdbcTemplateByDb(dbId);
    String sql = queryRepository
        .getQueryByDbIdAndQueryName(dbId, queryName)
        .orElseThrow(() -> new WrongDataException("Can't find query by name for this db"));
    return dbJdbcTemplate.query(sql, request, rs -> {
      List<Object> list = new ArrayList<>();
      while (rs.next()) {
        list.add(mapObject(rs));
      }
      return list;
    });
  }

  private Map<String, Object> mapObject(ResultSet rs) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      data.put(metaData.getTableName(i) + "." + metaData.getColumnName(i), rs.getObject(i));
    }
    return data;
  }
}
