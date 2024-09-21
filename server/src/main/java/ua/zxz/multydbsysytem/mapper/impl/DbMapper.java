package ua.zxz.multydbsysytem.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.zxz.multydbsysytem.dto.DbDto;
import ua.zxz.multydbsysytem.entity.DbEntity;
import ua.zxz.multydbsysytem.mapper.Mapper;
import ua.zxz.multydbsysytem.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DbMapper implements Mapper<DbEntity, DbDto> {

    private final UserRepository userRepository;
    private final DbTokenMapper dbTokenMapper;

    @Override
    public DbEntity dtoToEntity(DbDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }
        DbEntity dbEntity = new DbEntity();
        dbEntity.setId(dto.getId());
        dbEntity.setName(dto.getName());
        dbEntity.setCreatedAt(dto.getCreatedAt());
        dbEntity.setStatus(dto.getStatus());
        dbEntity.setUser(userRepository.findByUsername(dto.getUser().getUsername()).get());
        return dbEntity;
    }

    @Override
    public DbDto entityToDto(DbEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        DbDto dto = new DbDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStatus(entity.getStatus());
        dto.setToken(dbTokenMapper.entityToDto(entity.getToken()));
        return dto;
    }

    @Override
    public List<DbEntity> dtosToEntities(List<DbDto> dtos) {
        if (Objects.isNull(dtos) || dtos.isEmpty()) {
            return new ArrayList<>();
        }
        List<DbEntity> entities = new ArrayList<>();
        for (DbDto dto : dtos) {
            entities.add(dtoToEntity(dto));
        }
        return entities;
    }

    @Override
    public List<DbDto> entitiesToDtos(List<DbEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<DbDto> dtos = new ArrayList<>();
        for (DbEntity entity : entities) {
            dtos.add(entityToDto(entity));
        }
        return dtos;
    }
}
