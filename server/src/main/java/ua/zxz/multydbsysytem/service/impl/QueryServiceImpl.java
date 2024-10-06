package ua.zxz.multydbsysytem.service.impl;

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

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final TableService tableService;
    private final TableRepository tableRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Object> getByColumn(long tableId, Condition condition) {
        return jdbcTemplate.query("SELECT * FROM table_" + tableId +
                        " WHERE " + condition.getColumnName() + condition.getOperator().toString() + "?;",
                rs -> {
                    List<Object> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(mapObject(rs));
                    }
                    return list;
                },
                condition.getValue());
    }

    @Override
    public List<Object> getByColumn(long dbId, String tableName, Condition request) {
        TableEntity tableEntity = getTableEntity(dbId, tableName);
        return getByColumn(tableEntity.getId(), request);
    }

    private TableEntity getTableEntity(long dbId, String tableName) {
        return tableRepository.findByNameAndDbId(tableName, dbId)
                .orElseThrow(() -> new WrongDataException("Can't get data from table, something went wrong"));
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
    public List<Object> getAll(long tableId) {
        return jdbcTemplate.query("SELECT * FROM table_" + tableId + ";",
                rs -> {
                    List<Object> list = new ArrayList<>();
                    while (rs.next()) {
                        list.add(mapObject(rs));
                    }
                    return list;
                });
    }

    @Override
    public List<Object> getAll(long dbId, String tableName) {
        TableEntity tableEntity = getTableEntity(dbId, tableName);
        return getAll(tableEntity.getId());
    }

    @Override
    public void save(long tableId, Map<String, Object> object) {
        String columns = String.join(" = ?, ", object.keySet()) + " = ?";
        String parametersCount = String.join(",", Collections.nCopies(object.size(), "?"));
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        return jdbcTemplate.update(
//                String.format("INSERT INTO table_" + tableEntity.getId() + " (%s) VALUES (%s);", columns, parametersCount),
//                object.values().toArray());
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            String.format("INSERT INTO table_" + tableId + " SET %s;", columns)
//                            Statement.RETURN_GENERATED_KEYS
                    );
                    int index = 1;
                    for (Object v : object.values()) {
                        ps.setObject(index, v);
                        index++;
                    }
                    return ps;
                }
//                keyHolder
        );
    }

    @Override
    public void save(long dbId, String tableName, Map<String, Object> object) {
        TableEntity tableEntity = getTableEntity(dbId, tableName);
        save(tableEntity.getId(), object);
    }

    @Override
    public void update(long tableId, UpdateQueryRequest request) {
        Condition condition = request.getCondition();
        Map<String, Object> object = request.getObject();
        String columnsToUpdate = object.keySet().stream().map(c -> c + " = ?").collect(Collectors.joining(", "));
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "UPDATE table_" + tableId +
                                    " SET " + columnsToUpdate +
                                    " WHERE " + condition.getColumnName() + condition.getOperator().toString() + " ?;",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    int index = 1;
                    for (Object v : object.values()) {
                        ps.setObject(index++, v);
                    }
                    ps.setObject(index, condition.getValue());
                    return ps;
                }
        );
    }

    @Override
    public void update(long dbId, String tableName, UpdateQueryRequest request) {
        TableEntity tableEntity = getTableEntity(dbId, tableName);
        update(tableEntity.getId(), request);
    }

    @Override
    public void delete(long tableId, Map<String, Object> object) {
        Map<String, String> constraints = tableService.getConstraints(tableId);
        if (constraints.isEmpty()) {
            throw new WrongDataException("Can't delete data from table, something went wrong");
        }
        String columnRemoveBy = constraints.get("PRIMARY KEY");
        Object value = object.get(columnRemoveBy);
        if (Objects.isNull(columnRemoveBy) || Objects.isNull(value)) {
            throw new WrongDataException("Can't delete data from table, something went wrong");
        }
        delete(tableId, new Condition(columnRemoveBy, Condition.Operator.EQUALS, value));
    }

    @Override
    public void delete(long dbId, String tableName, Condition condition) {
        TableEntity tableEntity = getTableEntity(dbId, tableName);
        delete(tableEntity.getId(), condition);
    }

    private void delete(long tableId, Condition condition) {
        jdbcTemplate.update("DELETE FROM table_" + tableId +
                " WHERE " +
                condition.getColumnName() +
                condition.getOperator().toString() +
                "?", condition.getValue());
    }
}
