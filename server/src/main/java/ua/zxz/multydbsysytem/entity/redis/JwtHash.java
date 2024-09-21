package ua.zxz.multydbsysytem.entity.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serial;
import java.io.Serializable;

@RedisHash("JWT")
@Setter
@Getter
@Builder
public class JwtHash implements Serializable {

    @Serial
    private static final long serialVersionUID = -501702715163284257L;

    @Id
    private String id;

    @Indexed
    private String token;

    @Indexed
    private String username;
}
