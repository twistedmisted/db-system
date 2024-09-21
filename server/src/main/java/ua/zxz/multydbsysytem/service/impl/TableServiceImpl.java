package ua.zxz.multydbsysytem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.zxz.multydbsysytem.dto.DbDto;
import ua.zxz.multydbsysytem.dto.FieldDto;
import ua.zxz.multydbsysytem.dto.TableDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.mapper.impl.TableMapper;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.service.DbService;
import ua.zxz.multydbsysytem.service.TableService;
import ua.zxz.multydbsysytem.util.DbColumnToDtoTransformer;
import ua.zxz.multydbsysytem.web.payload.TablePayload;
import ua.zxz.multydbsysytem.web.payload.TableResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private static final String GET_TABLE_INFORMATION_QUERY = """
            SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, AUTO_INCREMENT, UNIQUE_COLUMN, PRIMARY_KEY, IS_IDENTITY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_name = ?;""";

    private final JdbcTemplate jdbcTemplate;
    private final DbService dbService;
    private final TableRepository tableRepository;
    private final DbRepository dbRepository;
    private final TableMapper tableMapper;

    @Override
    public TableDto getTableById(Long id, String username) {
        TableEntity tableEntity = getTableById(id);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't get the table");
        }
        List<FieldDto> fields = jdbcTemplate.query(
                GET_TABLE_INFORMATION_QUERY,
                r -> {
                    List<FieldDto> result = new ArrayList<>();
                    while (r.next()) {
                        result.add(DbColumnToDtoTransformer.transform(r));
                    }
                    return result;
                },
                "table_" + tableEntity.getId());
        TableDto tableDto = new TableDto();
        tableDto.setId(tableEntity.getId());
        tableDto.setName(tableEntity.getName());
        tableDto.setColumns(fields);
        return tableDto;
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Can't get the table"));
    }

    @Override
    public List<TableResponse> getAllTablesByDb(Long dbId, String username) {
        if (!dbService.userHasRightsToDb(dbId, username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't get tables, something went wrong");
        }
        return tableMapper.entitiesToResponse(tableRepository.findAllByDbId(dbId));
    }

    @Transactional
    @Override
    public void create(TablePayload table, String username) {
        if (!dbService.userHasRightsToDb(table.getDbId(), username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't create table, something went wrong");
        }
        DbDto dbDto = dbService.getById(table.getDbId());
        TableEntity tableEntity = new TableEntity();
        tableEntity.setName(table.getName());
//        tableEntity.setTechName(tableName);
        tableEntity.setDb(dbRepository.findById(table.getDbId()).get());
        tableEntity = tableRepository.save(tableEntity);
//        String tableName = username + "_" + dbDto.getName() + "_" + table.getName();
        String tableName = "table_" + tableEntity.getId();
        String firstPart = "CREATE TABLE " + tableName + "(";
        StringBuilder tableCreationSql = new StringBuilder(firstPart);
        for (int i = 0; i < table.getColumns().size(); i++) {
            tableCreationSql.append(ColumnService.mapToSqlColumn(table.getColumns().get(i)));
            if (i < table.getColumns().size() - 1) {
                tableCreationSql.append(",");
            }
        }
        tableCreationSql.append(")");
        log.info(tableCreationSql.toString());
        jdbcTemplate.update(tableCreationSql.toString());
    }

    @Override
    public void update(TableDto tableDto, String username) {
        TableEntity tableEntity = getTableById(tableDto.getId());
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't update table, something went wrong");
        }
        tableEntity.setName(tableDto.getName());
        tableRepository.save(tableEntity);
    }

    @Override
    public void deleteById(Long tableId, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't update table, something went wrong");
        }
        tableRepository.deleteById(tableId);
        jdbcTemplate.update("DROP TABLE table_" + tableEntity.getId() + ";");
    }
}
