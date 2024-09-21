package ua.zxz.multydbsysytem.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.zxz.multydbsysytem.entity.redis.JwtHash;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtRepository extends CrudRepository<JwtHash, String> {

    Optional<JwtHash> findByToken(String token);

    List<JwtHash> findAllByUsername(String username);

    boolean existsByToken(String token);
}
