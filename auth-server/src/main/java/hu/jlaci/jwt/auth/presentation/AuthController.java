package hu.jlaci.jwt.auth.presentation;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    /**
     * The user authetnicates for the first time.
     * @return
     */
    @PostMapping("authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        Optional<AuthResponse> response = authService.authenticate(authRequest);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * The user requests a new token.
     * @return
     */
    @PostMapping("request-new-token")
    public ResponseEntity<AuthResponse> requestNewToken(@RequestParam String refreshToken) {
        Optional<AuthResponse> response = authService.requestNewToken(refreshToken);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * The user leaves the system.
     * @param userId
     * @return
     */
    @PostMapping("logout")
    public ResponseEntity logout(@RequestParam Long userId, @RequestParam(required = false) String accessToken) {
        authService.logout(userId, accessToken);
        return ResponseEntity.noContent().build();
    }

}
