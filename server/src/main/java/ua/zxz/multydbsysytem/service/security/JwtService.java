package ua.zxz.multydbsysytem.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.zxz.multydbsysytem.entity.redis.JwtHash;
import ua.zxz.multydbsysytem.repository.redis.JwtRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class JwtService {

    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;
    private final Algorithm hmac512;
    private final JWTVerifier verifier;
    private final JwtRepository jwtRepository;

    public JwtService(@Value("${jwt.secret}") final String secret, JwtRepository jwtRepository) {
        this.hmac512 = Algorithm.HMAC512(secret);
        this.verifier = JWT.require(this.hmac512).build();
        this.jwtRepository = jwtRepository;
    }

    public String generateToken(final Authentication authenticate) {
        String jwtToken = JWT.create()
                .withSubject(authenticate.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .sign(this.hmac512);
        saveJwtTokenToDb(jwtToken, authenticate.getName());
        return jwtToken;
    }

    private void saveJwtTokenToDb(String jwtToken, String username) {
        JwtHash jwtTokenHash = JwtHash.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .token(jwtToken)
                .username(username)
                .build();
        jwtRepository.save(jwtTokenHash);
    }

    public String validateTokenAndGetUsername(final String token) {
        if (tokenNotExistsInDb(token)) {
            return null;
        }
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException e) {
            jwtRepository.delete(jwtRepository.findByToken(token).get());
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (tokenNotExistsInDb(token)) {
            return false;
        }
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean tokenNotExistsInDb(String token) {
        return !jwtRepository.existsByToken(token);
    }

    public void invalidateTokenByUsername(String username) {
        List<JwtHash> tokens = jwtRepository.findAllByUsername(username);
        jwtRepository.deleteAll(tokens);
    }

    public void invalidateTokenByUserToken(String token) {
        JwtHash jwtTokenHash = jwtRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Can't logout."));
        jwtRepository.delete(jwtTokenHash);
    }
}
