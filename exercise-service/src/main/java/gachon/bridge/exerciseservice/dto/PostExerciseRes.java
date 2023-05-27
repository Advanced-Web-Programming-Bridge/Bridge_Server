package gachon.bridge.exerciseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PostExerciseRes {
    private UUID exerciseIdx;
    private String exerciseDate;
    private String exerciseArea;
    private String exerciseName;
    private int exerciseGoal;
}
