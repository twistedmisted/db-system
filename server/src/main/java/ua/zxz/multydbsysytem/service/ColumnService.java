package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.FieldDto;
import ua.zxz.multydbsysytem.web.payload.TablePayload;

import java.security.Principal;
import java.util.stream.Collectors;

public interface ColumnService {

    void addNewColumn(Long tableId, TablePayload.Column column, String username);

    void deleteColumn(Long tableId, String columnName, String username);

    static String mapToSqlColumn(TablePayload.Column column) {
        String sqlConstraints = column.getSettings().stream()
//                .filter(s -> s.getValue().isActiveWhen() == s.isStatusValue())
                .map(s -> s.getValue().toString())
                .collect(Collectors.joining(" "));
        return column.getName() + " " + column.getType() + " " + sqlConstraints;
    }

    FieldDto getColumnByName(Long tableId, String columnName, String username);
}
