package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.table.ColumnConstraint;
import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.Constraints;
import ua.zxz.multydbsysytem.web.payload.table.CrateTablePayload;

import java.util.Objects;

public interface ColumnService {

    void addNewColumn(Long tableId, CrateTablePayload.Column column, String username);

    void deleteColumn(Long tableId, String columnName, String username);

    static String mapToSqlColumn(CrateTablePayload.Column column) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < column.getConstraints().size(); i++) {
            ColumnConstraint constraint = column.getConstraints().get(i);
            if (Constraints.FOREIGN_KEY.equals(constraint.getValue())) {
                CrateTablePayload.ForeignTable foreignTable = column.getForeignTable();
                sb.append("REFERENCES ")
                        .append(foreignTable.getTableName())
                        .append(" (")
                        .append(foreignTable.getColumnName())
                        .append(") ");
                continue;
            }
            sb.append(constraint.getValue()).append(" ");
        }
        String defaultValue = column.getDefaultValue();
        if (Objects.nonNull(defaultValue) && !defaultValue.isBlank()) {
            sb.append("DEFAULT ").append(defaultValue);
        }
        return column.getName() + " " + column.getType() + " " + sb.toString().trim();
    }

    ColumnDto getColumnByName(Long tableId, String columnName, String username);
}
