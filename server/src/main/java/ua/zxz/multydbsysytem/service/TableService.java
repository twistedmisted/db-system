package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.TableDto;
import ua.zxz.multydbsysytem.web.payload.TablePayload;

import java.util.List;

public interface TableService {

    TableDto getTableById(Long id, String username);

    List<TableDto> getAllTablesByDb(Long dbId, String username);

    void create(TablePayload table, String username);

    void update(TableDto tableDto, String username);

    void deleteById(Long tableId, String username);
}
