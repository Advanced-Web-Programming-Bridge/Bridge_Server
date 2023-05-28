package gachon.bridge.exerciseservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class GetExerciseDto {
    private String name;
    private int goal;
    private int done;

    @Builder
    public GetExerciseDto(String name, int goal, int done) {
        this.name = name;
        this.goal = goal;
        this.done = done;
    }
}
