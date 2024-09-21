package ua.zxz.multydbsysytem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tables")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

//    @Column(name = "tech_name", nullable = false)
//    private String techName;

    @ManyToOne
    @JoinColumn(name = "db_id", nullable = false)
    private DbEntity db;
}
