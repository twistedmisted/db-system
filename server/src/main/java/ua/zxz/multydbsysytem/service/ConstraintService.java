package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.table.Constraints;

public interface ConstraintService {

    Constraints getByTableId(Long tableId, String columnName, String username);
}
