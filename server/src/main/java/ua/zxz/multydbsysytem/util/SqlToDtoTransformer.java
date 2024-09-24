package ua.zxz.multydbsysytem.util;

import ua.zxz.multydbsysytem.dto.table.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlToDtoTransformer {

    public static ColumnDto transformColumn(ResultSet r) throws SQLException {
        ColumnDto field = new ColumnDto();
        field.setName(r.getString("COLUMN_NAME"));
        field.setType(parseType(
                r.getString("DATA_TYPE"),
                r.getString("CHARACTER_MAXIMUM_LENGTH")
        ));
        field.setConstraints(parseConstraints(
                r.getString("IS_NULLABLE"),
                r.getString("AUTO_INCREMENT"),
                r.getString("UNIQUE_COLUMN"),
                r.getString("PRIMARY_KEY"),
                r.getString("IS_IDENTITY")
        ));
        return field;
    }

    private static ColumnType parseType(String dataType,
                                        String characterMaximumLength) {
        ColumnType columnType = new ColumnType();
        columnType.setType(ColumnType.Type.fromString(dataType));
        columnType.setValue(characterMaximumLength);
        return columnType;
    }

    private static List<ColumnConstraint> parseConstraints(String isNullable,
                                                           String autoIncrement,
                                                           String uniqueColumn,
                                                           String primaryKey,
                                                           String isIdentity) {
        List<ColumnConstraint> settings = new ArrayList<>();
        settings.add(new ColumnConstraint(Constraints.NULLABLE, isNullable));
        settings.add(new ColumnConstraint(Constraints.AUTO_INCREMENT, autoIncrement));
        settings.add(new ColumnConstraint(Constraints.UNIQUE, uniqueColumn));
        settings.add(new ColumnConstraint(Constraints.PRIMARY_KEY, primaryKey));
        settings.add(new ColumnConstraint(Constraints.IDENTITY, isIdentity));
        return settings;
    }

    public static ForeignTableDto transformForeignTable(ResultSet r) throws SQLException {
        ForeignTableDto table = new ForeignTableDto();
        table.setTableName(r.getString("REFERENCED_TABLE_NAME"));
        table.setColumnName(r.getString("REFERENCED_COLUMN_NAME"));
        table.setConstraintName(r.getString("REFERENCED_CONSTRAINT_NAME"));
        table.setTableColumn(r.getString("COLUMN_NAME"));
        return table;
    }
}
