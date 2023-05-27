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
public class GetExerciseListDoneRes {
    private UUID exerciseIdx;
    private String exerciseDate;
    private String exerciseName;

}
