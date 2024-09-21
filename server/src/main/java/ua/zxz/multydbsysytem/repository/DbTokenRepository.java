package ua.zxz.multydbsysytem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.DbTokenEntity;

@Repository
public interface DbTokenRepository extends CrudRepository<DbTokenEntity, Long> {

    boolean existsByToken(String token);
}
