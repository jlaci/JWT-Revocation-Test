package hu.jlaci.jwt.cost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostResponse {
    private double ci;
    private double cv;
    private double cc;
    private double cd;
}
