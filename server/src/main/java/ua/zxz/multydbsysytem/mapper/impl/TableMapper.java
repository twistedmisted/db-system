package ua.zxz.multydbsysytem.mapper.impl;

import org.springframework.stereotype.Component;
import ua.zxz.multydbsysytem.dto.TableDto;
import ua.zxz.multydbsysytem.entity.TableEntity;
import ua.zxz.multydbsysytem.mapper.Mapper;
import ua.zxz.multydbsysytem.web.payload.TableResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TableMapper implements Mapper<TableEntity, TableDto> {

    @Override
    public TableEntity dtoToEntity(TableDto dto) {
        return null;
    }

    @Override
    public TableDto entityToDto(TableEntity entity) {
        return null;
    }

    @Override
    public List<TableEntity> dtosToEntities(List<TableDto> dtos) {
        return List.of();
    }

    @Override
    public List<TableDto> entitiesToDtos(List<TableEntity> entities) {
        return List.of();
    }

    public TableResponse entityToResponse(TableEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        TableResponse response = new TableResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }

    public List<TableResponse> entitiesToResponse(List<TableEntity> entities) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<TableResponse> responses = new ArrayList<>();
        for (TableEntity entity : entities) {
            responses.add(entityToResponse(entity));
        }
        return responses;
    }
}
