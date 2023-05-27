package gachon.bridge.exerciseservice.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "Exercise")
@DynamicInsert
@NoArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID exerciseIdx;

    @Column(name = "userIdx", nullable = false)
    private UUID userIdx;

    @Column(name = "exercise_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date exercise_date;

    @Column(name = "exercise_area", nullable = false)
    private String exercise_area;

    @Column(name = "exercise_name", nullable = false)
    private String exercise_name;

    @Column(name = "exercise_target_count", nullable = false)
    private int exercise_target_count;

    @Column(name = "exercise_did_count", nullable = false)
    private int exercise_did_count;

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
    public Exercise(UUID userIdx, Date exercise_date, String exercise_area, String exercise_name, int exercise_target_count, int exercise_did_count, boolean achieved) {
        this.userIdx = userIdx;
        this.exercise_date = exercise_date;
        this.exercise_area = exercise_area;
        this.exercise_name = exercise_name;
        this.exercise_target_count = exercise_target_count;
        this.exercise_did_count = exercise_did_count;
        this.achieved = achieved;
    }

    public UUID getExerciseIdx() { return exerciseIdx; }

    public void updateExerciseGoal(int targetGoal) { this.exercise_target_count =targetGoal; }

    public void updateExerciseDone(int done) { this.exercise_did_count = done; }




}
