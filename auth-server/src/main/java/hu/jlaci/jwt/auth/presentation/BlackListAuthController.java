package hu.jlaci.jwt.auth.presentation;

import hu.jlaci.jwt.auth.service.BlackListAuthService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Profile("BlackList")
public class BlackListAuthController {

    private BlackListAuthService blackListAuthService;

    @PostMapping("/is-blacklisted")
    public ResponseEntity<Boolean> isBlacklisted(@RequestParam String accessToken) {
        return ResponseEntity.ok(blackListAuthService.isBlacklisted(accessToken));
    }

}
