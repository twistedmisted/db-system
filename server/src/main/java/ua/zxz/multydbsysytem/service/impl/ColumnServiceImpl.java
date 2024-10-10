package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.util.SqlToDtoTransformer;
import ua.zxz.multydbsysytem.web.payload.RenameColumnPayload;
import ua.zxz.multydbsysytem.web.payload.column.ModifyDataType;
import ua.zxz.multydbsysytem.web.payload.column.constraint.DeleteConstraint;

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
    public void addNewColumn(Long tableId, ColumnDto column, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't add new column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId + " ADD COLUMN " + ColumnService.mapToSqlColumn(column));
    }

    @Override
    public void deleteColumn(Long tableId, String columnName, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't delete column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId + " DROP COLUMN " + columnName + " CASCADE;");
    }

    @Override
    public ColumnDto getColumnByName(Long tableId, String columnName, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!tableEntity.getDb().getUser().getUsername().equals(username)) {
            throw new WrongDataException("Can't get the table");
        }
        return jdbcTemplate.query(
                GET_COLUMN_INFORMATION_QUERY,
                r -> r.next() ? SqlToDtoTransformer.transformColumn(r) : null,
                "table_" + tableEntity.getId(), columnName
        );
    }

    @Override
    public void renameColumn(Long tableId, RenameColumnPayload request, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't rename column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId +
                " ALTER COLUMN " + request.getOldName() + " RENAME " + request.getNewName());
    }

    @Override
    public void modifyColumnType(Long tableId, ModifyDataType request, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't modify column, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId +
                " MODIFY " + request.getColumnName() + " " + request.getColumnType());
    }

    @Override
    public void deleteConstraint(Long tableId, DeleteConstraint request, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
        switch (request.getConstraint()) {
            case NOT_NULL:
                jdbcTemplate.update("ALTER TABLE table_" + tableId + " MODIFY " + request.getColumnName() + " NULL");
                return;
            default:
                throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't get the table"));
    }
}
