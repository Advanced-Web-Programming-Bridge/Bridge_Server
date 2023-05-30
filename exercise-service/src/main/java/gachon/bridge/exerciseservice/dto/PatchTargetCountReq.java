package gachon.bridge.exerciseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchTargetCountReq {
    private String userIdx;
    private String exerciseIdx;
    private int exerciseTargetCount;
}
