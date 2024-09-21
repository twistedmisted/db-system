package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.zxz.multydbsysytem.dto.FieldDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.util.DbColumnToDtoTransformer;
import ua.zxz.multydbsysytem.web.payload.TablePayload;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private static final String GET_COLUMN_INFORMATION_QUERY = """
            SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, AUTO_INCREMENT, UNIQUE_COLUMN, PRIMARY_KEY, IS_IDENTITY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_name = ? AND column_name = ?;""";

    private final JdbcTemplate jdbcTemplate;
    private final TableRepository tableRepository;

    @Override
    public void addNewColumn(Long tableId, TablePayload.Column column, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't add new column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId + " ADD COLUMN " + ColumnService.mapToSqlColumn(column));
    }

    @Override
    public void deleteColumn(Long tableId, String columnName, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't add new column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId + " DROP COLUMN " + columnName);
    }

    @Override
    public FieldDto getColumnByName(Long tableId, String columnName, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!tableEntity.getDb().getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't get the table");
        }
        return jdbcTemplate.query(
                GET_COLUMN_INFORMATION_QUERY,
                r -> r.next() ? DbColumnToDtoTransformer.transform(r) : null,
                "table_" + tableEntity.getId(), columnName
        );
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Can't get the table"));
    }
}
