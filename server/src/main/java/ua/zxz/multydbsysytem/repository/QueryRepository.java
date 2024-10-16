package ua.zxz.multydbsysytem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.QueryEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueryRepository extends CrudRepository<QueryEntity, Long> {

  List<QueryEntity> findAllByDbId(Long dbId);

  boolean existsByDbIdAndQueryName(Long dbId, String queryName);

  @Query("SELECT q.query FROM QueryEntity q WHERE q.db.id = :dbId AND q.queryName = :queryName")
  Optional<String> getQueryByDbIdAndQueryName(Long dbId, String queryName);
}
