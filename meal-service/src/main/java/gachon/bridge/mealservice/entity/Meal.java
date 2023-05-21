package gachon.bridge.mealservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Meal")
@DynamicInsert
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mealIdx;

    @Column(name = "userIdx", nullable = false)
    private UUID userIdx;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "goal", nullable = false)
    @ColumnDefault("0")
    private double goal;

    @Column(name = "achieved", nullable = false)
    @ColumnDefault("false")
    private boolean achieved;

    @Column(name = "createdAt", nullable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updatedAt", nullable = false)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "status", nullable = false)
    @ColumnDefault("true")
    private boolean status;



}
