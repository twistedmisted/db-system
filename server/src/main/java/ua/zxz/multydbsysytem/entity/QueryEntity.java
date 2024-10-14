package ua.zxz.multydbsysytem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "queries")
public class QueryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "query_name", nullable = false)
  private String queryName;

  @Column(name = "query", nullable = false)
  private String query;

  @ManyToOne(optional = false)
  @JoinColumn(name = "db_id", nullable = false)
  private DbEntity db;
}
