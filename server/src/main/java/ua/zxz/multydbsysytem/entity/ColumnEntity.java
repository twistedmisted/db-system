//package ua.zxz.multydbsysytem.entity;
//
//import jakarta.persistence.CollectionTable;
//import jakarta.persistence.Column;
//import jakarta.persistence.ElementCollection;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.Getter;
//import lombok.Setter;
//import ua.zxz.multydbsysytem.dto.ColumnConstraint;
//import ua.zxz.multydbsysytem.dto.ColumnType;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "columns")
//public class ColumnEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "type", nullable = false)
//    @Enumerated(EnumType.STRING)
//    private ColumnType type;
//
//    @ElementCollection
//    @CollectionTable(name = "constraints")
//    private Set<ColumnConstraint> constraints = new HashSet<>();
//}
