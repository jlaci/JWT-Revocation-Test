package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.Util;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRoleEntity;
import hu.jlaci.jwt.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("SecretChange")
@Slf4j
public class SecretChangeAuthService extends AuthService {

    private String currentSecret;
    private RestTemplate restTemplate;

    public SecretChangeAuthService(UserService userService, RefreshTokenService refreshTokenService) {
        super(userService, refreshTokenService);
        this.restTemplate = new RestTemplate();
        regenerateSecret();
    }

    @Override
    public void logout(Long userId, String accessToken) {
        refreshTokenService.deleteRefreshTokens(userId);
        regenerateSecret();
    }

    private void regenerateSecret() {
        log.info("Secret change notifying services.");
        currentSecret = Util.getRandomString(48);
        notifyService("8081");
        notifyService("8082");
    }

    private void notifyService(String port) {
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/secret-change", currentSecret, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Hibás kérés! Kód: " + response.getStatusCodeValue());
        }
    }

    protected String buildAccessToken(UserEntity user) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(Constants.JWT_CLAIM_USER_ID, user.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, user.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, user.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, currentSecret)
                .compact();
    }
}
