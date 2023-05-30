package gachon.bridge.mealservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostMealReq {
    private double morningCal;
    private double lunchCal;
    private double dinnerCal;
}
