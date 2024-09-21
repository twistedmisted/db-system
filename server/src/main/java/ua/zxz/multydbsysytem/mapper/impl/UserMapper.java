package ua.zxz.multydbsysytem.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxz.multydbsysytem.dto.UserDto;
import ua.zxz.multydbsysytem.entity.UserEntity;
import ua.zxz.multydbsysytem.mapper.Mapper;

import java.util.List;
import java.util.Objects;

@Component
public class UserMapper implements Mapper<UserEntity, UserDto> {

    @Override
    public UserEntity dtoToEntity(UserDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(dto.getId());
        userEntity.setFirstName(dto.getFirstName());
        userEntity.setLastName(dto.getLastName());
        userEntity.setUsername(dto.getUsername());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPassword(dto.getPassword());
        return userEntity;
    }

    @Override
    public UserDto entityToDto(UserEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword());
        return dto;
    }

    @Override
    public List<UserEntity> dtosToEntities(List<UserDto> dtos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<UserDto> entitiesToDtos(List<UserEntity> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
