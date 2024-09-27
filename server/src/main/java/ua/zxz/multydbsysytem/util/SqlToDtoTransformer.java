package ua.zxz.multydbsysytem.util;

import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.ColumnType;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SqlToDtoTransformer {

    private static final Predicate<String> ACTIVE_WHEN_YES = "YES"::equals;
    private static final Predicate<String> ACTIVE_WHEN_NO = "NO"::equals;

    public static ColumnDto transformColumn(ResultSet r) throws SQLException {
        ColumnDto field = new ColumnDto();
        field.setName(r.getString("COLUMN_NAME"));
        field.setType(parseType(
                r.getString("DATA_TYPE"),
                r.getString("CHARACTER_MAXIMUM_LENGTH")
        ));
        parseConstraints(field, r);
        field.setDefaultValue(r.getString("COLUMN_DEFAULT"));
        return field;
    }

    private static ColumnType parseType(String dataType,
                                        String characterMaximumLength) {
        ColumnType columnType = new ColumnType();
        columnType.setType(ColumnType.Type.fromString(dataType));
        columnType.setValue(characterMaximumLength);
        return columnType;
    }

    private static void parseConstraints(ColumnDto field, ResultSet r) throws SQLException {
        setConstraint(v -> field.getConstraints().setPrimaryKey(v), ACTIVE_WHEN_YES, r.getString("PRIMARY_KEY"));
        setConstraint(v -> field.getConstraints().setIdentity(v), ACTIVE_WHEN_YES, r.getString("IS_IDENTITY"));
        setConstraint(v -> field.getConstraints().setAutoIncrement(v), ACTIVE_WHEN_YES, r.getString("AUTO_INCREMENT"));
        setConstraint(v -> field.getConstraints().setNotNull(v), ACTIVE_WHEN_NO, r.getString("IS_NULLABLE"));
        setConstraint(v -> field.getConstraints().setUnique(v), ACTIVE_WHEN_YES, r.getString("UNIQUE_COLUMN"));
    }

    private static void setConstraint(Consumer<Boolean> constraintSetter, Predicate<String> isActive, String value) {
        constraintSetter.accept(isActive.test(value));
    }

    public static ForeignTableDto transformForeignTable(ResultSet r) throws SQLException {
        ForeignTableDto table = new ForeignTableDto();
        table.setTableName(r.getString("REFERENCED_TABLE_NAME"));
        table.setColumnName(r.getString("REFERENCED_COLUMN_NAME"));
        table.setConstraintName(r.getString("REFERENCED_CONSTRAINT_NAME"));
        table.setTableColumn(r.getString("COLUMN_NAME"));
        table.setForeignKey(true);
        return table;
    }
}
