package gachon.bridge.mealservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Meal")
@DynamicInsert
@NoArgsConstructor
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
    @ColumnDefault("1")
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

    @Builder
    public Meal(UUID userIdx, Date date, double goal, boolean achieved) {
        this.userIdx = userIdx;
        this.date = date;
        this.goal = goal;
        this.achieved = achieved;
    }

    public UUID getMealIdx() {
        return mealIdx;
    }

    public void updateMealGoal(double targetGoal){
        this.goal = targetGoal;
    }
}
