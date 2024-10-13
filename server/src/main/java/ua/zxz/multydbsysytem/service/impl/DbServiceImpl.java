package ua.zxz.multydbsysytem.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.zxz.multydbsysytem.dto.DbDto;
import ua.zxz.multydbsysytem.dto.PageDto;
import ua.zxz.multydbsysytem.entity.DbEntity;
import ua.zxz.multydbsysytem.exception.WrongDataException;
import ua.zxz.multydbsysytem.mapper.impl.DbMapper;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.service.DbService;
import ua.zxz.multydbsysytem.service.DbTokenService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbServiceImpl implements DbService {

    private final DbRepository dbRepository;
    private final DbMapper dbMapper;
    private final DbTokenService dbTokenService;
    private final JdbcTemplate jdbcTemplate;
    private final DataSourceService dataSourceService;

    @Override
    public DbDto getById(long id) {
        return dbMapper.entityToDto(dbRepository.findById(id)
                .orElseThrow(() -> new WrongDataException("Can't find a db by id = [" + id + "]")));
    }

    @Override
    public PageDto<DbDto> getAll(int pageNum, int pageSize, String username) {
        if (pageNum < 0 && pageSize < 0) {
            log.error("pageNum and pageSize can't be negative");
            throw new WrongDataException("pageNum and pageSize can't be negative");
        }
        Page<DbEntity> dbEntityPage = dbRepository.findAllByUserUsername(username, PageRequest.of(pageNum, pageSize));
        if (dbEntityPage.isEmpty()) {
            log.info("dbEntities is empty");
            return new PageDto<>(pageNum, dbEntityPage.getTotalPages());
        }
        return new PageDto<>(
                dbMapper.entitiesToDtos(dbEntityPage.getContent()),
                pageNum,
                dbEntityPage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void saveDb(DbDto dbDto) {
        if (existsByNameAndUsername(dbDto.getName(), dbDto.getUser().getUsername())) {
            log.warn("The user = [{}] already has db with name = [{}]", dbDto.getName(), dbDto.getUser().getUsername());
            throw new WrongDataException("The user already has db with name = [" + dbDto.getName() + "]");
        }
        DbEntity savedDbEntity = dbRepository.save(dbMapper.dtoToEntity(dbDto));
        String dbTechName = "db_" + savedDbEntity.getId();
        String roleName = "role_" + dbTechName;
        jdbcTemplate.batchUpdate(
                "CREATE DATABASE " + dbTechName + ";",
                "USE " + dbTechName + ";",
                "CREATE ROLE " + roleName + ";",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA SQLUser TO " + roleName + ";",
                "GRANT " + roleName + " TO " + dbDto.getUser().getUsername() + ";",
                "USE \"USER\";"
        );
        CompletableFuture.runAsync(() ->
                dbTokenService.create(savedDbEntity.getId(), dbDto.getToken().getLifeTime(), () -> true));
    }

    private boolean existsByNameAndUsername(String name, String username) {
        return dbRepository.existsByNameAndUserUsername(name, username);
    }

    @Override
    public void updateDb(DbDto dbDto) {
        if (!existsByNameAndUsername(dbDto.getName(), dbDto.getUser().getUsername())) {
            log.warn("The user = [{}] does not have db with name = [{}]", dbDto.getName(), dbDto.getUser().getUsername());
            throw new WrongDataException("The user does not have db with name = [" + dbDto.getName() + "]");
        }
        DbEntity dbEntity = dbRepository.findById(dbDto.getId()).get();
        dbEntity.setName(dbDto.getName());
        dbRepository.save(dbMapper.dtoToEntity(dbDto));
    }

    @Override
    public void removeDbById(long id, String username) {
        if (!userHasRightsToDb(id, username)) {
            throw new WrongDataException("Can't remove db");
        }
        jdbcTemplate.update("DROP DATABASE db_" + id + ";");
        dbRepository.deleteById(id);
        log.info("Removed db with id = [{}]", id);
    }

    public boolean userHasRightsToDb(long dbId, String username) {
        return dbRepository.existsByIdAndUserUsername(dbId, username);
    }

    @Override
    public boolean dbAlreadyHasTableWithName(long dbId, String table) {
        return dbRepository.existsByIdAndTablesName(dbId, table);
    }
}
