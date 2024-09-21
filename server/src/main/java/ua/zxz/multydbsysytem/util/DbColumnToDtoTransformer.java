package ua.zxz.multydbsysytem.util;

import ua.zxz.multydbsysytem.dto.FieldDto;
import ua.zxz.multydbsysytem.dto.FieldSettings;
import ua.zxz.multydbsysytem.dto.FieldType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbColumnToDtoTransformer {

    public static FieldDto transform(ResultSet r) throws SQLException {
        FieldDto field = new FieldDto();
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

    private static FieldType parseType(String dataType,
                                String characterMaximumLength) {
        FieldType fieldType = new FieldType();
        fieldType.setType(FieldType.Type.fromString(dataType));
        fieldType.setValue(characterMaximumLength);
        return fieldType;
    }

    private static List<FieldSettings> parseConstraints(String isNullable,
                                                 String autoIncrement,
                                                 String uniqueColumn,
                                                 String primaryKey,
                                                 String isIdentity) {
        List<FieldSettings> settings = new ArrayList<>();
        settings.add(new FieldSettings(FieldSettings.Setting.NULLABLE, isNullable));
        settings.add(new FieldSettings(FieldSettings.Setting.AUTO_INCREMENT, autoIncrement));
        settings.add(new FieldSettings(FieldSettings.Setting.UNIQUE, uniqueColumn));
        settings.add(new FieldSettings(FieldSettings.Setting.PRIMARY_KEY, primaryKey));
        settings.add(new FieldSettings(FieldSettings.Setting.IDENTITY, isIdentity));
        return settings;
    }
}
