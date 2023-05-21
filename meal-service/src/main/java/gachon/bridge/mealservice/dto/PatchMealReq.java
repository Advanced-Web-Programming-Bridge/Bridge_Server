package gachon.bridge.mealservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMealReq {
    private double morningCal;
    private double lunchCal;
    private double dinnerCal;
}
