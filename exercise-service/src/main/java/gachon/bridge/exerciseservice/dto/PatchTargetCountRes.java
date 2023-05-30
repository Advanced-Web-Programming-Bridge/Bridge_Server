package gachon.bridge.exerciseservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PatchTargetCountRes {
    private String exerciseIdx;
    private String exerciseDate;
    private int exerciseTargetCount;

    @Builder
    public PatchTargetCountRes(String exerciseIdx, String exerciseDate, int exerciseTargetCount) {
        this.exerciseIdx = exerciseIdx;
        this.exerciseDate = exerciseDate;
        this.exerciseTargetCount = exerciseTargetCount;
    }
}
