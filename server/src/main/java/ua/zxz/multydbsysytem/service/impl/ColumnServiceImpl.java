package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.Constraints;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ColumnService;
import ua.zxz.multydbsysytem.util.SqlToDtoTransformer;
import ua.zxz.multydbsysytem.web.payload.RenameColumnPayload;
import ua.zxz.multydbsysytem.web.payload.column.ModifyDataType;
import ua.zxz.multydbsysytem.web.payload.column.ModifyDefVal;
import ua.zxz.multydbsysytem.web.payload.column.constraint.AddConstraintsReq;
import ua.zxz.multydbsysytem.web.payload.column.constraint.DeleteConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService {

    private static final String GET_COLUMN_INFORMATION_QUERY = """
            SELECT COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, AUTO_INCREMENT, UNIQUE_COLUMN, PRIMARY_KEY, IS_IDENTITY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE table_name = ? AND column_name = ?;""";

    private static final String GET_KEY_CONSTRAINT_NAME = """
            SELECT CONSTRAINT_NAME
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE table_name = ? AND COLUMN_NAME = ? AND CONSTRAINT_TYPE = ?;""";

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
    public void addConstraints(Long tableId, AddConstraintsReq req, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
        Constraints constraints = req.getConstraints();
        List<String> sqlQueries = new ArrayList<>();
        if (constraints.isNotNull()) {
            sqlQueries.add("ALTER TABLE table_" + tableId + " MODIFY " + req.getColumnName() + " NOT NULL");
        }
        if (constraints.isPrimaryKey()) {
            String constraintName = "pk_table_" + tableId + "_" + req.getColumnName();
            sqlQueries.add("ALTER TABLE table_" + tableId +
                    " ADD CONSTRAINT " + constraintName + " PRIMARY KEY (" + req.getColumnName() + ");");
        }
        if (constraints.isUnique()) {
            String constraintName = "uq_table_" + tableId + "_" + req.getColumnName();
            sqlQueries.add("ALTER TABLE table_" + tableId +
                    " ADD CONSTRAINT " + constraintName + " UNIQUE (" + req.getColumnName() + ");");
        }
        if (Objects.nonNull(constraints.getForeignTable())
                && constraints.getForeignTable().isForeignKey()) {
            String constraintName = "fk_" + "table_" + tableId + "_" + req.getColumnName();
            ForeignTableDto foreignTable = constraints.getForeignTable();
            jdbcTemplate.update("ALTER TABLE table_" + tableId +
                    " ADD CONSTRAINT " + constraintName + " FOREIGN KEY (" + req.getColumnName() + ") REFERENCES " +
                    foreignTable.getTableName() + " (" + foreignTable.getColumnName() + ");");
        }
        if (!sqlQueries.isEmpty()) {
            jdbcTemplate.batchUpdate(sqlQueries.toArray(new String[0]));
        } else {
            throw new WrongDataException("Can't add column constraints, something went wrong");
        }
    }

    @Override
    public void deleteConstraint(Long tableId, DeleteConstraint request, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
        String constraint = request.getConstraint();
        if (NOT_NULL.equals(constraint)) {
            jdbcTemplate.update("ALTER TABLE table_" + tableId + " MODIFY " + request.getColumnName() + " NULL");
        } else if (UNIQUE.equals(constraint) || PRIMARY_KEY.equals(constraint)) {
            String constraintName = jdbcTemplate.query(
                    GET_KEY_CONSTRAINT_NAME,
                    rs -> rs.next() ? rs.getString("CONSTRAINT_NAME") : null,
                    "table_" + tableId, request.getColumnName(), constraint);
            jdbcTemplate.update("ALTER TABLE table_" + tableId + " DROP CONSTRAINT " + constraintName);
        } else if (Objects.nonNull(constraint) && constraint.startsWith(FOREIGN_KEY)) {
            String constraintName = jdbcTemplate.query(
                    GET_KEY_CONSTRAINT_NAME,
                    rs -> rs.next() ? rs.getString("CONSTRAINT_NAME") : null,
                    "table_" + tableId, request.getColumnName(), FOREIGN_KEY);
            jdbcTemplate.update("ALTER TABLE table_" + tableId + " DROP CONSTRAINT " + constraintName);
        } else {
            throw new WrongDataException("Can't delete unsupported constraint");
        }
    }

    @Override
    public void modifyDefVal(Long tableId, ModifyDefVal req, String username) {
        if (tableRepository.userHasAccessToTable(tableId, username) != 1) {
            throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
        jdbcTemplate.update("ALTER TABLE table_" + tableId +
                " MODIFY " + req.getColumnName() + " DEFAULT " + req.getDefVal());
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't get the table"));
    }
}
