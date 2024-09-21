package ua.zxz.multydbsysytem.service;

import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;

import java.util.function.Supplier;

public interface DbTokenService {

    void create(Long dbId, DbTokenLifeTime lifeTime, Supplier<Boolean> userHasRights);

    Long validateTokenAndGetDbId(final String token);

    boolean isTokenValid(String value);

    Object getById(Long dbId, String username);
}
