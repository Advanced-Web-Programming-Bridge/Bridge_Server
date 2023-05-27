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
@Table(name = "Calories")
@DynamicInsert
@NoArgsConstructor
public class Calories {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID calorieIdx;

    @ManyToOne
    @JoinColumn(name = "meal_idx")
    private Meal mealIdx;

    @Column(name = "calories", nullable = false)
    private double calories;

    @Column(name = "time", nullable = false)
    @Enumerated(EnumType.STRING)
    private MealTime time;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "status", nullable = false)
    @ColumnDefault("true")
    private boolean status;

    @Builder
    public Calories(Meal mealIdx, double calories, MealTime time) {
        this.mealIdx = mealIdx;
        this.calories = calories;
        this.time = time;
    }

    public double getCalories() {
        return calories;
    }

    public MealTime getTime() {
        return time;
    }
}
