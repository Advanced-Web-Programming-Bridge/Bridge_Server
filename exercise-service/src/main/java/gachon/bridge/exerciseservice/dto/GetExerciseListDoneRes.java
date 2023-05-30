package gachon.bridge.exerciseservice.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class GetExerciseListDoneRes {
    private String exerciseDate;
    private List<ExerciseDoneListDto> exercise;

    @Builder
    public GetExerciseListDoneRes(String exerciseDate, List<ExerciseDoneListDto> exercise) {
        this.exerciseDate = exerciseDate;
        this.exercise = exercise;
    }
}
