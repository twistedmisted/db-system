package ua.zxz.multydbsysytem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.QueryEntity;

@Repository
public interface QueryRepository extends CrudRepository<QueryEntity, Long> {

  boolean existsByDbIdAndQueryName(Long dbId, String queryName);
}
