package ua.zxz.multydbsysytem.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldSettings {

    private Setting value;
    private boolean statusValue;

    public FieldSettings(Setting value, String statusValue) {
        this.value = value;
        this.statusValue = value.isActiveWhen.equals(statusValue);
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    @RequiredArgsConstructor
    public enum Setting {
        NULLABLE("NOT NULL", "NO"),
        AUTO_INCREMENT("AUTO_INCREMENT", "YES"),
        PRIMARY_KEY("PRIMARY KEY", "YES"),
        UNIQUE("UNIQUE", "YES"),
        FOREIGN_KEY("FOREIGN KEY", "YES"),
        IDENTITY("IDENTITY", "YES");

        private final String value;
//        private final boolean isActiveWhen;
        private final String isActiveWhen;

        @JsonValue
        @Override
        public String toString() {
            return value;
        }
    }
}
