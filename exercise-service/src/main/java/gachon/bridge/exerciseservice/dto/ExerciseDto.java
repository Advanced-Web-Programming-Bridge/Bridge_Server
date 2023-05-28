package gachon.bridge.exerciseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseDto {
    private String category;
    private String name;
    private int goal;
}
