package ua.zxz.multydbsysytem.util;

import ua.zxz.multydbsysytem.dto.table.ColumnDto;
import ua.zxz.multydbsysytem.dto.table.ColumnType;
import ua.zxz.multydbsysytem.dto.table.Constraints;
import ua.zxz.multydbsysytem.dto.table.ForeignTableDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SqlToDtoTransformer {

    public static final Predicate<String> ACTIVE_WHEN_YES = "YES"::equals;
    public static final Predicate<String> ACTIVE_WHEN_NO = "NO"::equals;

    public static ColumnDto transformColumn(ResultSet r) throws SQLException {
        ColumnDto field = new ColumnDto();
        field.setName(r.getString("COLUMN_NAME"));
        field.setType(parseType(
                r.getString("DATA_TYPE"),
                r.getString("CHARACTER_MAXIMUM_LENGTH")
        ));
        parseConstraints(field.getConstraints(), r);
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

    public static void parseConstraints(Constraints constraints, ResultSet r) throws SQLException {
        setConstraint(constraints::setPrimaryKey, ACTIVE_WHEN_YES, r.getString("PRIMARY_KEY"));
        setConstraint(constraints::setIdentity, ACTIVE_WHEN_YES, r.getString("IS_IDENTITY"));
        setConstraint(constraints::setAutoIncrement, ACTIVE_WHEN_YES, r.getString("AUTO_INCREMENT"));
        setConstraint(constraints::setNotNull, ACTIVE_WHEN_NO, r.getString("IS_NULLABLE"));
        setConstraint(constraints::setUnique, ACTIVE_WHEN_YES, r.getString("UNIQUE_COLUMN"));
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
