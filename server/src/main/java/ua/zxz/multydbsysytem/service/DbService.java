package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.DbDto;
import ua.zxz.multydbsysytem.dto.PageDto;
import ua.zxz.multydbsysytem.web.payload.DbUpdateRequest;

public interface DbService {

    DbDto getById(long id);

    PageDto<DbDto> getAll(int pageNum, int pageSize, String username);

    void saveDb(DbDto dbDto);

    void updateDb(DbDto dbDto);

    void removeDbById(long id, String username);

    boolean userHasRightsToDb(long dbId, String username);

    boolean dbAlreadyHasTableWithName(long dbId, String table);

  void updateDb(Long id, DbUpdateRequest dbCreateRequest, String name);
}
