package hu.jlaci.jwt.bar;

import hu.jlaci.jwt.ServiceResponse;
import hu.jlaci.jwt.validation.TokenValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BarController {

    private final TokenValidator tokenValidator;

    @GetMapping("/bar")
    public ResponseEntity<ServiceResponse> bar(@RequestHeader(name = "Authorization", required = false) String accessToken) {
        if (accessToken != null && tokenValidator.isValid(accessToken)) {
            return ResponseEntity.ok(ServiceResponse.randomResponse());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
