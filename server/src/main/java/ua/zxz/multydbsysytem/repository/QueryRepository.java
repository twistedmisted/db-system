package ua.zxz.multydbsysytem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.QueryEntity;

import java.util.List;

@Repository
public interface QueryRepository extends CrudRepository<QueryEntity, Long> {

  List<QueryEntity> findAllByDbId(Long dbId);

  boolean existsByDbIdAndQueryName(Long dbId, String queryName);
}
