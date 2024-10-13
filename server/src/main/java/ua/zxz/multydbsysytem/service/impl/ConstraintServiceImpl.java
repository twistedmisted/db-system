package ua.zxz.multydbsysytem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.table.Constraints;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.repository.TableRepository;
import ua.zxz.multydbsysytem.service.ConstraintService;

import static ua.zxz.multydbsysytem.util.SqlToDtoTransformer.parseConstraints;
import static ua.zxz.multydbsysytem.util.SqlToDtoTransformer.transformForeignTable;

@Service
@RequiredArgsConstructor
public class ConstraintServiceImpl implements ConstraintService {

    private static final String GET_TABLE_CONSTRAINTS = """
            SELECT IS_NULLABLE, AUTO_INCREMENT, UNIQUE_COLUMN, PRIMARY_KEY, IS_IDENTITY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = ? AND COLUMN_NAME = ?;""";

    private static final String GET_FOREIGN_KEYS = """
            SELECT COLUMN_NAME, REFERENCED_CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE CONSTRAINT_TYPE = 'FOREIGN KEY' AND TABLE_NAME = ? AND COLUMN_NAME = ?;""";

    private final JdbcTemplate jdbcTemplate;
    private final TableRepository tableRepository;

    @Override
    public Constraints getByTableId(Long tableId, String columnName, String username) {
        TableEntity tableEntity = getTableById(tableId);
        if (!tableEntity.getDb().getUser().getUsername().equals(username)) {
            throw new WrongDataException("Can't get constraints by table id " + tableId);
        }
        Constraints constraints = new Constraints();
        jdbcTemplate.update("USE db_" + tableEntity.getDb().getId() + ";");
        getTableConstraints(tableEntity.getName(), columnName, constraints);
        getForeignKeys(tableEntity.getName(), columnName, constraints);
        jdbcTemplate.update("USE \"USER\";");
        return constraints;
    }

    private TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't get the table"));
    }

    private void getForeignKeys(String tableName, String columnName, Constraints constraints) {
        jdbcTemplate.query(
                GET_FOREIGN_KEYS,
                r -> {
                    if (r.next()) {
                        ForeignTableDto foreignTableDto = transformForeignTable(r);
                        constraints.setForeignTable(foreignTableDto);
                    }
                    return null;
                },
                tableName, columnName);
    }

    private void getTableConstraints(String tableName, String columnName, Constraints constraints) {
        jdbcTemplate.query(
                GET_TABLE_CONSTRAINTS,
                r -> {
                    if (r.next()) {
                        parseConstraints(constraints, r);
                    }
                    return null;
                },
                tableName, columnName);
    }
}
