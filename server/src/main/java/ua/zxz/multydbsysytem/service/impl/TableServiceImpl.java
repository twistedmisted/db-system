package ua.zxz.multydbsysytem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;
import ua.zxz.multydbsysytem.dto.table.TableDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.service.DbService;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.util.SqlToDtoTransformer;
import ua.zxz.multydbsysytem.web.payload.table.CrateTablePayload;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private static final String GET_TABLE_INFORMATION_QUERY = """
            SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, AUTO_INCREMENT, UNIQUE_COLUMN, PRIMARY_KEY, IS_IDENTITY, COLUMN_DEFAULT
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_name = ?;""";

    private static final String GET_KEY_COLUMN_USAGE = """
            SELECT COLUMN_NAME, REFERENCED_CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE CONSTRAINT_TYPE = 'FOREIGN KEY' AND table_name = ?;""";

    private static final String GET_TABLE_CONSTRAINTS = """
            SELECT COLUMN_NAME, CONSTRAINT_TYPE
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE CONSTRAINT_TYPE != 'UNIQUE' AND CONSTRAINT_TYPE != 'FOREIGN KEY' AND table_name = ?;""";

    private final JdbcTemplate jdbcTemplate;
    private final JdbcService jdbcService;
    private final DbService dbService;
    private final TableRepository tableRepository;
    private final DbRepository dbRepository;
    private final DataSourceService dataSourceService;

    @Override
    public TableDto getTableById(Long id, String username) {
        TableEntity tableEntity = getTableById(id);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't get the table");
        }
        try {
            jdbcTemplate.update("USE " + tableEntity.getDb().getTechName() + ";");
            TableDto tableDto = getTableDto(tableEntity);
            jdbcService.rollbackNamespace();
            return tableDto;
        } catch (Exception e) {
            jdbcService.rollbackNamespace();
            throw e;
        }
    }

    private TableDto getTableDto(TableEntity tableEntity) {
        Map<String, ColumnDto> columnsByName = getColumns(tableEntity.getName());
        List<ForeignTableDto> foreignTables = getForeignTables(tableEntity.getName());
        if (!foreignTables.isEmpty() && !columnsByName.isEmpty()) {
            foreignTables.forEach(ft -> {
                ColumnDto columnDto = columnsByName.get(ft.getTableColumn());
                if (Objects.nonNull(columnDto)) {
//                    String techName = ft.getTableName();
//                    ft.setTableName(tableRepository.findNameById(Long.valueOf(techName.substring(techName.indexOf("_") + 1))));
                    columnDto.getConstraints().setForeignTable(ft);
                }
            });
        }
        TableDto tableDto = new TableDto();
        tableDto.setId(tableEntity.getId());
        tableDto.setName(tableEntity.getName());
        tableDto.setColumns(new ArrayList<>(columnsByName.values()));
        return tableDto;
    }

    private List<ForeignTableDto> getForeignTables(String tableName) {
        List<ForeignTableDto> tables = jdbcTemplate.query(
                GET_KEY_COLUMN_USAGE,
                r -> {
                    List<ForeignTableDto> result = new ArrayList<>();
                    while (r.next()) {
                        result.add(SqlToDtoTransformer.transformForeignTable(r));
                    }
                    return result;
                },
                tableName
        );
        return Objects.isNull(tables) ? Collections.emptyList() : tables;
    }

    private Map<String, ColumnDto> getColumns(String tableName) {
        Map<String, ColumnDto> columns = jdbcTemplate.query(
                GET_TABLE_INFORMATION_QUERY,
                r -> {
                    Map<String, ColumnDto> result = new LinkedHashMap<>();
                    while (r.next()) {
                        ColumnDto columnDto = SqlToDtoTransformer.transformColumn(r);
                        result.put(columnDto.getName(), columnDto);
                    }
                    return result;
                },
                tableName);
        return Objects.isNull(columns) ? Collections.emptyMap() : columns;
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't get the table"));
    }

    @Override
    public List<TableDto> getAllTablesByDb(Long dbId, String username) {
        if (!dbService.userHasRightsToDb(dbId, username)) {
            throw new WrongDataException("Can't get tables, something went wrong");
        }
        List<TableEntity> tables = tableRepository.findAllByDbId(dbId);
        List<TableDto> tableDtos = new ArrayList<>();
        if (tables.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            jdbcTemplate.update("USE " + tables.getFirst().getDb().getTechName() + ";");
            for (TableEntity table : tables) {
                tableDtos.add(getTableDto(table));
            }
            jdbcService.rollbackNamespace();
            return tableDtos;
        } catch (Exception e) {
            jdbcService.rollbackNamespace();
            throw e;
        }
    }

    @Transactional
    @Override
    public void create(CrateTablePayload table, String username) {
        if (!dbService.userHasRightsToDb(table.getDbId(), username)) {
            throw new WrongDataException("Can't create table, something went wrong");
        }
        if (dbService.dbAlreadyHasTableWithName(table.getDbId(), table.getName())) {
            throw new WrongDataException("The table with name " + table.getName() + " already exists in this database");
        }
        TableEntity tableEntity = new TableEntity();
        tableEntity.setName(table.getName());
        tableEntity.setDb(dbRepository.findById(table.getDbId()).get());
        tableEntity = tableRepository.save(tableEntity);
        createSqlTable(table, tableEntity);
    }

    private void createSqlTable(CrateTablePayload table, TableEntity tableEntity) {
        String tableName = table.getName();
        String firstPart = "CREATE TABLE " + tableName + "(";
        StringBuilder tableCreationSql = new StringBuilder(firstPart);
        for (int i = 0; i < table.getColumns().size(); i++) {
            tableCreationSql.append(ColumnService.mapToSqlColumn(table.getColumns().get(i)));
            if (i < table.getColumns().size() - 1) {
                tableCreationSql.append(",");
            }
        }
        tableCreationSql.append(");");
        jdbcService.batchUpdate(tableEntity.getDb().getId(), tableCreationSql.toString());
    }

    @Override
    @Transactional
    public void update(TableDto tableDto, String username) {
        TableEntity tableEntity = getTableById(tableDto.getId());
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't update table, something went wrong");
        }
        String oldName = tableEntity.getName();
        if (!oldName.equals(tableDto.getName())) {
            tableEntity.setName(tableDto.getName());
            tableRepository.save(tableEntity);
            jdbcService.batchUpdate(tableDto.getDbId(), "ALTER TABLE " + oldName + " RENAME " + tableDto.getName() + ";");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long tableId, Long dbId, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't update table, something went wrong");
        }
        tableRepository.deleteById(tableId);
        jdbcService.batchUpdate(dbId, "DROP TABLE " + tableEntity.getName() + ";");
    }

    @Override
    public boolean hasRights(Long tableId, String username) {
        return tableRepository.existsByIdAndDbUserUsername(tableId, username);
    }

    @Override
    public Map<String, String> getConstraints(String tableName) {
        return jdbcTemplate.query(
                GET_TABLE_CONSTRAINTS,
                r -> {
                    Map<String, String> constraints = new HashMap<>();
                    while (r.next()) {
                        constraints.put(r.getString("CONSTRAINT_TYPE"), r.getString("COLUMN_NAME"));
                    }
                    return constraints;
                },
                tableName);
    }
}
