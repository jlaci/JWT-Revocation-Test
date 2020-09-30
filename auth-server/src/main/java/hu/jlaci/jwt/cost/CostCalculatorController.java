package hu.jlaci.jwt.cost;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/costs")
@AllArgsConstructor
public class CostCalculatorController {

    private CostCalculatorService service;

    @GetMapping
    public CostResponse calculateCosts() {
        return service.calculateCosts();
    }
}
