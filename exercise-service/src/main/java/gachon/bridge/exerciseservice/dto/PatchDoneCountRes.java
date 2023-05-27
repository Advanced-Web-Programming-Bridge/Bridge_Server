package gachon.bridge.exerciseservice.dto;

import lombok.*;

import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class PatchDoneCountRes {
    private String exerciseIdx;
    private String exerciseDate;
    private int exerciseDoneCount;

    @Builder
    public PatchDoneCountRes(String exerciseIdx, String exerciseDate, int exerciseDoneCount) {
        this.exerciseIdx = exerciseIdx;
        this.exerciseDate = exerciseDate;
        this.exerciseDoneCount = exerciseDoneCount;
    }
}
