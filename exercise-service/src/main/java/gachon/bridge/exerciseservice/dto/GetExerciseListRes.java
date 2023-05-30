package gachon.bridge.exerciseservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetExerciseListRes {
    private String exerciseDate;
    private List<ExerciseListDto> exercise;

    @Builder
    public GetExerciseListRes(String exerciseDate, List<ExerciseListDto> exercise) {
        this.exerciseDate = exerciseDate;
        this.exercise = exercise;
    }
}
