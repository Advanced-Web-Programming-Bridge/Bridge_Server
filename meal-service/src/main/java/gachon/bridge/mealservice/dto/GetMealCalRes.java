package gachon.bridge.mealservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMealCalRes {
    private UUID mealIdx;
    private double allCal;
    private double morningCal;
    private double lunchCal;
    private double dinnerCal;

}
