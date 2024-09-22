package ua.zxz.multydbsysytem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.DbEntity;

@Repository
public interface DbRepository extends CrudRepository<DbEntity, Long> {

    Page<DbEntity> findAllByUserUsername(String username, Pageable pageable);

    boolean existsByNameAndUserId(final String name, final Long userId);

    boolean existsByNameAndUserUsername(final String name, final String username);

    boolean existsByIdAndUserUsername(final Long id, final String username);

    boolean existsByIdAndTablesName(long dbId, String table);
}
