package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.table.TableDto;
import ua.zxz.multydbsysytem.web.payload.table.CrateTablePayload;

import java.util.List;

public interface TableService {

    TableDto getTableById(Long id, String username);

    List<TableDto> getAllTablesByDb(Long dbId, String username);

    void create(CrateTablePayload table, String username);

    void update(TableDto tableDto, String username);

    void deleteById(Long tableId, String username);

    boolean hasRights(Long tableId, String username);
}
