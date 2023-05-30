package gachon.bridge.exerciseservice.dto;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class PostExerciseRes {
    private String exerciseIdx;
    private String exerciseDate;

    @Builder
    public PostExerciseRes(String exerciseIdx, String exerciseDate) {
        this.exerciseIdx = exerciseIdx;
        this.exerciseDate = exerciseDate;
    }
}
