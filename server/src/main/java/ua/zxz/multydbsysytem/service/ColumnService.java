package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.Constraints;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;
import ua.zxz.multydbsysytem.web.payload.RenameColumnPayload;
import ua.zxz.multydbsysytem.web.payload.column.ModifyDataType;
import ua.zxz.multydbsysytem.web.payload.column.constraint.DeleteConstraint;

import java.util.Objects;

public interface ColumnService {

    String PRIMARY_KEY = "PRIMARY KEY";
    String IDENTITY = "IDENTITY";
    String AUTO_INCREMENT = "AUTO_INCREMENT";
    String REFERENCES = "REFERENCES";
    String NOT_NULL = "NOT NULL";
    String UNIQUE = "UNIQUE";

    void addNewColumn(Long tableId, ColumnDto column, String username);

    void deleteColumn(Long tableId, String columnName, String username);

    static String mapToSqlColumn(ColumnDto column) {
        StringBuilder sb = new StringBuilder();
        Constraints constraints = column.getConstraints();
        if (constraints.isPrimaryKey()) {
            sb.append(PRIMARY_KEY + " ");
        }
        if (Objects.nonNull(column.getConstraints().getForeignTable())
                && column.getConstraints().getForeignTable().isForeignKey()) {
            ForeignTableDto foreignTable = column.getConstraints().getForeignTable();
            sb.append(REFERENCES + " ")
                    .append(foreignTable.getTableName())
                    .append(" (")
                    .append(foreignTable.getColumnName())
                    .append(") ");
        }
        if (constraints.isIdentity()) {
            sb.append(IDENTITY + " ");
        }
        if (constraints.isAutoIncrement()) {
            sb.append(AUTO_INCREMENT + " ");
        }
        if (constraints.isNotNull()) {
            sb.append(NOT_NULL + " ");
        }
        if (constraints.isUnique()) {
            sb.append(UNIQUE + " ");
        }
        String defaultValue = column.getDefaultValue();
        if (Objects.nonNull(defaultValue) && !defaultValue.isBlank()) {
            sb.append("DEFAULT ").append(defaultValue);
        }
        return column.getName() + " " + column.getType() + " " + sb.toString().trim();
    }

    ColumnDto getColumnByName(Long tableId, String columnName, String username);

    void renameColumn(Long tableId, RenameColumnPayload request, String username);

    void modifyColumnType(Long tableId, ModifyDataType request, String username);

    void deleteConstraint(Long tableId, DeleteConstraint request, String username);
}
