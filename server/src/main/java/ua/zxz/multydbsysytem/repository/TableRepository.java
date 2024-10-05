package ua.zxz.multydbsysytem.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.TableEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends CrudRepository<TableEntity, Long> {

    List<TableEntity> findAllByDbId(Long dbId);

    @Query("""
            SELECT COUNT(*) FROM TableEntity t
            JOIN DbEntity d ON d.id = t.db.id
            JOIN UserEntity u ON u.id = d.user.id
            WHERE u.username = :username AND t.id = :tableId""")
    int userHasAccessToTable(Long tableId, String username);

    Optional<TableEntity> findByNameAndDbId(String tableName, Long dbId);

    @Query("SELECT t.name FROM TableEntity t WHERE t.id = :id")
    String findNameById(Long id);

    boolean existsByIdAndDbUserUsername(Long tableId, String username);
}
