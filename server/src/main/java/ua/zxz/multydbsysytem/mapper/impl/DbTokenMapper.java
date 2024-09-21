package ua.zxz.multydbsysytem.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxz.multydbsysytem.dto.DbTokenDto;
import ua.zxz.multydbsysytem.entity.DbTokenEntity;
import ua.zxz.multydbsysytem.mapper.Mapper;

import java.util.List;
import java.util.Objects;

@Component
public class DbTokenMapper implements Mapper<DbTokenEntity, DbTokenDto> {

    @Override
    public DbTokenEntity dtoToEntity(DbTokenDto dto) {
        return null;
    }

    @Override
    public DbTokenDto entityToDto(DbTokenEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        DbTokenDto dto = new DbTokenDto();
        dto.setDbId(entity.getDbId());
        dto.setToken(entity.getToken());
        dto.setLifeTime(entity.getLifeTime());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Override
    public List<DbTokenEntity> dtosToEntities(List<DbTokenDto> dtos) {
        return List.of();
    }

    @Override
    public List<DbTokenDto> entitiesToDtos(List<DbTokenEntity> entities) {
        return List.of();
    }
}
