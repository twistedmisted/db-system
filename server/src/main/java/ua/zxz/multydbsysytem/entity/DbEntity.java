package ua.zxz.multydbsysytem.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ua.zxz.multydbsysytem.dto.DbStatus;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "dbs")
public class DbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DbStatus status;

    @OneToOne(mappedBy = "db", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private DbTokenEntity token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "db", cascade = CascadeType.ALL)
    private List<TableEntity> tables = new ArrayList<>();

    public void addTable(TableEntity table) {
        table.setDb(this);
        tables.add(table);
    }

    public void removeTable(TableEntity table) {
        table.setDb(null);
        tables.remove(table);
    }
}