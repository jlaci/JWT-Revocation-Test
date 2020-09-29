package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.TestConfiguration;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("Novel")
@Service
@Slf4j
public class NovelAuthService extends AuthService {

    private List<String> secrets;
    private RestTemplate restTemplate;

    public NovelAuthService(UserService userService, RefreshTokenService refreshTokenService) {
        super(userService, refreshTokenService);
        this.restTemplate = new RestTemplate();
        this.secrets = new ArrayList<>(TestConfiguration.SystemCharacteristics.K);
        for(int i = 0; i < TestConfiguration.SystemCharacteristics.K; i++) {
            secrets.add("");
            regenerateSecret(i);
        }
    }

    @Override
    public void logout(Long userId, String accessToken) {
        refreshTokenService.deleteRefreshTokens(userId);
        regenerateSecret((int) (userId % TestConfiguration.SystemCharacteristics.K));
    }

    private void regenerateSecret(int clientGroup) {
        log.info("Secret changed for group {} notifying services.", clientGroup);
        secrets.set(clientGroup,  Util.getRandomString(48));
        notifyService("8081", clientGroup);
        notifyService("8082", clientGroup);
    }

    private void notifyService(String port, int clientGroup) {
        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + "/secret-change?group=" + clientGroup, secrets.get(clientGroup), Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Hibás kérés! Kód: " + response.getStatusCodeValue());
        }
    }

    protected String buildAccessToken(UserEntity user) {
        int clientGroup = (int) (user.getId() % TestConfiguration.SystemCharacteristics.K);
        log.info("User {} assigned to group {}", user, clientGroup);
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(Constants.JWT_CLAIM_USER_ID, user.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, user.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, user.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, secrets.get(clientGroup))
                .compact();
    }
}
