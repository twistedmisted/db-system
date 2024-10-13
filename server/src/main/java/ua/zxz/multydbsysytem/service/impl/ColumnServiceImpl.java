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
import ua.zxz.multydbsysytem.service.DbService;
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
    private final JdbcService jdbcService;
    private final DbService dbService;
    private final TableRepository tableRepository;

    @Override
    public void addNewColumn(Long tableId, ColumnDto column, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't add new column, something went wrong");
        }
        jdbcService.batchUpdate(
                tableEntity.getDb().getId(),
                "ALTER TABLE " + tableEntity.getName() + " ADD COLUMN " + ColumnService.mapToSqlColumn(column)
        );
    }

    @Override
    public void deleteColumn(Long tableId, String columnName, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't delete column, something went wrong");
        }
        jdbcService.batchUpdate(
                tableEntity.getDb().getId(),
                "ALTER TABLE " + tableEntity.getName() + " DROP COLUMN " + columnName + " CASCADE;"
        );
    }

    @Deprecated
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
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't rename column, something went wrong");
        }
        jdbcService.batchUpdate(
                tableEntity.getDb().getId(),
                "ALTER TABLE " + tableEntity.getName() +
                        " ALTER COLUMN " + request.getOldName() + " RENAME " + request.getNewName()
        );
    }

    @Override
    public void modifyColumnType(Long tableId, ModifyDataType request, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't modify column type, something went wrong");
        }
        jdbcService.batchUpdate(
                tableEntity.getDb().getId(),
                "ALTER TABLE " + tableEntity.getName() +
                        " MODIFY " + request.getColumnName() + " " + request.getColumnType()
        );
    }

    @Override
    public void addConstraints(Long tableId, AddConstraintsReq req, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't add column constraints, something went wrong");
        }
        String tableName = tableEntity.getName();
        Constraints constraints = req.getConstraints();
        List<String> sqlQueries = new ArrayList<>();
        if (constraints.isNotNull()) {
            sqlQueries.add("ALTER TABLE " + tableName + " MODIFY " + req.getColumnName() + " NOT NULL;");
        }
        if (constraints.isPrimaryKey()) {
            String constraintName = "pk_" + tableName;
            sqlQueries.add("ALTER TABLE " + tableName +
                    " ADD CONSTRAINT " + constraintName + " PRIMARY KEY (" + req.getColumnName() + ");");
        }
        if (constraints.isUnique()) {
            String constraintName = "uq_" + tableName + "_" + req.getColumnName();
            sqlQueries.add("ALTER TABLE " + tableName +
                    " ADD CONSTRAINT " + constraintName + " UNIQUE (" + req.getColumnName() + ");");
        }
        if (Objects.nonNull(constraints.getForeignTable())
                && constraints.getForeignTable().isForeignKey()) {
            String constraintName = "fk_" + tableName + "_" + constraints.getForeignTable().getTableName();
            ForeignTableDto foreignTable = constraints.getForeignTable();
            sqlQueries.add("ALTER TABLE " + tableName +
                    " ADD CONSTRAINT " + constraintName + " FOREIGN KEY (" + req.getColumnName() + ") REFERENCES " +
                    foreignTable.getTableName() + " (" + foreignTable.getColumnName() + ");");
        }
        if (!sqlQueries.isEmpty()) {
            jdbcService.batchUpdate(tableEntity.getDb().getId(), sqlQueries);
        } else {
            throw new WrongDataException("Can't add column constraints, something went wrong");
        }
    }

    @Override
    public void deleteConstraint(Long tableId, DeleteConstraint request, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't delete column constraint, something went wrong");
        }
        String tableName = tableEntity.getName();
        String constraint = request.getConstraint();
        if (NOT_NULL.equals(constraint)) {
            jdbcTemplate.update("USE db_" + tableEntity.getDb().getId() + ";");
            jdbcTemplate.update("ALTER TABLE " + tableName + " MODIFY " + request.getColumnName() + " NULL;");
            jdbcTemplate.update("USE \"USER\";");
        } else if (UNIQUE.equals(constraint) || PRIMARY_KEY.equals(constraint)) {
            jdbcTemplate.update("USE db_" + tableEntity.getDb().getId() + ";");
            String constraintName = jdbcTemplate.query(
                    GET_KEY_CONSTRAINT_NAME,
                    rs -> rs.next() ? rs.getString("CONSTRAINT_NAME") : null,
                    tableName, request.getColumnName(), constraint);
            jdbcTemplate.update("ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";");
            jdbcTemplate.update("USE \"USER\";");
        } else if (Objects.nonNull(constraint) && constraint.startsWith(FOREIGN_KEY)) {
            jdbcTemplate.update("USE db_" + tableEntity.getDb().getId() + ";");
            String constraintName = jdbcTemplate.query(
                    GET_KEY_CONSTRAINT_NAME,
                    rs -> rs.next() ? rs.getString("CONSTRAINT_NAME") : null,
                    tableName, request.getColumnName(), FOREIGN_KEY);
            jdbcTemplate.update("ALTER TABLE " + tableName + " DROP CONSTRAINT " + constraintName + ";");
            jdbcTemplate.update("USE \"USER\";");
        } else {
            throw new WrongDataException("Can't delete unsupported constraint");
        }
    }

    @Override
    public void modifyDefVal(Long tableId, ModifyDefVal req, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!dbService.userHasRightsToDb(tableEntity.getDb().getId(), username)) {
            throw new WrongDataException("Can't change default column value, something went wrong");
        }
        jdbcService.batchUpdate(tableEntity.getDb().getId(), "ALTER TABLE " + tableEntity.getName() +
                " MODIFY " + req.getColumnName() + " DEFAULT " + req.getDefVal() + ";");
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't get the table"));
    }
}
