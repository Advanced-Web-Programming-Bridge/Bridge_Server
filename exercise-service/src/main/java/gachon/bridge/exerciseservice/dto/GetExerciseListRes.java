package gachon.bridge.exerciseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetExerciseListRes {
    private UUID exerciseIdx;
    private String exerciseDate;
    private String exerciseArea;
    private String exerciseName;
    private int exerciseGoal;
    private  int exerciseDone;
}
