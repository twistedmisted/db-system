package ua.zxz.multydbsysytem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.zxz.multydbsysytem.dto.DbTokenLifeTime;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "db_tokens")
public class DbTokenEntity {

    @Id
    @Column(name = "db_id", nullable = false)
    private Long dbId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "life_time", nullable = false)
    @Enumerated(EnumType.STRING)
    private DbTokenLifeTime lifeTime;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToOne(cascade = CascadeType.MERGE)
    @MapsId
    @JoinColumn(name = "db_id")
    private DbEntity db;
}
