package ua.zxz.multydbsysytem.dto.table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@RequiredArgsConstructor
public enum Constraints {
    PRIMARY_KEY("PRIMARY KEY", "YES"),
    IDENTITY("IDENTITY", "YES"),
    AUTO_INCREMENT("AUTO_INCREMENT", "YES"),
    NULLABLE("NOT NULL", "NO"),
    UNIQUE("UNIQUE", "YES"),
    FOREIGN_KEY("FOREIGN KEY", "YES");

    private final String value;
    //        private final boolean isActiveWhen;
    private final String isActiveWhen;

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
