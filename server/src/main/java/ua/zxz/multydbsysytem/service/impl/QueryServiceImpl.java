package ua.zxz.multydbsysytem.service.impl;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.QueryService;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.web.payload.query.Condition;
import ua.zxz.multydbsysytem.web.payload.query.UpdateQueryRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

  private final TableService tableService;
  private final TableRepository tableRepository;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<Object> getByColumn(TableEntity table, Condition condition) {
    setNamespace("db_" + table.getDb().getId());
    List<Object> result =
        jdbcTemplate.query(
            "SELECT * FROM "
                + table.getName()
                + " WHERE "
                + condition.getColumnName()
                + condition.getOperator().toString()
                + "?;",
            rs -> {
              List<Object> list = new ArrayList<>();
              while (rs.next()) {
                list.add(mapObject(rs));
              }
              return list;
            },
            condition.getValue());
    setNamespace("\"USER\"");
    return result;
  }

  @Override
  public List<Object> getByColumn(long dbId, String tableName, Condition request) {
    TableEntity tableEntity = getTableEntity(dbId, tableName);
    return getByColumn(tableEntity, request);
  }

  private TableEntity getTableEntity(long dbId, String tableName) {
    return tableRepository
        .findByNameAndDbId(tableName, dbId)
        .orElseThrow(
            () -> new WrongDataException("Can't get data from table, something went wrong"));
  }

  private Map<String, Object> mapObject(ResultSet rs) throws SQLException {
    Map<String, Object> data = new LinkedHashMap<>();
    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      data.put(metaData.getColumnName(i), rs.getObject(i));
    }
    return data;
  }

  @Override
  public List<Object> getAll(TableEntity table) {
    setNamespace("db_" + table.getDb().getId());
    List<Object> query =
        jdbcTemplate.query(
            "SELECT * FROM " + table.getName() + ";",
            rs -> {
              List<Object> list = new ArrayList<>();
              while (rs.next()) {
                list.add(mapObject(rs));
              }
              return list;
            });
    setNamespace("\"USER\"");
    return query;
  }

  private void setNamespace(String namespace) {
    jdbcTemplate.update("USE " + namespace + ";");
  }

  @Override
  public List<Object> getAll(long dbId, String tableName) {
    TableEntity tableEntity = getTableEntity(dbId, tableName);
    return getAll(tableEntity);
  }

  @Override
  public void save(TableEntity table, Map<String, Object> object) {
    setNamespace("db_" + table.getDb().getId());
    String columns = String.join(" = ?, ", object.keySet()) + " = ?";
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps =
              con.prepareStatement(
                  String.format("INSERT INTO " + table.getName() + " SET %s;", columns));
          int index = 1;
          for (Object v : object.values()) {
            ps.setObject(index, v);
            index++;
          }
          return ps;
        });
    setNamespace("\"USER\"");
  }

  @Override
  public void save(long dbId, String tableName, Map<String, Object> object) {
    TableEntity tableEntity = getTableEntity(dbId, tableName);
    save(tableEntity, object);
  }

  @Override
  public void update(TableEntity table, UpdateQueryRequest request) {
    setNamespace("db_" + table.getDb().getId());
    Condition condition = request.getCondition();
    Map<String, Object> object = request.getObject();
    String columnsToUpdate =
        object.keySet().stream().map(c -> c + " = ?").collect(Collectors.joining(", "));
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps =
              con.prepareStatement(
                  "UPDATE "
                      + table.getName()
                      + " SET "
                      + columnsToUpdate
                      + " WHERE "
                      + condition.getColumnName()
                      + condition.getOperator().toString()
                      + " ?;",
                  Statement.RETURN_GENERATED_KEYS);
          int index = 1;
          for (Object v : object.values()) {
            ps.setObject(index++, v);
          }
          ps.setObject(index, condition.getValue());
          return ps;
        });
    setNamespace("\"USER\"");
  }

  @Override
  public void update(long dbId, String tableName, UpdateQueryRequest request) {
    TableEntity tableEntity = getTableEntity(dbId, tableName);
    update(tableEntity, request);
  }

  @Override
  public void delete(TableEntity table, Map<String, Object> object) {
    setNamespace("db_" + table.getDb().getId());
    Map<String, String> constraints = tableService.getConstraints(table.getName());
    if (constraints.isEmpty()) {
      throw new WrongDataException("Can't delete data from table, something went wrong");
    }
    String columnRemoveBy = constraints.get("PRIMARY KEY");
    Object value = object.get(columnRemoveBy);
    if (Objects.isNull(columnRemoveBy) || Objects.isNull(value)) {
      throw new WrongDataException("Can't delete data from table, something went wrong");
    }
    delete(table.getName(), new Condition(columnRemoveBy, Condition.Operator.EQUALS, value));
    setNamespace("\"USER\"");
  }

  @Override
  public void delete(long dbId, String tableName, Condition condition) {
    setNamespace("db_" + dbId);
    jdbcTemplate.update(
        "DELETE FROM "
            + tableName
            + " WHERE "
            + condition.getColumnName()
            + condition.getOperator().toString()
            + "?",
        condition.getValue());
    setNamespace("\"USER\"");
  }

  private void delete(String tableName, Condition condition) {
    jdbcTemplate.update(
        "DELETE FROM "
            + tableName
            + " WHERE "
            + condition.getColumnName()
            + condition.getOperator().toString()
            + "?",
        condition.getValue());
  }
}
