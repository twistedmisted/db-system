package ua.zxz.multydbsysytem.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.zxz.multydbsysytem.dto.DbStatus;
import ua.zxz.multydbsysytem.dto.DbTokenDto;
import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;
import ua.zxz.multydbsysytem.entity.DbTokenEntity;
import ua.zxz.multydbsysytem.mapper.impl.DbMapper;
import ua.zxz.multydbsysytem.mapper.impl.DbTokenMapper;
import ua.zxz.multydbsysytem.repository.DbRepository;
import ua.zxz.multydbsysytem.repository.DbTokenRepository;
import ua.zxz.multydbsysytem.service.DbTokenService;
import ua.zxz.multydbsysytem.web.payload.DbTokenPayload;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
public class DbTokenServiceImpl implements DbTokenService {

    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final DbTokenRepository dbTokenRepository;
    private final DbRepository dbRepository;
    private final DbMapper dbMapper;
    private final DbTokenMapper dbTokenMapper;

    public DbTokenServiceImpl(@Value("${db-token.secret}") String dbTokenSecret,
                              DbTokenRepository dbTokenRepository, DbRepository dbRepository, DbMapper dbMapper, DbTokenMapper dbTokenMapper) {
        this.hmac512 = Algorithm.HMAC512(dbTokenSecret);
        this.dbRepository = dbRepository;
        this.verifier = JWT.require(this.hmac512).build();
        this.dbTokenRepository = dbTokenRepository;
        this.dbMapper = dbMapper;
        this.dbTokenMapper = dbTokenMapper;
    }

//    @Transactional
//    @Override
//    public void create(Long dbId, DbTokenDto tokenDto) {
//        DbEntity dbEntity = dbRepository.findById(dbId).get();
//        DbTokenEntity dbEntityToken = new DbTokenEntity();
//        dbEntityToken.setToken(generateToken(dbEntity.getName(), tokenDto.getLifeTime().getLifeTime()));
//        dbEntityToken.setLifeTime(tokenDto.getLifeTime());
//        dbEntity.setToken(dbEntityToken);
//        dbEntity.setStatus(DbStatus.ACTIVE);
//        dbEntityToken.setDb(dbEntity);
//        dbTokenRepository.save(dbEntityToken);
//    }

    private String generateToken(Long dbId, long lifeTime) {
        return JWT.create()
                .withSubject(String.valueOf(dbId))
                .withExpiresAt(new Date(System.currentTimeMillis() + lifeTime))
                .sign(hmac512);
    }

    public void create(DbTokenPayload dbTokenPayload, String username) {
        create(dbTokenPayload.getDbId(),
                dbTokenPayload.getLifeTime(),
                () -> dbRepository.existsByIdAndUserUsername(dbTokenPayload.getDbId(), username));
    }

    @Override
    public void create(Long dbId, DbTokenLifeTime lifeTime, Supplier<Boolean> userHasRights) {
        if (!userHasRights.get()) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't update db token, something went wrong");
        }
        DbTokenEntity dbTokenEntity = dbTokenRepository.findById(dbId).orElseGet(() -> {
            DbTokenEntity t = new DbTokenEntity();
            t.setDbId(dbId);
            t.setDb(dbRepository.findById(dbId).get());
            return t;
        });
        String token = generateToken(dbTokenEntity.getDb().getId(), lifeTime.getLifeTime());
        dbTokenEntity.setToken(token);
        dbTokenEntity.setLifeTime(lifeTime);
        dbTokenEntity.getDb().setStatus(DbStatus.ACTIVE);
        dbTokenEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        dbTokenRepository.save(dbTokenEntity);
    }

    @Override
    public Long validateTokenAndGetDbId(final String token) {
        if (tokenNotExistInDb(token)) {
            return null;
        }
        try {
            return Long.valueOf(verifier.verify(token).getSubject());
        } catch (final JWTVerificationException e) {
            return null;
        }
    }

    private boolean tokenNotExistInDb(final String token) {
        return !dbTokenRepository.existsByToken(token);
    }

    @Override
    public boolean isTokenValid(String value) {
        try {
            verifier.verify(value);
            return true;
        } catch (final JWTVerificationException e) {
            return false;
        }
    }

    @Override
    public DbTokenDto getById(Long dbId, String username) {
        if (!dbRepository.existsByIdAndUserUsername(dbId, username)) {
            throw new ResponseStatusException(BAD_REQUEST, "Can't get db token, something went wrong");
        }
        return dbTokenMapper.entityToDto(dbTokenRepository.findById(dbId)
                .orElseThrow(() ->
                        new ResponseStatusException(BAD_REQUEST, "Can't get db token, something went wrong")));
    }
}
